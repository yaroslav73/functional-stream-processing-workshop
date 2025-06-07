import cats.effect.{IO, IOApp}
import cats.implicits.catsSyntaxSemigroup
import fs2.*

object Playground extends IOApp.Simple {
  def run: IO[Unit] =
    // Empty streams
    IO.println(s"Empty stream: ${Stream.empty}") *>
      IO.println(s"Empty stream compiled: ${Stream.empty.compile}") *>
      IO.println(s"Empty stream compiled last: ${Stream.empty.compile.last}") *>
      IO.println(s"Empty stream to list: ${Stream.empty.toList}") *>
      IO.println(s"Empty stream compiled count: ${Stream.empty.compile.count}")

    // Unit streams
    IO.println(s"Unit stream: ${Stream.unit}") *>
      IO.println(s"Unit stream compiled: ${Stream.unit.compile}") *>
      IO.println(s"Unit stream compiled last: ${Stream.unit.compile.last}") *>
      IO.println(s"Unit stream to list: ${Stream.unit.toList}") *>
      IO.println(s"Unit stream compiled count: ${Stream.unit.compile.count}")

    // String stream
    val emptyStringStream = Stream[Pure, String]().compile.count
    IO.println(s"String stream compiled count: $emptyStringStream")

    // Infinite streams
    val intInfiniteStream = Stream(1).repeat.take(2).compile.count
    IO.println(s"Int infinite stream compiled count: $intInfiniteStream")

//    val emptyInfiniteStream = Stream.empty.repeat.take(2).compile.count
//    IO.println(s"Empty (Nothing) infinite stream compiled count: $emptyInfiniteStream")

    val unitInfiniteStream = Stream.unit.repeat.take(2).compile.count
    IO.println(s"Unit infinite stream compiled count: $unitInfiniteStream")

    // Mental model of streams
    Stream(1) // Stream(1) stage
      .repeat // repeat stage
      .take(2) // take(2) stage
      .compile // compile stage
      .count // count stage

    IO.println(
      Stream(1, 2, 3)
        .take(2)
        .drop(1)
        .compile
        .toList
    )

    // IO.unit

    val greetings1 = IO(IO.println("Hello1!"))
    val greetings2 = IO(IO.println("Hello2!"))
    val greetTwiceNotWork = greetings1 *> greetings2
    val greetTwiceWork = greetings1 |+| greetings2

    greetTwiceNotWork.flatten
    greetTwiceWork.flatten
}
