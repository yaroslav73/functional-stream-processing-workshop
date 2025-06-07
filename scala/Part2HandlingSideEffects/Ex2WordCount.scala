import cats.effect.*
import cats.implicits.toTraverseOps
import fs2.*
import fs2.io.file.{Files, Path}

// Counts the total number of words in all books in the `data` directory
object Ex2WordCount extends IOApp.Simple {

  // Use the files API to read UTF8 strings and split them into words
  def readBook(title: Path): Stream[IO, String] =
    Files[IO].readUtf8Lines(title)

  def readBookSafe(title: Path): Stream[IO, Either[Throwable, String]] =
    Files[IO].readUtf8Lines(title).attempt

  def countWords(book: Stream[IO, String]): Stream[IO, Long] =
    book.filterNot(w => w.isBlank || w.isBlank).map(_ => 1L).foldMonoid

  def countWordsSafe(
      book: Stream[IO, Either[Throwable, String]]
  ): Stream[IO, Long] =
    book
      .collect { case Right(words) => words }
      .filterNot(w => w.isBlank || w.isBlank)
      .map(_ => 1L)
      .foldMonoid

  def countWordsInBook(title: Path): Stream[IO, Long] =
    countWords(readBook(title))

  def countWordsInBookSafe(title: Path): Stream[IO, Long] =
    countWordsSafe(readBookSafe(title))

  def countWordsInBooks(titles: Stream[IO, Path]): Stream[IO, Long] =
    titles
      .map(title => countWordsInBook(title))
      .parJoinUnbounded
      .foldMonoid

  def countWordsInBooksSafe(titles: Stream[IO, Path]): Stream[IO, Long] =
    titles
      .map(title => countWordsInBookSafe(title))
      .parJoinUnbounded
      .foldMonoid

  // Use the files API to list the contents of the data directory
  def titles: Stream[IO, Path] =
    Files[IO]
      .list(
        Path(
          "/Users/yaroslav/Documents/learning/functional-stream-processing-workshop/data"
        )
      )
      .filter(_.extName.endsWith("txt"))

  def run: IO[Unit] = {
    titles
      .map { path =>
        countWordsInBookSafe(path).map { words =>
          s"Book ${path.fileName.toString} has $words words"
        }
      }
      .parJoinUnbounded
      .compile
      .toList
      .flatMap { results =>
        results.traverse(IO.println)
      } *>
      countWordsInBooksSafe(titles).compile.last.flatMap(result =>
        IO.println(s"The total word count was $result")
      )
  }
}
