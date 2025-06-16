import cats.effect.*
import fs2.*
import fs2.concurrent.*
import cats.syntax.all.*
import doodle.core.Color
import doodle.java2d.*
import doodle.interact.syntax.all.*
import doodle.interact.*
import doodle.core.*
import fs2.concurrent.SignallingRef
import scala.concurrent.duration.DurationInt

trait GameApp[S, C] extends IOApp.Simple {

  def game: IO[Game[S, C]]

  def run: IO[Unit] = game.flatMap { game =>
    // make frame for rendering
    val frame = Frame.default.withSize(600, 600).withBackground(Color.paleGreen)

    // build signalling ref for the game state
    SignallingRef.of[IO, S](game.init).flatMap { stateSignal =>

      // render loop that continuously renders the game state
      val renderLoop = stateSignal.continuous
        .map(game.render)
        .metered(20.millis)

      // actions stream that reads input, processes commands, and updates the state
      val actions = fs2.io
        .stdinUtf8[IO](1024)
        .map(_.trim)
        .mapFilter(game.input)
        .map(game.action(_, stateSignal))
        .parJoinUnbounded

      // run the render loop, actions, and simulation concurrently
      renderLoop
        .concurrently(actions)
        .concurrently(game.simulation(stateSignal))
        .animateToIO(frame)
    }
  }
}
