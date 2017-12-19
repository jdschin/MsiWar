package de.htwg.se.msiwar.model

import de.htwg.se.msiwar.util.Direction.Direction

import scala.swing.Publisher
import scala.swing.event.Event

case class GameBoardChanged(rowColumnIndexes: List[(Int, Int)]) extends Event

trait GameModel extends Publisher {
  def gameObjectAt(rowIndex: Int, columnIndex: Int): Option[GameObject]

  def actionIdsForPlayer(playerNumber: Int): List[Int]
  def actionHotKey(actionId: Int): String
  def actionDescription(actionId: Int): String
  def actionIconPath(actionId: Int): String

  def executeAction(actionId: Int, direction:Direction): Unit
  def canExecuteAction(actionId: Int, direction: Direction) : Boolean

  def activePlayerNumber: Int
  def activePlayerName: String

  def rowCount: Int
  def columnCount: Int

  def turnCounter: Int
  def turnOver: Boolean
  def nextTurn: Int

  def reset: Unit
}
