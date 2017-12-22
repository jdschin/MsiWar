package de.htwg.se.msiwar.controller

import de.htwg.se.msiwar.model._
import de.htwg.se.msiwar.util.Direction.Direction

class ControllerImpl(model: GameModel) extends Controller {

  listenTo(model)
  reactions += {
    case e: GameBoardChanged => publish(CellChanged(e.rowColumnIndexes))
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
    model.cellContentToText(rowIndex, columnIndex)
  }

  override def cellContentImagePath(rowIndex: Int, columnIndex: Int): Option[String] = {
    model.cellContentImagePath(rowIndex, columnIndex)
  }

  override def cellsInRange(actionId: Option[Int]): Unit = {
    publish(CellsInRange(model.cellsInRange(actionId)))
  }

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

  override def levelBackgroundImagePath: String = {
    model.levelBackgroundImagePath
  }

  override def actionbarBackgroundImagePath: String = {
    model.actionbarBackgroundImagePath
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

  override def actionDamage(actionId: Int): Int = {
    model.actionDamage(actionId)
  }

  override def actionRange(actionId: Int): Int = {
    model.actionRange(actionId)
  }

  override def activePlayerActionPoints: Int = {
    model.activePlayerActionPoints
  }

  override def activePlayerHealthPoints: Int = {
    model.activePlayerHealthPoints
  }
}
