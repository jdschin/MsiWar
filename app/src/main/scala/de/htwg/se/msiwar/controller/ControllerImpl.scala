package de.htwg.se.msiwar.controller

import de.htwg.se.msiwar.model._
import de.htwg.se.msiwar.util.Direction.Direction

case class ControllerImpl(var model: GameModel) extends Controller {
  listenTo(model)

  reactions += {
    case e: ModelCellChanged => publish(CellChanged(e.rowColumnIndexes))
    case _: ModelPlayerStatsChanged => publish(PlayerStatsChanged(model.activePlayerNumber, model.activePlayerActionPoints))
    case e: ModelAttackResult => publish(AttackResult(e.rowIndex, e.columnIndex, e.hit, e.attackImagePath, e.attackSoundPath))
    case e: ModelTurnStarted => publish(TurnStarted(e.playerNumber))
    case e: ModelPlayerWon => publish(PlayerWon(e.playerNumber, e.wonImagePath))
    case _: ModelGameStarted => publish(GameStarted())
    case _: ModelCouldNotGenerateGame => publish(CouldNotGenerateGame())
  }

  override def cellContentToText(rowIndex: Int, columnIndex: Int): String = {
    model.cellContentToText(rowIndex, columnIndex)
  }

  override def cellContent(rowIndex: Int, columnIndex: Int) : Option[GameObject] = {
    model.cellContent(rowIndex, columnIndex)
  }

  override def cellContentImagePath(rowIndex: Int, columnIndex: Int): Option[String] = {
    model.cellContentImagePath(rowIndex, columnIndex)
  }

  override def executeAction(actionId: Int, direction: Direction): Unit = {
    model = model.executeAction(actionId, direction)
    checkAfterActionExecution
  }

  override def executeAction(actionId: Int, rowIndex: Int, columnIndex: Int): Unit = {
    model = model.executeAction(actionId, rowIndex, columnIndex)
    checkAfterActionExecution
  }

  private def checkAfterActionExecution = {
    if (model.winnerId.isDefined) {
      publish(ModelPlayerWon(model.winnerId.get, model.wonImagePath))
    }
    publish(ModelPlayerStatsChanged())
    publish(TurnStarted(model.activePlayerNumber))
    cellsInRange(model.lastExecutedActionId)
  }

  override def cellsInRange(actionId: Option[Int]): List[(Int, Int)] = {
    val cells = model.cellsInRange(actionId)
    publish(CellsInRange(cells))
    cells
  }

  override def canExecuteAction(actionId: Int, direction: Direction): Boolean = {
    model.canExecuteAction(actionId, direction)
  }

  override def canExecuteAction(actionId: Int, rowIndex: Int, columnIndex: Int): Boolean = {
    model.canExecuteAction(actionId, rowIndex, columnIndex)
  }

  override def actionIds(playerNumber: Int): Set[Int] = {
    model.actionIdsForPlayer(playerNumber).get
  }

  override def actionDescription(actionId: Int): String = {
    model.actionDescription(actionId)
  }

  override def actionIconPath(actionId: Int): Option[String] = {
    model.actionIconPath(actionId)
  }

  override def scenarioIds: Set[Int] = {
    model.scenarioIds
  }

  override def scenarioName(scenarioId: Int): Option[String] = {
    model.scenarioName(scenarioId)
  }

  override def rowCount: Int = {
    model.rowCount
  }

  override def columnCount: Int = {
    model.columnCount
  }

  override def openingBackgroundImagePath: String = {
    model.gameConfigProvider.openingBackgroundImagePath
  }

  override def levelBackgroundImagePath: String = {
    model.gameConfigProvider.levelBackgroundImagePath
  }

  override def actionbarBackgroundImagePath: String = {
    model.gameConfigProvider.actionbarBackgroundImagePath
  }

  override def activePlayerNumber: Int = {
    model.activePlayerNumber
  }

  override def activePlayerName: String = {
    model.activePlayerName
  }

  override def startGame(scenarioId: Int): Unit = {
    model = model.startGame(scenarioId)

    publish(GameStarted())
    model = model.updateTurn
    publish(TurnStarted(model.activePlayerNumber))
  }

  override def turnCounter: Int = {
    model.turnCounter
  }

  override def actionPointCost(actionId: Int): Int = {
    model.actionPointCost(actionId)
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

  override def appIconImagePath: String = {
    model.gameConfigProvider.appIconImagePath
  }

  override def startRandomGame(): Unit = {
    model = model.startRandomGame()

    publish(GameStarted())
    model = model.updateTurn
    publish(TurnStarted(model.activePlayerNumber))
  }
}
