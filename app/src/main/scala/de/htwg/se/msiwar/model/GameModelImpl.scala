package de.htwg.se.msiwar.model

import de.htwg.se.msiwar.model.ActionType._
import de.htwg.se.msiwar.util.Direction.Direction
import de.htwg.se.msiwar.util.{Direction, GameConfigProvider}

import scala.util.control.Breaks

case class GameModelImpl(gameConfigProvider: GameConfigProvider, gameBoard: GameBoard, lastExecutedAction: Option[Action], activePlayer: PlayerObject, turnNumber: Int) extends GameModel {

  private val gameObjects = gameConfigProvider.gameObjects
  private val availableScenarios = gameConfigProvider.listScenarios

  override def reset(gameConfigProvider: GameConfigProvider): GameModel = {
    val newModel = copy(gameConfigProvider, GameBoard(gameConfigProvider.rowCount, gameConfigProvider.colCount, gameConfigProvider.gameObjects), Option.empty[Action])
    gameObjects.collect({ case o: PlayerObject => o }).foreach(p => resetPlayer(p))
    newModel
  }

  private def resetPlayer(playerToReset: PlayerObject): Unit = {
    playerToReset.resetActionPoints()
    playerToReset.resetHealthPoints()
  }

  override def startGame(scenarioId: Int): GameModel = {
    val scenarioName = availableScenarios(scenarioId)
    val configProvider = gameConfigProvider.loadFromFile(scenarioName)
    reset(configProvider)
  }

  override def startRandomGame(rowCount: Int, columnCount: Int): GameModel = {
    var newModel: GameModel = this
    gameConfigProvider.generateGame(rowCount, columnCount, couldGenerateGame => {
      if (couldGenerateGame) {
        newModel = reset(gameConfigProvider)
      } else {
        publish(ModelCouldNotGenerateGame())
      }
    })
    newModel
  }

  override def activePlayerName: String = {
    activePlayer.name
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

  override def executeAction(actionId: Int, rowIndex: Int, columnIndex: Int): GameModel = {
    executeAction(actionId, gameBoard.calculateDirection(activePlayer.position, Position(rowIndex, columnIndex)))
  }

  override def executeAction(actionId: Int, direction: Direction): GameModel = {
    val actionForId = activePlayer.actions.find(_.id == actionId)
    if (actionForId.isDefined) {
      // Update view direction first to ensure correct view direction on action execution
      activePlayer.viewDirection = direction

      val actionToExecute = actionForId.get
      actionToExecute.actionType match {
        case MOVE =>
          val newPositionOpt = gameBoard.calculatePositionForDirection(activePlayer.position, direction, actionToExecute.range)
          if (newPositionOpt.isDefined) {
            val newPosition = newPositionOpt.get
            val oldPosition = activePlayer.position.copy()
            gameBoard.moveGameObject(activePlayer, newPosition)
            publish(ModelCellChanged(List((newPosition.rowIdx, newPosition.columnIdx), (oldPosition.rowIdx, oldPosition.columnIdx))))

          }
        case SHOOT =>
          val positionForDirection = gameBoard.calculatePositionForDirection(activePlayer.position, direction, actionToExecute.range)
          if (positionForDirection.isDefined) {
            val collisionObjectOpt = gameBoard.collisionObject(activePlayer.position, positionForDirection.get, ignoreLastPosition = false)
            if (collisionObjectOpt.isDefined) {
              val collisionObject = collisionObjectOpt.get
              collisionObject match {
                case playerCollisionObject: PlayerObject =>
                  playerCollisionObject.currentHealthPoints -= actionToExecute.damage
                  // Remove player if dead
                  if (playerCollisionObject.currentHealthPoints < 0) {
                    removePlayerFromGame(playerCollisionObject)
                  }
                  publish(ModelCellChanged(List((playerCollisionObject.position.rowIdx, playerCollisionObject.position.columnIdx), (activePlayer.position.rowIdx, activePlayer.position.columnIdx))))
                case _ => publish(ModelCellChanged(List((activePlayer.position.rowIdx, activePlayer.position.columnIdx))))
              }
              publish(ModelAttackResult(collisionObject.position.rowIdx, collisionObject.position.columnIdx, hit = true, gameConfigProvider.attackImagePath, gameConfigProvider.attackSoundPath))
            } else {
              val targetPositionOpt = gameBoard.calculatePositionForDirection(activePlayer.position, direction, actionToExecute.range)
              if (targetPositionOpt.isDefined) {
                val targetPosition = targetPositionOpt.get
                publish(ModelCellChanged(List((activePlayer.position.rowIdx, activePlayer.position.columnIdx))))
                publish(ModelAttackResult(targetPosition.rowIdx, targetPosition.columnIdx, hit = false, gameConfigProvider.attackImagePath, gameConfigProvider.attackSoundPath))
              }
            }
          }
        case WAIT => // Do nothing
      }
      activePlayer.currentActionPoints -= actionToExecute.actionPoints
      val model = copy(gameConfigProvider, gameBoard, Option(actionToExecute))
      updateTurn()
      model
    } else {
      this
    }
  }

  def updateTurn(): GameModel = {
    var newModel: GameModel = this
    if (winnerId.isDefined) {
      publish(ModelPlayerWon(winnerId.get, wonImagePath))
    } else if (!activePlayer.hasActionPointsLeft) {
      newModel = nextTurn
    }
    newModel
  }

  private def removePlayerFromGame(playerObject: PlayerObject): GameModel = {
    gameBoard.removeGameObject(playerObject)
    val newGameObjects = gameObjects.filter(gameObject => {
      gameObject match {
        case p: PlayerObject => p.playerNumber != playerObject.playerNumber
        case _ => true
      }
    })
    copy(gameConfigProvider, GameBoard(gameConfigProvider.rowCount, gameConfigProvider.colCount, newGameObjects), lastExecutedAction)
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
      actionToExecute.actionType match {
        case MOVE =>
          val newPositionOpt = gameBoard.calculatePositionForDirection(activePlayer.position, direction, actionToExecute.range)
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
    val playersAliveIds = gameObjects.collect({ case p: PlayerObject => p }).filter(_.hasHealthPointsLeft).map(_.playerNumber)
    if (playersAliveIds.lengthCompare(1) == 0) {
      Option(playersAliveIds.head)
    } else {
      Option.empty
    }
  }

  private def nextTurn: GameModel = {
    var foundNextPlayer = false
    var nextPlayer = firstPlayerAlive()
    var nextTurn = turnCounter
    Breaks.breakable(
      for (playerObject <- gameObjects.collect({ case s: PlayerObject => s })) {
        if (playerObject.playerNumber > activePlayerNumber) {
          nextPlayer = playerObject
          foundNextPlayer = true
          Breaks.break()
        }
      }
    )

    // If every player did his turn, start the next turn with first player alive
    if (!foundNextPlayer) {
      nextTurn += 1
      // Reset action points of all players when new turn has started
      for (playerObject <- gameObjects.collect({ case s: PlayerObject => s })) {
        playerObject.resetActionPoints()
      }
    }
    val result = copy(gameConfigProvider, gameBoard, lastExecutedAction, nextPlayer, nextTurn)
    result
  }

  private def firstPlayerAlive(): PlayerObject = {
    gameObjects.collect({ case s: PlayerObject => s }).reduceLeft((a, b) => if (a.playerNumber < b.playerNumber) a else b)
  }

  private def player(playerNumber: Int): Option[PlayerObject] = {
    gameObjects.collect({ case s: PlayerObject => s }).find(_.playerNumber == playerNumber)
  }

  override def activePlayerNumber: Int = {
    activePlayer.playerNumber
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
    val objectAt = gameBoard.gameObjectAt(rowIndex, columnIndex)
    if (objectAt.isDefined) {
      objectAt.get match {
        case playerObj: PlayerObject =>
          Option(PlayerObject(playerObj.name,
          imagePathForViewDirection(playerObj.imagePath, playerObj.viewDirection),
          playerObj.position,
          playerObj.viewDirection,
          playerObj.playerNumber,
          playerObj.wonImagePath,
          playerObj.currentActionPoints,
          playerObj.currentHealthPoints,
          playerObj.actions)
        )
        case blockObject: BlockObject =>
          Option(BlockObject(blockObject.name,
            blockObject.imagePath,
            blockObject.position)
          )
      }
    } else {
      Option.empty[GameObject]
    }
  }

  override def cellsInRange(actionId: Option[Int]): List[(Int, Int)] = {
    if (actionId.isDefined) {
      val actionForId = activePlayer.actions.find(_.id == actionId.get)
      if (actionForId.isDefined) {
        return gameBoard.reachableCells(activePlayer.position, actionForId.get)
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
      player(winnerId.get).get.wonImagePath
    } else {
      ""
    }
  }

  override def scenarioIds: Set[Int] = {
    gameConfigProvider.listScenarios.indices.toSet
  }

  override def scenarioName(scenarioId: Int): Option[String] = {
    val scenarioName = gameConfigProvider.listScenarios(scenarioId)
    Option(scenarioName.substring(0, scenarioName.lastIndexOf('.')).replace('_', ' '))
  }

  override def activePlayerActionPoints: Int = {
    activePlayer.currentActionPoints
  }

  override def activePlayerHealthPoints: Int = {
    activePlayer.currentHealthPoints
  }
}
