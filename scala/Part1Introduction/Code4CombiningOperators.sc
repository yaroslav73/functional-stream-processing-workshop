import fs2.*

val helloWords = Stream("Hello", "Cádiz")
val goodbyeWords = Stream("Goodbye", "London")

/** ++ is an alias for append */
(helloWords ++ goodbyeWords).compile.toList

helloWords.zip(goodbyeWords).compile.toList

helloWords.interleave(goodbyeWords).compile.toList
helloWords
  .flatMap(helloWord =>
    goodbyeWords.map(goodbyeWord => s"$helloWord-$goodbyeWord")
  )
  .compile
  .toList

val stream1 = Stream(1, 2, 3)
val stream2 = Stream(4, 5, 6, 7, 8, 9)

stream1
  .zip(stream2)
  .compile
  .toList
