package de.htwg.se.msiwar

import de.htwg.se.msiwar.model.ActionType.{MOVE, SHOOT, WAIT}
import de.htwg.se.msiwar.model._
import de.htwg.se.msiwar.util.{Direction, GameConfigProvider}

class TestConfigProvider extends GameConfigProvider {
  private var actions:List[Action] = List[Action]()

  // Global sounds
  var attackSoundPath = "sounds/explosion.wav"
  // Global images
  var openingBackgroundImagePath = "images/background_opening.png"
  var levelBackgroundImagePath = "images/background_woodlands.png"
  var actionbarBackgroundImagePath = "images/background_actionbar.png"

  var attackImagePath = "images/hit.png"
  // Setup board
  var rowCount = 10
  var colCount = 10
  var gameObjects: List[GameObject] = List[GameObject]()

  override def loadFromFile(configFilePath: String): Unit = {
    // Nothing to do here
  }

  def load2PlayerEmptyMapScenario(): Unit = {
     rowCount = 10
     colCount = 2

    // Setup actions
    val moveAction = Action(1, "Panzer bewegen", "images/action_move.png", "move.wav", 1, 1, MOVE, 0)
    val shootAction = Action(2, "Schießen", "images/action_attack.png", "shoot.wav", 1, 3, SHOOT, 2)
    val waitAction = Action(3, "Warten", "images/action_wait.png", "shoot.wav", 1, 1, WAIT, 2)
    actions = List(moveAction, shootAction, waitAction)

    // Setup players
    val player1 = PlayerObject("Spieler1", "images/light_tank_red.png", Position(0, 0), Direction.DOWN, 1, "images/background_won_red.png", 3, 3, actions)
    val player2 = PlayerObject("Spieler2", "images/medium_tank_blue.png", Position(0, 1), Direction.DOWN, 2, "images/background_won_blue.png", 3, 3, actions)

    gameObjects = List(player1, player2)
  }

  def loadInstantWinScenario(): Unit = {
    rowCount = 1
    colCount = 1
    gameObjects = List(PlayerObject("Winner", "images/light_tank_red.png", Position(0, 0), Direction.DOWN, 1, "images/background_won_red.png", 3, 3, List()))
  }

  override def listScenarios: List[String] = {
    List[String]()
  }
}
