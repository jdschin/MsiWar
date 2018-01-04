package de.htwg.se.msiwar

import de.htwg.se.msiwar.controller.ControllerImpl
import de.htwg.se.msiwar.model.GameModelImpl
import de.htwg.se.msiwar.util.GameConfigProviderImpl
import org.scalatest.{FlatSpec, Matchers}

class ControllerSpec extends FlatSpec with Matchers {

  "ControllerImpl" should "return turn counter of 1 at game start" in {
    val controller = new ControllerImpl(GameModelImpl(new GameConfigProviderImpl))
    controller.turnCounter should be (1)
  }
}