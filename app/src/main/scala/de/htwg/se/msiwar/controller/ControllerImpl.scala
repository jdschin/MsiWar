package de.htwg.se.msiwar.controller

import java.io.FileNotFoundException

import de.htwg.se.msiwar.model._
import de.htwg.se.msiwar.util.Direction.Direction
import de.htwg.se.msiwar.util.{GameConfigProvider, JSONException}

case class ScenarioNotFoundException(message: String) extends Exception

class ControllerImpl extends Controller {
  private var model: GameModel = createModel
  private val scenariosById = new scala.collection.mutable.HashMap[Int, String]()
  private val availableScenarios = GameConfigProvider.listScenarios
  for (i <- availableScenarios.indices) {
    scenariosById.put(i, availableScenarios(i))
  }

  reactions += {
    case e: GameBoardChanged => publish(CellChanged(e.rowColumnIndexes))
    case _: ActivePlayerStatsChanged => publish(PlayerStatsChanged(model.activePlayerNumber, model.activePlayerActionPoints))
    case e: AttackResult => publish(AttackActionResult(e.rowIndex, e.columnIndex, e.hit, e.attackImagePath, e.attackSoundPath))
  }

  override def cellContentToText(rowIndex: Int, columnIndex: Int): String = {
    model.cellContentToText(rowIndex, columnIndex)
  }

  override def cellContentImagePath(rowIndex: Int, columnIndex: Int): Option[String] = {
    model.cellContentImagePath(rowIndex, columnIndex)
  }

  override def executeAction(actionId: Int, direction: Direction): Unit = {
    model.executeAction(actionId, direction)
    updateTurn()
    cellsInRange(model.lastExecutedActionId)
  }

  override def executeAction(actionId: Int, rowIndex: Int, columnIndex: Int): Unit = {
    model.executeAction(actionId, rowIndex, columnIndex)
    updateTurn()
    cellsInRange(model.lastExecutedActionId)
  }

  def updateTurn(): Unit = {
    val winnerId = model.winnerId
    if (winnerId.isDefined) {
      publish(PlayerWon(winnerId.get, model.wonImagePath))
    } else if (model.turnOver) {
      model.nextTurn
      publish(TurnStarted(model.activePlayerNumber))
    }
  }

  override def cellsInRange(actionId: Option[Int]): Unit = {
    publish(CellsInRange(model.cellsInRange(actionId)))
  }

  override def canExecuteAction(actionId: Int, direction: Direction): Boolean = {
    model.canExecuteAction(actionId, direction)
  }

  override def canExecuteAction(actionId: Int, rowIndex: Int, columnIndex: Int): Boolean = {
    model.canExecuteAction(actionId, rowIndex, columnIndex)
  }

  override def actionIds(playerNumber: Int): Set[Int] = {
    model.actionIdsForPlayer(playerNumber)
  }

  override def actionDescription(actionId: Int): String = {
    model.actionDescription(actionId)
  }

  override def actionIconPath(actionId: Int): Option[String] = {
    model.actionIconPath(actionId)
  }

  override def scenarioIds: Set[Int] = {
    scenariosById.keys.toSet
  }

  override def scenarioName(scenarioId: Int): Option[String] = {
    val scenarioNameOpt = scenariosById.get(scenarioId)
    if(scenarioNameOpt.isDefined) {
      val scenarioName = scenarioNameOpt.get
      Option(scenarioName.substring(0, scenarioName.lastIndexOf('.')).replace('_', ' '))
    } else {
      scenarioNameOpt
    }
  }

  override def rowCount: Int = {
    model.rowCount
  }

  override def columnCount: Int = {
    model.columnCount
  }

  override def openingBackgroundImagePath: String = {
    model.openingBackgroundImagePath
  }

  override def levelBackgroundImagePath: String = {
    model.levelBackgroundImagePath
  }

  override def actionbarBackgroundImagePath: String = {
    model.actionbarBackgroundImagePath
  }

  override def activePlayerNumber: Int = {
    model.activePlayerNumber
  }

  override def playerName(playerNumber: Int): String = {
    model.activePlayerName
  }

  override def startGame(scenarioId: Int): Unit = {
    try {
      val scenarioName = scenariosById.get(scenarioId)
      if(scenariosById.get(scenarioId).isDefined) {
        GameConfigProvider.loadFromFile("src/main/resources/scenarios/" +scenarioName.get)
      } else {
        throw ScenarioNotFoundException("Found no scenario for id" + scenarioId)
      }
    }
    catch {
      case e: FileNotFoundException => print(e.getMessage)
      case e: JSONException => print(e.getMessage)
      case e: NoSuchElementException => print(e.getMessage)
    }
    model = createModel
    listenTo(model)

    // Fire initial events
    publish(GameStarted())
    updateTurn()
    publish(TurnStarted(model.activePlayerNumber))
  }

  private def createModel: GameModel = {
    val createdModel = GameModelImpl(GameConfigProvider.rowCount, GameConfigProvider.colCount, GameConfigProvider.gameObjects, GameConfigProvider.levelBackgroundImagePath, GameConfigProvider.actionbarBackgroundImagePath, GameConfigProvider.attackImagePath, GameConfigProvider.attackSoundPath, GameConfigProvider.openingBackgroundImagePath)
    createdModel
  }

  override def turnCounter: Int = {
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
