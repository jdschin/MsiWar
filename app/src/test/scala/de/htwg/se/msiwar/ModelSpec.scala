package de.htwg.se.msiwar

import de.htwg.se.msiwar.model.GameModelImpl
import de.htwg.se.msiwar.util.GameConfigProvider
import org.scalatest.{FlatSpec, Matchers}

class ModelSpec extends FlatSpec with Matchers {

  "GameModelImpl" should "return turn counter of 1 at game start" in {
    val model = GameModelImpl(GameConfigProvider.rowCount, GameConfigProvider.colCount, GameConfigProvider.gameObjects, GameConfigProvider.levelBackgroundImagePath, GameConfigProvider.actionbarBackgroundImagePath, GameConfigProvider.attackImagePath, GameConfigProvider.attackSoundPath, GameConfigProvider.openingBackgroundImagePath)
    model.turnCounter should be(1)
  }
}