package de.htwg.se.msiwar.model

import de.htwg.se.msiwar.model.ActionType._
import de.htwg.se.msiwar.util.Direction
import de.htwg.se.msiwar.util.Direction.Direction

import scala.util.control.Breaks

case class GameModelImpl(numRows: Int, numCols: Int, gameObjects: List[GameObject], levelBackgroundImagePath:String , actionbarBackgroundImagePath:String) extends GameModel {
  private var gameBoard = GameBoard(numRows, numCols, gameObjects)
  private var activePlayer = player(1)
  private var turnNumber = 1

  override def reset: Unit = {
    gameBoard = GameBoard(numRows, numCols, gameObjects)
    gameObjects.collect({ case p: PlayerObject => p }).foreach(playerObject => {
      playerObject.resetActionPoints
      playerObject.resetHealthPoints
    })
    activePlayer = player(1)
    turnNumber = 1
  }

  private def player(playerNumber: Int): PlayerObject = {
    val foundPlayer = gameObjects.collect({ case s: PlayerObject => s }).find(_.playerNumber == playerNumber)
    foundPlayer.get
  }

  private def actions: List[Action] = {
    val players = gameObjects.collect({ case s: PlayerObject => s })
    players.flatMap(_.actions)
  }

  override def activePlayerNumber: Int = {
    activePlayer.playerNumber
  }

  override def activePlayerName: String = {
    activePlayer.name
  }

  override def actionIdsForPlayer(playerNumber: Int): List[Int] = {
    player(playerNumber).actions.map(_.id)
  }

  override def actionHotKey(actionId: Int): String = {
    val foundAction = actions.find(_.id == actionId)
    if (foundAction.isDefined) {
      // TODO create hotkey
      foundAction.get.description
    } else {
      ""
    }
  }

  override def actionDescription(actionId: Int): String = {
    val foundAction = actions.find(_.id == actionId)
    if (foundAction.isDefined) {
      foundAction.get.description
    } else {
      ""
    }
  }

  override def actionIconPath(actionId: Int): Option[String] = {
    val foundAction = actions.find(_.id == actionId)
    if (foundAction.isDefined) {
      Option(foundAction.get.imagePath)
    } else {
      Option.empty
    }
  }

  override def executeAction(actionId: Int, direction: Direction): Unit = {
    val actionForId = activePlayer.actions.find(_.id == actionId)
    if (actionForId.isDefined) {
      val actionToExecute = actionForId.get

      actionToExecute.actionType match {
        case MOVE => {
          val newPosition = calculatePositionForDirection(activePlayer.position, direction, actionToExecute.range);
          val oldPosition = activePlayer.position.copy()
          gameBoard.moveGameObject(activePlayer, newPosition)
          publish(GameBoardChanged(List((newPosition.y, newPosition.x), (oldPosition.y, oldPosition.x))))
        }
        case SHOOT => {
          val collisionObjectOpt = gameBoard.collisionObject(activePlayer.position, calculatePositionForDirection(activePlayer.position, direction, actionToExecute.range))
          if (collisionObjectOpt.isDefined) {
            if (collisionObjectOpt.get.isInstanceOf[PlayerObject]) {
              val playerCollisionObject = collisionObjectOpt.get.asInstanceOf[PlayerObject]
              playerCollisionObject.currentHealthPoints -= actionToExecute.damage
              publish(ObjectHit(playerCollisionObject))
              // TODO: check if there is a winner
            } else {
              publish(ObjectHit(collisionObjectOpt.get))
            }
          }
        }
        case WAIT => {}
      }
      activePlayer.currentActionPoints -= actionToExecute.actionPoints
    }
  }

  def calculatePositionForDirection(oldPosition: Position, direction: Direction, range: Int): Position = {
    var newPosition: Option[Position] = None
    direction match {
      case Direction.UP => newPosition = Some(Position(oldPosition.x, oldPosition.y + range))
      case Direction.DOWN => newPosition = Some(Position(oldPosition.x, oldPosition.y - range))
      case Direction.LEFT => newPosition = Some(Position(oldPosition.x - range, oldPosition.y))
      case Direction.RIGHT => newPosition = Some(Position(oldPosition.x + range, oldPosition.y))
      case Direction.LEFT_UP => newPosition = Some(Position(oldPosition.x - range, oldPosition.y + range))
      case Direction.LEFT_DOWN => newPosition = Some(Position(oldPosition.x - range, oldPosition.y - range))
      case Direction.RIGHT_UP => newPosition = Some(Position(oldPosition.x + range, oldPosition.y + range))
      case Direction.RIGHT_DOWN => newPosition = Some(Position(oldPosition.x + range, oldPosition.y - range))
    }
    newPosition.get
  }

  override def canExecuteAction(actionId: Int, direction: Direction): Boolean = {
    if (winnerId.isDefined) {
      return false
    }
    val actionForId = activePlayer.actions.find(_.id == actionId)
    var result = false
    if (actionForId.isDefined) {
      val actionToExecute = actionForId.get
      if ((activePlayer.currentActionPoints - actionToExecute.actionPoints) < 0) {
        return false
      }
      actionToExecute.actionType match {
        case MOVE => {
          val newPosition = calculatePositionForDirection(activePlayer.position, direction, actionToExecute.range)
          result = gameBoard.isInBound(newPosition) &&
            gameBoard.gameObjectAt(newPosition).isEmpty
        }
        case _ => result = true
      }
    }
    result
  }

  override def turnCounter: Int = turnNumber

  override def nextTurn: Int = {
    var foundNextPlayer = false
    Breaks.breakable(
      for (playerObject <- gameObjects.collect({ case s: PlayerObject => s })) {
        if (playerObject.playerNumber > activePlayerNumber) {
          activePlayer = playerObject
          foundNextPlayer = true
          Breaks.break()
        }
      }
    )

    // If every player did his turn, start the next turn with player 1
    if (!foundNextPlayer) {
      activePlayer = player(1)
      turnNumber += 1
      // Reset action points of all players when new turn has started
      for (playerObject <- gameObjects.collect({ case s: PlayerObject => s })) {
        playerObject.resetActionPoints
      }
    }
    turnCounter
  }

  override def turnOver = {
    !activePlayer.hasActionPointsLeft
  }

  override def cellContentImagePath(rowIndex: Int, columnIndex: Int): Option[String] = {
    val objectAt = gameBoard.gameObjectAt(rowIndex, columnIndex)
    if (objectAt.isDefined) {
      Option(objectAt.get.imagePath)
    } else {
      Option.empty[String]
    }
  }

  override def cellContentToText(rowIndex: Int, columnIndex: Int): String = {
    val objectAt = gameBoard.gameObjectAt(rowIndex, columnIndex)
    if (objectAt.isDefined) {
      objectAt.get match {
        case playerObj: PlayerObject => playerObj.playerNumber.toString
        case blockObj: BlockObject => blockObj.name
      }
    } else {
      "X"
    }
  }

  override def cellsInRange(actionId: Option[Int]): List[(Int, Int)] = {
    // TODO calculate and return cells in range
    List()
  }

  override def rowCount: Int = {
    gameBoard.rows
  }

  override def columnCount: Int = {
    gameBoard.columns
  }

  override def winnerId: Option[Int] = {
    val playersAliveIds = gameObjects.collect({ case p: PlayerObject => p }).filter(_.hasHealthPointsLeft).map(_.playerNumber)
    if (playersAliveIds.length == 1) {
      Option(playersAliveIds(0))
    } else {
      Option.empty
    }
  }
}
