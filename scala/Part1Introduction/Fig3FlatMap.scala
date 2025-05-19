import fs2.*
import cats.effect.IO
import aquascape.*

object Fig3FlatMap extends WorkshopAquascapeApp1 {
  def stream(using Scape[IO]): IO[Unit] = {
    Stream("ab", "cd")
      .stage("Stream(ab, cd)")
      .flatMap(str => Stream.emits(str.toList).stage(s"Stream(${str}.toList)"))
      .stage("flatMap(…)")
      .compile
      .toList
      .compileStage("compile.toList")
      .void
  }
}
