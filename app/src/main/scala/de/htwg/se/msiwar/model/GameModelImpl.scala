package de.htwg.se.msiwar.model

import de.htwg.se.msiwar.model.ActionType._
import de.htwg.se.msiwar.util.Direction
import de.htwg.se.msiwar.util.Direction.Direction

import scala.util.control.Breaks

case class GameModelImpl(numRows: Int, numCols: Int, var gameObjects: List[GameObject], levelBackgroundImagePath: String, actionbarBackgroundImagePath: String, attackImagePath: String, attackSoundPath: String, openingBackgroundImagePath: String) extends GameModel {
  private var gameBoard = GameBoard(numRows, numCols, gameObjects)
  private var activePlayer = player(1)
  private var turnNumber = 1
  private var lastExecutedAction = Option.empty[Action]

  override def reset(): Unit = {
    gameBoard = GameBoard(numRows, numCols, gameObjects)
    gameObjects.collect({ case p: PlayerObject => p }).foreach(playerObject => {
      playerObject.resetActionPoints()
      playerObject.resetHealthPoints()
    })
    activePlayer = player(1)
    turnNumber = 1
    lastExecutedAction = Option.empty[Action]
  }

  override def activePlayerName: String = {
    activePlayer.name
  }

  override def actionIdsForPlayer(playerNumber: Int): Set[Int] = {
    player(playerNumber).actions.map(_.id).toSet
  }

  override def actionDescription(actionId: Int): String = {
    val foundAction = actions.find(_.id == actionId)
    if (foundAction.isDefined) {
      foundAction.get.description
    } else {
      ""
    }
  }

  private def actions: Set[Action] = {
    val players = gameObjects.collect({ case s: PlayerObject => s })
    players.flatMap(_.actions).toSet
  }

  override def actionIconPath(actionId: Int): Option[String] = {
    val foundAction = actions.find(_.id == actionId)
    if (foundAction.isDefined) {
      Option(foundAction.get.imagePath)
    } else {
      Option.empty
    }
  }

  override def executeAction(actionId: Int, rowIndex: Int, columnIndex: Int): Unit = {
    executeAction(actionId, gameBoard.calculateDirection(activePlayer.position, Position(rowIndex, columnIndex)))
  }

  override def executeAction(actionId: Int, direction: Direction): Unit = {
    val actionForId = activePlayer.actions.find(_.id == actionId)
    if (actionForId.isDefined) {
      // Update view direction first to ensure correct view direction on action execution
      activePlayer.viewDirection = direction

      val actionToExecute = actionForId.get
      actionToExecute.actionType match {
        case MOVE =>
          val newPosition = gameBoard.calculatePositionForDirection(activePlayer.position, direction, actionToExecute.range)
          val oldPosition = activePlayer.position.copy()
          gameBoard.moveGameObject(activePlayer, newPosition)
          publish(GameBoardChanged(List((newPosition.rowIdx, newPosition.columnIdx), (oldPosition.rowIdx, oldPosition.columnIdx))))

        case SHOOT =>
          val collisionObjectOpt = gameBoard.collisionObject(activePlayer.position, gameBoard.calculatePositionForDirection(activePlayer.position, direction, actionToExecute.range), ignoreLastPosition = false)
          if (collisionObjectOpt.isDefined) {
            val collisionObject = collisionObjectOpt.get
            collisionObject match {
              case playerCollisionObject: PlayerObject =>
                playerCollisionObject.currentHealthPoints -= actionToExecute.damage
                // Remove player if dead
                if (playerCollisionObject.currentHealthPoints < 0) {
                  removePlayerFromGame(playerCollisionObject)
                }
                publish(GameBoardChanged(List((playerCollisionObject.position.rowIdx, playerCollisionObject.position.columnIdx), (activePlayer.position.rowIdx, activePlayer.position.columnIdx))))
              case _ => publish(GameBoardChanged(List((activePlayer.position.rowIdx, activePlayer.position.columnIdx))))
            }
            publish(AttackResult(collisionObject.position.rowIdx, collisionObject.position.columnIdx, hit = true, attackImagePath, attackSoundPath))
          } else {
            val targetPosition = gameBoard.calculatePositionForDirection(activePlayer.position, direction, actionToExecute.range)
            publish(GameBoardChanged(List((activePlayer.position.rowIdx, activePlayer.position.columnIdx))))
            publish(AttackResult(targetPosition.rowIdx, targetPosition.columnIdx, hit = false, attackImagePath, attackSoundPath))
          }
        case WAIT => // Do nothing
      }
      activePlayer.currentActionPoints -= actionToExecute.actionPoints
      lastExecutedAction = Option(actionToExecute)
      publish(ActivePlayerStatsChanged())
    }
  }

  private def removePlayerFromGame(playerObject: PlayerObject): Unit = {
    gameBoard.removeGameObject(playerObject)
    gameObjects = gameObjects.filter(gameObject => {
      gameObject match {
        case p: PlayerObject => p.playerNumber != playerObject.playerNumber
        case _ => true
      }
    })
  }

  override def lastExecutedActionId: Option[Int] = {
    if (lastExecutedAction.isDefined) {
      Option(lastExecutedAction.get.id)
    } else {
      Option.empty[Int]
    }
  }

  override def canExecuteAction(actionId: Int, rowIndex: Int, columnIndex: Int): Boolean = {
    canExecuteAction(actionId, gameBoard.calculateDirection(activePlayer.position, Position(rowIndex, columnIndex)))
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
        case MOVE =>
          val newPosition = gameBoard.calculatePositionForDirection(activePlayer.position, direction, actionToExecute.range)
          result = gameBoard.isInBound(newPosition) &&
            gameBoard.gameObjectAt(newPosition).isEmpty

        case _ => result = true
      }
    }
    result
  }

  override def winnerId: Option[Int] = {
    val playersAliveIds = gameObjects.collect({ case p: PlayerObject => p }).filter(_.hasHealthPointsLeft).map(_.playerNumber)
    if (playersAliveIds.lengthCompare(1) == 0) {
      Option(playersAliveIds.head)
    } else {
      Option.empty
    }
  }

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
        playerObject.resetActionPoints()
      }
    }
    turnCounter
  }

  private def player(playerNumber: Int): PlayerObject = {
    val foundPlayer = gameObjects.collect({ case s: PlayerObject => s }).find(_.playerNumber == playerNumber)
    foundPlayer.get
  }

  override def activePlayerNumber: Int = {
    activePlayer.playerNumber
  }

  override def turnCounter: Int = turnNumber

  override def turnOver: Boolean = {
    !activePlayer.hasActionPointsLeft
  }

  override def cellContentImagePath(rowIndex: Int, columnIndex: Int): Option[String] = {
    val objectAt = gameBoard.gameObjectAt(rowIndex, columnIndex)
    if (objectAt.isDefined) {
      objectAt.get match {
        case playerObj: PlayerObject => Option(imagePathForViewDirection(playerObj.imagePath, playerObj.viewDirection))
        case blockObj: BlockObject => Option(blockObj.imagePath)
      }
    } else {
      Option.empty[String]
    }
  }

  private def imagePathForViewDirection(imagePath: String, viewDirection: Direction): String = {
    val basePath = imagePath.substring(0, imagePath.lastIndexOf('.'))
    val imageExtension = imagePath.substring(imagePath.lastIndexOf('.'), imagePath.length)

    val sb = StringBuilder.newBuilder
    sb.append(basePath)
    sb.append("_")
    sb.append(Direction.toDegree(viewDirection))
    sb.append(imageExtension)
    sb.toString()
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
    if (actionId.isDefined) {
      val actionForId = activePlayer.actions.find(_.id == actionId.get)
      if (actionForId.isDefined) {
        return gameBoard.cellsInRange(activePlayer.position, actionForId.get)
      }
    }
    List()
  }

  override def rowCount: Int = {
    gameBoard.rows
  }

  override def columnCount: Int = {
    gameBoard.columns
  }

  override def actionDamage(actionId: Int): Int = {
    val actionForId = activePlayer.actions.find(_.id == actionId)
    if (actionForId.isDefined) {
      actionForId.get.damage
    } else {
      0
    }
  }

  override def actionRange(actionId: Int): Int = {
    val actionForId = activePlayer.actions.find(_.id == actionId)
    if (actionForId.isDefined) {
      actionForId.get.range
    } else {
      0
    }
  }

  override def wonImagePath: String = {
    if (winnerId.isDefined) {
      player(winnerId.get).wonImagePath
    } else {
      ""
    }
  }

  override def activePlayerActionPoints: Int = {
    activePlayer.currentActionPoints
  }

  override def activePlayerHealthPoints: Int = {
    activePlayer.currentHealthPoints
  }
}
