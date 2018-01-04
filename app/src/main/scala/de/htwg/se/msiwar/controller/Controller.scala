package de.htwg.se.msiwar.controller

import de.htwg.se.msiwar.util.Direction.Direction

import scala.swing.Publisher
import scala.swing.event.Event

case class GameStarted() extends Event
case class CellChanged(rowColumnIndexes: List[(Int, Int)]) extends Event
case class CellsInRange(rowColumnIndexes: List[(Int, Int)]) extends Event
case class TurnStarted(playerNumber: Int) extends Event
case class PlayerWon(playerNumber: Int, wonImagePath: String) extends Event
case class PlayerStatsChanged(playerNumber: Int, newActionPoints: Int) extends Event
case class AttackActionResult(rowIndex: Int, columnIndex: Int, hit: Boolean, attackImagePath: String, attackSoundPath: String) extends Event

trait Controller extends Publisher{
  def cellContentToText(rowIndex: Int, columnIndex: Int) : String
  def cellContentImagePath(rowIndex: Int, columnIndex: Int) : Option[String]

  /**
    * Calculates the cells in range for active player and given actionId.
    * when no action id is enabled, result is always empty.
    * Result is published by CellsInRange Event.
    **/
  def cellsInRange(actionId: Option[Int]) : Unit

  def executeAction(actionId: Int, direction:Direction) : Unit
  def executeAction(actionId: Int, rowIndex: Int, columnIndex: Int) : Unit
  def canExecuteAction(actionId: Int, direction:Direction) : Boolean
  def canExecuteAction(actionId: Int, rowIndex: Int, columnIndex: Int) : Boolean

  def actionIds(playerNumber: Int) : Set[Int]
  def actionDescription(actionId: Int) : String
  def actionIconPath(actionId: Int) : Option[String]
  def actionDamage(actionId: Int) : Int
  def actionRange(actionId: Int) : Int

  def scenarioIds : Set[Int]
  def scenarioName(scenarioId: Int) : Option[String]

  def columnCount : Int
  def rowCount : Int

  def openingBackgroundImagePath : String
  def levelBackgroundImagePath : String
  def actionbarBackgroundImagePath : String
  def appIconImagePath : String

  def activePlayerNumber : Int
  def activePlayerActionPoints : Int
  def activePlayerHealthPoints : Int
  def playerName(playerNumber: Int) : String

  def turnCounter : Int

  def startGame(scenarioId: Int) : Unit
}
