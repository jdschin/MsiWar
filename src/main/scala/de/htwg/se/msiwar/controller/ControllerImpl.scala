package de.htwg.se.msiwar.controller

import de.htwg.se.msiwar.model._
import de.htwg.se.msiwar.util.Direction.Direction

class ControllerImpl(model: GameModel) extends Controller {

  listenTo(model)
  reactions += {
    case e: GameBoardChanged => publish(CellChanged(e.rowColumnIndexes))
    case e: ObjectHit => {
      e.gameObject match {
        case playerObject: PlayerObject => publish(PlayerHit(playerObject.name, playerObject.playerNumber, playerObject.currentHealthPoints))
        case blockObject: BlockObject => publish(BlockHit(blockObject.name))
      }
    }
  }

  def updateTurn: Unit = {
    val winnerId = model.winnerId
    if (winnerId.isDefined) {
      publish(PlayerWon(winnerId.get))
    } else if (model.turnOver) {
      model.nextTurn
      publish(TurnStarted(model.activePlayerNumber))
    }
  }

  override def cellContentToText(rowIndex: Int, columnIndex: Int) = {
    val objectAt = model.gameObjectAt(rowIndex, columnIndex)
    if (objectAt.isDefined) {
      objectAt.get match {
        case playerObj: PlayerObject => playerObj.playerNumber.toString
        case blockObj: BlockObject => blockObj.name
      }
    } else {
      "X"
    }
  }

  override def cellContentImagePath(rowIndex: Int, columnIndex: Int): Option[String] = {
    val objectAt = model.gameObjectAt(rowIndex, columnIndex)
    if (objectAt.isDefined) {
      Option(objectAt.get.imagePath)
    } else {
      Option.empty[String]
    }
  }

  override def cellInRange(rowIndex: Int, columnIndex: Int) = {
    true
  }

  override def startActionMode(actionId: Int) = {}

  override def stopActionMode(actionId: Int) = {}

  override def executeAction(actionId: Int, direction: Direction) = {
    model.executeAction(actionId, direction)
    updateTurn
  }

  override def canExecuteAction(actionId: Int, direction: Direction): Boolean = {
    model.canExecuteAction(actionId, direction)
  }

  override def actionIds(playerNumber: Int): List[Int] = {
    model.actionIdsForPlayer(playerNumber)
  }

  override def actionHotKey(actionId: Int): String = {
    model.actionHotKey(actionId)
  }

  override def actionDescription(actionId: Int): String = {
    model.actionDescription(actionId)
  }

  override def actionIconPath(actionId: Int): Option[String] = {
    model.actionIconPath(actionId)
  }

  override def rowCount = {
    model.rowCount
  }

  override def columnCount = {
    model.columnCount
  }

  override def levelBackgroundImagePath: Option[String] = {
    // TODO get value from model
    Option("src/main/resources/images/background_woodlands.png")
  }

  override def actionbarBackgroundImagePath: Option[String] = {
    // TODO get value from model
    Option("src/main/resources/images/background_actionbar.png")
  }

  override def activePlayerNumber = {
    model.activePlayerNumber
  }

  override def playerName(playerNumber: Int) = {
    model.activePlayerName
  }

  override def reset = {
    model.reset
    updateTurn
    publish(TurnStarted(model.activePlayerNumber))
  }

  override def turnCounter = {
    model.turnCounter
  }
}
