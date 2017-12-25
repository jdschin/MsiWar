package de.htwg.se.msiwar.controller

import de.htwg.se.msiwar.model._
import de.htwg.se.msiwar.util.Direction.Direction

class ControllerImpl(model: GameModel) extends Controller {

  listenTo(model)
  reactions += {
    case e: GameBoardChanged => publish(CellChanged(e.rowColumnIndexes))
    case e: ActivePlayerStatsChanged => publish(PlayerStatsChanged(model.activePlayerNumber, model.activePlayerActionPoints))
    case e: AttackResult => publish(AttackActionResult(e.rowIndex, e.columnIndex, e.hit, e.attackImagePath))
  }

  override def cellContentToText(rowIndex: Int, columnIndex: Int) : String = {
    model.cellContentToText(rowIndex, columnIndex)
  }

  override def cellContentImagePath(rowIndex: Int, columnIndex: Int): Option[String] = {
    model.cellContentImagePath(rowIndex, columnIndex)
  }

  override def executeAction(actionId: Int, direction: Direction) : Unit = {
    model.executeAction(actionId, direction)
    updateTurn()
    cellsInRange(model.lastExecutedActionId)
  }

  def updateTurn() : Unit = {
    val winnerId = model.winnerId
    if (winnerId.isDefined) {
      publish(PlayerWon(winnerId.get, model.wonImagePath))
    } else if (model.turnOver) {
      model.nextTurn
      publish(TurnStarted(model.activePlayerNumber))
    }
  }

  override def cellsInRange(actionId: Option[Int]) : Unit = {
    publish(CellsInRange(convertToUiIndex(model.cellsInRange(actionId))))
  }

  private def convertToUiIndex(indexes: List[(Int, Int)]) : List[(Int, Int)] = {
    indexes.map((s) => s.swap)
  }

  override def executeAction(actionId: Int, rowIndex: Int, columnIndex: Int) : Unit = {
    model.executeAction(actionId, rowIndex, columnIndex)
    updateTurn()
    cellsInRange(model.lastExecutedActionId)
  }

  override def canExecuteAction(actionId: Int, direction: Direction) : Boolean = {
    model.canExecuteAction(actionId, direction)
  }

  override def canExecuteAction(actionId: Int, rowIndex: Int, columnIndex: Int) : Boolean = {
    model.canExecuteAction(actionId, rowIndex, columnIndex)
  }

  override def actionIds(playerNumber: Int) : List[Int] = {
    model.actionIdsForPlayer(playerNumber)
  }

  override def actionDescription(actionId: Int) : String = {
    model.actionDescription(actionId)
  }

  override def actionIconPath(actionId: Int) : Option[String] = {
    model.actionIconPath(actionId)
  }

  override def rowCount : Int = {
    model.rowCount
  }

  override def columnCount : Int = {
    model.columnCount
  }

  override def levelBackgroundImagePath: String = {
    model.levelBackgroundImagePath
  }

  override def actionbarBackgroundImagePath: String = {
    model.actionbarBackgroundImagePath
  }

  override def activePlayerNumber : Int = {
    model.activePlayerNumber
  }

  override def playerName(playerNumber: Int) : String = {
    model.activePlayerName
  }

  override def reset : Unit = {
    model.reset
    updateTurn()
    publish(TurnStarted(model.activePlayerNumber))
  }

  override def turnCounter : Int = {
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
