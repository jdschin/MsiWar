package de.htwg.se.msiwar.model

import de.htwg.se.msiwar.model.ActionType._
import de.htwg.se.msiwar.util.Direction.Direction
import de.htwg.se.msiwar.util.{Direction, GameConfigProvider}

import scala.swing.event.Event
import scala.util.control.Breaks

case class GameModelImpl(gameConfigProvider: GameConfigProvider, gameBoard: GameBoard, lastExecutedAction: Option[Action], playerNumber: Int, turnNumber: Int) extends GameModel {

  override def init(gameConfigProvider: GameConfigProvider): GameModel = {
    copy(gameConfigProvider, GameBoard(gameConfigProvider.rowCount, gameConfigProvider.colCount, gameConfigProvider.gameObjects), Option.empty[Action], 1,1)
  }

  override def startGame(scenarioId: Int): GameModel = {
    val scenarioName = gameConfigProvider.listScenarios(scenarioId)
    val configProvider = gameConfigProvider.loadFromFile(scenarioName)
    init(configProvider)
  }

  override def startRandomGame(rowCount: Int, columnCount: Int): GameModel = {
    var newModel: GameModel = this
    gameConfigProvider.generateGame(rowCount, columnCount, couldGenerateGame => {
      if (couldGenerateGame) {
        newModel = init(gameConfigProvider)
      } else {
        publish(ModelCouldNotGenerateGame())
      }
    })
    newModel
  }

  override def activePlayerName: String = {
    if(gameBoard.player(playerNumber).isDefined){
      gameBoard.player(playerNumber).get.name
    } else {
      ""
    }
  }

  override def actionIdsForPlayer(playerNumber: Int): Option[Set[Int]] = {
    player(playerNumber) map (_.actions.map(_.id).toSet)
  }

  override def actionPointCost(actionId: Int): Int = {
    val foundAction = actions.find(_.id == actionId)
    if (foundAction.isDefined) {
      foundAction.get.actionPoints
    } else {
      0
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

  private def actions: Set[Action] = {
    gameBoard.players.flatMap(_.actions).toSet
  }

  override def actionIconPath(actionId: Int): Option[String] = {
    val foundAction = actions.find(_.id == actionId)
    if (foundAction.isDefined) {
      Option(foundAction.get.imagePath)
    } else {
      Option.empty
    }
  }

  override def executeAction(actionId: Int, rowIndex: Int, columnIndex: Int): (GameModel, List[Event]) = {
    executeAction(actionId, gameBoard.calculateDirection(gameBoard.player(playerNumber).get.position, Position(rowIndex, columnIndex)))
  }

  override def executeAction(actionId: Int, direction: Direction): (GameModel, List[Event]) = {
    var newGameBoard: GameBoard = gameBoard.copy()
    var events: List[Event] = List[Event]()
    var newActivePlayer: PlayerObject = gameBoard.player(playerNumber).get.copy()

    val actionForId = newActivePlayer.actions.find(_.id == actionId)
    if (actionForId.isDefined) {
      // Update view direction first to ensure correct view direction on action execution
      newActivePlayer = newActivePlayer.copy(viewDirection=direction)

      val actionToExecute = actionForId.get
      actionToExecute.actionType match {
        case MOVE =>
          val newPositionOpt = newGameBoard.calculatePositionForDirection(newActivePlayer.position, direction, actionToExecute.range)
          if (newPositionOpt.isDefined) {
            val newPosition = newPositionOpt.get
            val oldPosition = newActivePlayer.position
            newGameBoard = newGameBoard.moveGameObject(newActivePlayer, newPosition)
            newActivePlayer = newActivePlayer.copy(position = newPosition)
            events = events.::(CellChanged(List((newPosition.rowIdx, newPosition.columnIdx), (oldPosition.rowIdx, oldPosition.columnIdx))))
          }
        case SHOOT =>
          val positionForDirection = newGameBoard.calculatePositionForDirection(newActivePlayer.position, direction, actionToExecute.range)
          if (positionForDirection.isDefined) {
            val collisionObjectOpt = newGameBoard.collisionObject(newActivePlayer.position, positionForDirection.get, ignoreLastPosition = false)
            if (collisionObjectOpt.isDefined) {
              val collisionObject = collisionObjectOpt.get
              collisionObject match {
                case playerCollisionObject: PlayerObject =>
                  val updatedPlayerCollisionObject = playerCollisionObject.copy(healthPoints = playerCollisionObject.healthPoints - actionToExecute.damage)
                  newGameBoard = newGameBoard.placeGameObject(updatedPlayerCollisionObject)
                  // Remove player if dead
                  if (updatedPlayerCollisionObject.healthPoints < 0) {
                    newGameBoard = newGameBoard.removeGameObject(updatedPlayerCollisionObject)
                  }
                  events = events.::(CellChanged(List((playerCollisionObject.position.rowIdx, playerCollisionObject.position.columnIdx), (updatedPlayerCollisionObject.position.rowIdx, updatedPlayerCollisionObject.position.columnIdx))))
                case _ => events = events.::(CellChanged(List((newActivePlayer.position.rowIdx, newActivePlayer.position.columnIdx))))
              }
              events = events.::(AttackResult(collisionObject.position.rowIdx, collisionObject.position.columnIdx, hit = true, gameConfigProvider.attackImagePath, gameConfigProvider.attackSoundPath))
            } else {
              val targetPositionOpt = newGameBoard.calculatePositionForDirection(newActivePlayer.position, direction, actionToExecute.range)
              if (targetPositionOpt.isDefined) {
                val targetPosition = targetPositionOpt.get
                events = events.::(CellChanged(List((newActivePlayer.position.rowIdx, newActivePlayer.position.columnIdx))))
                events = events.::(AttackResult(targetPosition.rowIdx, targetPosition.columnIdx, hit = false, gameConfigProvider.attackImagePath, gameConfigProvider.attackSoundPath))
              }
            }
          }
        case WAIT => // Do nothing
      }
      newGameBoard = newGameBoard.placeGameObject(newActivePlayer.copy(actionPoints = newActivePlayer.actionPoints - actionToExecute.actionPoints))

      val nextTurn = updateTurn(Option(actionToExecute), newGameBoard)
      (copy(gameConfigProvider, newGameBoard, Option(actionToExecute), nextTurn._1, nextTurn._2), events)
    } else {
      (this, List[Event]())
    }
  }

  private def updateTurn(lastAction: Option[Action], currentGameBoard: GameBoard): (Int, Int) = {
    val currentPlayerOpt = currentGameBoard.player(playerNumber)
    if (currentPlayerOpt.isDefined && currentPlayerOpt.get.actionPoints <= 0) {
      var foundNextPlayer = false
      // Set next player to first player found which is alive
      var nextPlayer =  currentGameBoard.players.reduceLeft((a, b) => if (a.playerNumber < b.playerNumber) a else b)
      var nextTurnNumber = turnCounter
      Breaks.breakable(
        for (playerObject <- currentGameBoard.players) {
          if (playerObject.playerNumber > activePlayerNumber) {
            nextPlayer = playerObject
            foundNextPlayer = true
            Breaks.break()
          }
        }
      )

      // If every player did his turn, start the next turn with first player alive
      if (!foundNextPlayer) {
        nextTurnNumber += 1
        // Reset action points of all players when new turn has started
        for (playerObject <- currentGameBoard.players) {
          val updatedPlayerObject = playerObject.copy(actionPoints = playerObject.maxActionPoints)
          currentGameBoard.placeGameObject(updatedPlayerObject)
        }
      }
      (nextPlayer.playerNumber, nextTurnNumber)
    } else {
      (playerNumber, turnNumber)
    }
  }

  override def lastExecutedActionId: Option[Int] = {
    if (lastExecutedAction.isDefined) {
      Option(lastExecutedAction.get.id)
    } else {
      Option.empty[Int]
    }
  }

  override def canExecuteAction(actionId: Int, rowIndex: Int, columnIndex: Int): Boolean = {
    canExecuteAction(actionId, gameBoard.calculateDirection(gameBoard.player(playerNumber).get.position, Position(rowIndex, columnIndex)))
  }

  override def canExecuteAction(actionId: Int, direction: Direction): Boolean = {
    if (winnerId.isDefined) {
      return false
    }
    val actionForId = gameBoard.player(playerNumber).get.actions.find(_.id == actionId)
    var result = false
    if (actionForId.isDefined) {
      val actionToExecute = actionForId.get
      actionToExecute.actionType match {
        case MOVE =>
          val newPositionOpt = gameBoard.calculatePositionForDirection(gameBoard.player(playerNumber).get.position, direction, actionToExecute.range)
          if (newPositionOpt.isDefined) {
            val newPosition = newPositionOpt.get
            result = gameBoard.isInBound(newPosition) &&
              gameBoard.gameObjectAt(newPosition).isEmpty
          }
        case _ => result = true
      }
    }
    result
  }

  override def winnerId: Option[Int] = {
    val playersAliveIds = gameBoard.players.filter(_.healthPoints > 0).map(_.playerNumber)
    if (playersAliveIds.lengthCompare(1) == 0) {
      Option(playersAliveIds.head)
    } else {
      Option.empty
    }
  }

  private def player(playerNumber: Int): Option[PlayerObject] = {
    gameBoard.players.find(_.playerNumber == playerNumber)
  }

  override def activePlayerNumber: Int = {
    gameBoard.player(playerNumber).get.playerNumber
  }

  override def turnCounter: Int = turnNumber

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

  override def cellContent(rowIndex: Int, columnIndex: Int) : Option[GameObject] = {
    gameBoard.gameObjectAt(rowIndex, columnIndex)
  }

  override def cellsInRange(actionId: Option[Int]): List[(Int, Int)] = {
    if (actionId.isDefined) {
      val actionForId = gameBoard.player(playerNumber).get.actions.find(_.id == actionId.get)
      if (actionForId.isDefined) {
        return gameBoard.reachableCells(gameBoard.player(playerNumber).get.position, actionForId.get)
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
    val actionForId = gameBoard.player(playerNumber).get.actions.find(_.id == actionId)
    if (actionForId.isDefined) {
      actionForId.get.damage
    } else {
      0
    }
  }

  override def actionRange(actionId: Int): Int = {
    val actionForId = gameBoard.player(playerNumber).get.actions.find(_.id == actionId)
    if (actionForId.isDefined) {
      actionForId.get.range
    } else {
      0
    }
  }

  override def wonImagePath: String = {
    if (winnerId.isDefined) {
      player(winnerId.get).get.wonImagePath
    } else {
      ""
    }
  }

  override def scenarioIds: Set[Int] = {
    gameConfigProvider.listScenarios.indices.toSet
  }

  override def scenarioName(scenarioId: Int): Option[String] = {
    if(scenarioId >= 0 && scenarioId < gameConfigProvider.listScenarios.size) {
      val scenarioName = gameConfigProvider.listScenarios(scenarioId)
      Option(scenarioName.substring(0, scenarioName.lastIndexOf('.')).replace('_', ' '))
    } else {
      Option.empty
    }
  }

  override def activePlayerActionPoints: Int = {
    gameBoard.player(playerNumber).get.actionPoints
  }

  override def activePlayerHealthPoints: Int = {
    gameBoard.player(playerNumber).get.healthPoints
  }
}
