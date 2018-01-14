package de.htwg.se.msiwar.model

import de.htwg.se.msiwar.model.ActionType.{MOVE, SHOOT, WAIT}
import de.htwg.se.msiwar.model.RandomImagePaths.blockImagePath
import de.htwg.se.msiwar.util.Direction.Direction
import de.htwg.se.msiwar.util.{Dijkstra, Direction}

import scala.util.Random._

object RandomImagePaths {
  def blockImagePath(): String = {
    nextInt(7) match {
      case 0 => "images/block_city.png"
      case 1 => "images/block_desert_city.png"
      case 2 => "images/block_dune.png"
      case 3 => "images/block_helicopter_destroyed.png"
      case 4 => "images/block_lake.png"
      case 5 => "images/block_mountain.png"
      case 6 => "images/block_wood.png"
    }
  }

  def backgroundImagePath(): String = {
    nextInt(2) match {
      case 0 => "images/background_desert.png"
      case 1 => "images/background_woodlands.png"
    }
  }

  private def tankType(): String = {
    nextInt(3) match {
      case 0 => "medium"
      case 1 => "light"
      case 2 => "heavy"
    }
  }

  private def tankColor(playerNumber: Int): String = {
    playerNumber match {
      case 1 => "red"
      case 2 => "blue"
      case 3 => "purple"
      case 4 => "brown"
    }
  }

  def playerImagePath(playerNumber: Int): (String, String) = {
    val tankType = RandomImagePaths.tankType()
    val tankColor = RandomImagePaths.tankColor(playerNumber)
    (s"images/${tankType}_tank_$tankColor.png", s"images/background_won_$tankColor.png")
  }
}

case class GameGenerator(rowCount: Int, columnCount: Int) {

  private var gameObjects = List[GameObject]()
  private val moveAction = Action(id = 1, "Move", "images/action_move.png", "move.wav", actionPoints = 1, range = 1, MOVE, damage = 0)
  private val shootAction = Action(id = 2, "Shoot", "images/action_attack.png", "shoot.wav", actionPoints = 1, range = 3, SHOOT, damage = 2)
  private val waitAction = Action(id = 3, "Wait", "images/action_wait.png", "shoot.wav", actionPoints = 1, range = 1, WAIT, damage = 2)
  private val rocketAction = Action(id = 4, "Shoot", "images/action_rocket_attack.png", "shoot.wav", actionPoints = 2, range = 5, SHOOT, damage = 3)
  private val actions = List(moveAction, shootAction, waitAction)

  def generate(): Option[List[GameObject]] = {
    val numberOfPlayers = nextInt(3) + 2

    gameObjects = List[GameObject]()

    val numberOfObjects = nextInt(rowCount * columnCount)

    for (i <- 1 until numberOfPlayers + 1) {
      gameObjects = gameObjects ::: List(randomPlayerObject(i))
    }

    for (_ <- 0 until numberOfObjects) {
      gameObjects = gameObjects ::: List(randomBlockObject())
    }

    val gameBoard = GameBoard(rowCount, columnCount, gameObjects)

    val playerObjects = gameObjects.collect({ case po: PlayerObject => po })
    if (playersCanReachEachOther(gameBoard, playerObjects)) {
      Option(gameObjects)
    } else {
      Option.empty
    }
  }

  private def playersCanReachEachOther(gameBoard: GameBoard, playerObjects: List[PlayerObject]): Boolean = {
    for (i <- playerObjects.indices) {
      for (j <- playerObjects.indices) {
        if (i != j) {
          if (!pathAvailable(gameBoard, playerObjects(i), playerObjects(j))) {
            return false
          }
        }
      }
    }
    true
  }


  private def randomFreePosition(): Position = {
    var position = randomPosition()
    while (positionOccupied(position)) {
      position = randomPosition()
    }
    position
  }

  private def positionOccupied(position: Position): Boolean = {
    gameObjects.exists(_.position == position)
  }

  private def randomBlockObject(): BlockObject = {
    val imagePath = blockImagePath()

    BlockObject("B", imagePath, randomFreePosition())
  }

  private def randomPlayerObject(playerNumber: Int): PlayerObject = {
    val imagePath = RandomImagePaths.playerImagePath(playerNumber)

    var playerActions = actions
    if (imagePath._1.contains("heavy")) {
      playerActions = playerActions ::: List(rocketAction)
    }
    PlayerObject(s"Spieler$playerNumber", imagePath._1, randomFreePosition(), randomDirection(), playerNumber = playerNumber,
      imagePath._2, maxActionPoints = nextInt(10) + 1, maxHealthPoints = nextInt(10) + 1, playerActions)
  }

  private def randomDirection(): Direction = {
    nextInt(8) match {
      case 0 => Direction.DOWN
      case 1 => Direction.UP
      case 2 => Direction.RIGHT
      case 3 => Direction.LEFT
      case 4 => Direction.RIGHT_DOWN
      case 5 => Direction.LEFT_DOWN
      case 6 => Direction.RIGHT_UP
      case 7 => Direction.LEFT_UP
    }
  }

  private def randomPosition(): Position = {
    Position(nextInt(rowCount), nextInt(columnCount))
  }

  private def pathAvailable(gameBoard: GameBoard, from: PlayerObject, to: PlayerObject): Boolean = {
    val lookup = buildGraphs(gameBoard)
    Dijkstra.findPath[Position](lookup, List((0, List(from.position))), to.position, Set()).isDefined
  }

  private def buildGraphs(gameBoard: GameBoard): Map[Position, List[(Int, Position)]] = {
    var lookup = Map[Position, List[(Int, Position)]]()
    for (row <- 0 until gameBoard.rows) {
      for (column <- 0 until gameBoard.columns) {
        val position = Position(row, column)
        val objectAt = gameBoard.gameObjectAt(position)
        if (objectAt.isEmpty || objectAt.get.isInstanceOf[PlayerObject]) {

          val surroundingCells = gameBoard.cellsInRange(position, 1).filter(p => {

            if (gameBoard.isInBound(p)) {
              val gameObjectOpt = gameBoard.gameObjectAt(p)
              gameObjectOpt match {
                case Some(gameObject) =>
                  gameObject match {
                    case _: PlayerObject => true
                    case _: BlockObject => false
                  }
                case _ => true
              }
            } else {
              false
            }
          }).map((1, _))

          lookup = lookup + (position -> surroundingCells)
        }
      }
    }
    lookup
  }

}
