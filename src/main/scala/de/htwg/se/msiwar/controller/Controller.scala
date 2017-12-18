package de.htwg.se.msiwar.controller

import de.htwg.se.msiwar.util.Direction.Direction

import scala.swing.Publisher
import scala.swing.event.Event

case class CellChanged(rowIndex: Int, columnIndex: Int, valid: Boolean) extends Event
case class TurnStarted(playerNumber: Int) extends Event
case class TurnEnded(playerNumber: Int) extends Event

trait Controller extends Publisher{
  def cellContentToText(rowIndex: Int, columnIndex: Int): String
  def cellContentImagePath(rowIndex: Int, columnIndex: Int): Option[String]
  def highlightCell(rowIndex: Int, columnIndex: Int) : Unit
  def isCellInRange(rowIndex: Int, columnIndex: Int) : Boolean

  def startActionMode(actionId: Int) : Unit
  def stopActionMode(actionId: Int) : Unit
  def executeAction(actionId: Int, direction:Direction) : Unit
  def canExecuteAction(actionId: Int, direction:Direction) : Boolean

  def actionIds(playerNumber: Int) : List[Int]
  def actionHotKey(actionId: Int) : String
  def actionDescription(actionId: Int) : String
  def actionIconPath(actionId: Int) : String

  def columnCount : Int
  def rowCount : Int
  def backgroundPath : String

  def activePlayerNumber : Int
  def playerName(playerNumber: Int) : String

  def turnCounter : Int

  def reset : Unit
}
