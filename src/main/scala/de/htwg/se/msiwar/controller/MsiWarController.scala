package de.htwg.se.msiwar.controller

import scala.swing.Publisher
import scala.swing.event.Event

case class CellChanged(rowIndex: Int, columnIndex: Int) extends Event

trait MsiWarController extends Publisher{
  def getCellContentToText(rowIndex: Int, columnIndex: Int): String
  def highlightCell(rowIndex: Int, columnIndex: Int) : Unit
  def isCellInRange(rowIndex: Int, columnIndex: Int) : Boolean

  def startActionMode(actionId: Int) : Unit
  def stopActionMode(actionId: Int) : Unit
  def executeAction(actionId: Int) : Unit

  def getActionIds : Set[Int]
  def getActionHotkey(actionId: Int) : String
  def getActionDescription(actionId: Int) : String
  def getActionIconPath(actionId: Int) : String

  def getColumnCount : Int
  def getRowCount : Int
  def getBackgroundPath : String

  def reset : Unit
}
