package de.htwg.se.msiwar.model

import de.htwg.se.msiwar.util.Direction.Direction

import scala.swing.Publisher
import scala.swing.event.Event

case class ModelGameStarted() extends Event
case class ModelCellChanged(rowColumnIndexes: List[(Int, Int)]) extends Event
case class ModelPlayerStatsChanged() extends Event
case class ModelAttackResult(rowIndex: Int, columnIndex: Int, hit: Boolean, attackImagePath: String, attackSoundPath: String) extends Event
case class ModelTurnStarted(playerNumber: Int) extends Event
case class ModelPlayerWon(playerNumber: Int, wonImagePath: String) extends Event

trait GameModel extends Publisher {
  def actionIdsForPlayer(playerNumber: Int): Set[Int]
  def actionDescription(actionId: Int): String
  def actionIconPath(actionId: Int): Option[String]
  def actionDamage(actionId: Int) : Int
  def actionRange(actionId: Int) : Int

  def cellContentToText(rowIndex: Int, columnIndex: Int): String
  def cellContentImagePath(rowIndex: Int, columnIndex: Int): Option[String]
  def cellsInRange(actionId: Option[Int]): List[(Int, Int)]

  def openingBackgroundImagePath: String
  def levelBackgroundImagePath: String
  def actionbarBackgroundImagePath: String
  def wonImagePath: String
  def appIconImagePath: String

  def executeAction(actionId: Int, direction:Direction): Unit
  def executeAction(actionId: Int, rowIndex: Int, columnIndex: Int): Unit
  def canExecuteAction(actionId: Int, direction: Direction) : Boolean
  def canExecuteAction(actionId: Int, rowIndex: Int, columnIndex: Int) : Boolean
  def lastExecutedActionId: Option[Int]

  def activePlayerNumber: Int
  def activePlayerName: String
  def activePlayerActionPoints: Int
  def activePlayerHealthPoints: Int

  def scenarioIds: Set[Int]
  def scenarioName(scenarioId: Int): Option[String]
  def startGame(scenarioId: Int) : Unit

  def rowCount: Int
  def columnCount: Int

  def turnCounter: Int
  def winnerId: Option[Int]
}
