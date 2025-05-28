import cats.effect.{IO, IOApp}
import fs2.*

object Puzzlers extends IOApp.Simple {
  def run: IO[Unit] =
    // What is the difference between Stream('a').repeat.take(1) and Stream('a').take(1).repeat?
    def stream1 = Stream('a').repeat.take(1)
    def stream2 = Stream('a').take(1).repeat

    // stream1 will produce infinite 'a's, but only one element will be taken.
    // stream2 will produce one 'a' and then repeat that single element infinitely.

    //What is the output of Stream.empty.repeat.take(1).compile.toList?
    def emptyStream = Stream.empty.repeat.take(1).compile.toList

    // Looks like it will be infinite not evaluated stream,
    // as Stream.empty is Stream[Pure, Nothing] and will not produce any elements.
    // Also the body of Stream.empty is:
    // val empty: Stream[Pure, Nothing] = Pull.done.streamNoScope
    // Interpret this Pull to produce a Stream without introducing a scope.
    // What is a scope?

    IO.unit
}
