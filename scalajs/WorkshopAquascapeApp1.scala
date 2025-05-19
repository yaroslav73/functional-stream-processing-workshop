import aquascape.*
import aquascape.drawing.*
trait WorkshopAquascapeApp1 extends AquascapeApp {
  override def config: Config = super.config.scale(4)
  override def name: String = "aquascape"
}
