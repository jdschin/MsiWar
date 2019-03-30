package de.htwg.se.msiwar

import java.io.FileNotFoundException

import de.htwg.se.msiwar.model.ActionType.{MOVE, SHOOT, WAIT}
import de.htwg.se.msiwar.model._
import de.htwg.se.msiwar.util.{Direction, GameConfigProvider}

class TestConfigProvider extends GameConfigProvider {
  private var actions:List[Action] = List[Action]()

  val testScenario1 = "S1_Scenario_(2-Player).json"
  val testScenario2 = "S2_Scenario_(3-Player).json"

  // Global sounds
  var attackSoundPath = "sounds/explosion.wav"
  // Global images
  var openingBackgroundImagePath = "images/background_opening.png"
  var levelBackgroundImagePath = "images/background_woodlands.png"
  var actionbarBackgroundImagePath = "images/background_actionbar.png"
  var appIconImagePath = "images/app_icon.png"

  var attackImagePath = "images/hit.png"
  // Setup board
  var rowCount = 10
  var colCount = 10
  var gameObjects: List[GameObject] = List[GameObject]()

  override def loadFromFile(configFilePath: String): GameConfigProvider = {
    if(configFilePath.isEmpty) {
      throw new FileNotFoundException("No file found for path " + configFilePath)
    }
    this
  }

  def load2PlayerDamageTestScenario(): Unit = {
    rowCount = 2
    colCount = 1

    val shootAction = Action(id=2, "Shoot", "images/action_attack.png", "shoot.wav", actionPoints=1, range=3, SHOOT, damage=2)
    actions = List(shootAction)

    // Setup players
    val player1 = PlayerObject("Player1", "images/light_tank_red.png", Position(0, 0), Direction.DOWN, playerNumber=1, "images/background_won_red.png", actionPoints=3, maxActionPoints=3, healthPoints=3, maxHealthPoints=3, actions)
    val player2 = PlayerObject("Player2", "images/medium_tank_blue.png", Position(1, 0), Direction.UP, playerNumber=2, "images/background_won_blue.png", actionPoints=3, maxActionPoints=3, healthPoints=3, maxHealthPoints=3, actions)

    gameObjects = List(player1, player2)
  }

  def load3PlayerTestScenario(): Unit = {
    rowCount = 3
    colCount = 3

    val shootAction = Action(id=2, "Shoot", "images/action_attack.png", "shoot.wav", actionPoints=1, range=3, SHOOT, damage=3)
    actions = List(shootAction)

    // Setup players
    val player1 = PlayerObject("Player1", "images/light_tank_red.png", Position(0, 0), Direction.DOWN, playerNumber=1, "images/background_won_red.png", actionPoints=3, maxActionPoints=3, healthPoints=3, maxHealthPoints=3, actions)
    val player2 = PlayerObject("Player2", "images/medium_tank_blue.png", Position(2, 0), Direction.UP, playerNumber=2, "images/background_won_blue.png", actionPoints=3, maxActionPoints=3, healthPoints=3, maxHealthPoints=1, actions)
    val player3 = PlayerObject("Player3", "images/medium_tank_blue.png", Position(2, 1), Direction.UP, playerNumber=3, "images/background_won_blue.png", actionPoints=3, maxActionPoints=3, healthPoints=3, maxHealthPoints=3, actions)

    val wood = BlockObject("B", "images/block_wood.png", Position(1, 1))

    gameObjects = List(player1, player2,player3, wood)
  }

  def load2PlayerSmallMapScenario(): Unit = {
    rowCount = 3
    colCount = 4

    val shootAction = Action(id=2, "Shoot", "images/action_attack.png", "shoot.wav", actionPoints=1, range=3, SHOOT, damage=2)
    actions = List(shootAction)

    // Setup players
    val player1 = PlayerObject("Player1", "images/light_tank_red.png", Position(1, 1), Direction.DOWN, playerNumber=1, "images/background_won_red.png", actionPoints=3, maxActionPoints=3, healthPoints=3, maxHealthPoints=3, actions)
    val player2 = PlayerObject("Player2", "images/medium_tank_blue.png", Position(1, 2), Direction.UP, playerNumber=2, "images/background_won_blue.png", actionPoints=3, maxActionPoints=3, healthPoints=3, maxHealthPoints=3, actions)

    val wood1 = BlockObject("B", "images/block_wood.png", Position(0, 0))
    val wood2 = BlockObject("B", "images/block_wood.png", Position(0, 1))
    val wood3 = BlockObject("B", "images/block_wood.png", Position(0, 2))
    val wood4 = BlockObject("B", "images/block_wood.png", Position(0, 3))

    gameObjects = List(player1, player2, wood1, wood2, wood3, wood4)
  }

  def load2PlayerEmptyMapScenario(): Unit = {
     rowCount = 10
     colCount = 2

    // Setup actions
    val moveAction = Action(id=1, "Move", "images/action_move.png", "move.wav", actionPoints=1, range=1, MOVE, damage=0)
    val shootAction = Action(id=2, "Shoot", "images/action_attack.png", "shoot.wav", actionPoints=1, range=3, SHOOT, damage=2)
    val waitAction = Action(id=3, "Wait", "images/action_wait.png", "shoot.wav", actionPoints=1, range=1, WAIT, damage=2)
    actions = List(moveAction, shootAction, waitAction)

    // Setup players
    val player1 = PlayerObject("Player1", "images/light_tank_red.png", Position(0, 0), Direction.DOWN, playerNumber=1, "images/background_won_red.png", actionPoints=1, maxActionPoints=1, healthPoints=3, maxHealthPoints=3, actions)
    val player2 = PlayerObject("Player2", "images/medium_tank_blue.png", Position(0, 1), Direction.DOWN, playerNumber=2, "images/background_won_blue.png", actionPoints=1, maxActionPoints=1, healthPoints=3, maxHealthPoints=3, actions)

    gameObjects = List(player1, player2)
  }

  def loadInstantWinScenario(): Unit = {
    rowCount = 1
    colCount = 1
    gameObjects = List(PlayerObject("Winner", "images/light_tank_red.png", Position(0, 0), Direction.DOWN, playerNumber=1, "images/background_won_red.png", actionPoints=3, maxActionPoints=3, healthPoints=3, maxHealthPoints=3, List()))
  }

  override def listScenarios: List[String] = {
    List[String](testScenario1,testScenario2)
  }

  override def generateGame(rowCount: Int, columnCount: Int, completion: (Boolean) => Unit): GameConfigProvider = {
    // For testing reasons. The game generation is tested in an other spec
    if (rowCount > 0 && columnCount > 0) {
      completion(true)
    } else {
      completion(false)
    }
    this
  }
}
