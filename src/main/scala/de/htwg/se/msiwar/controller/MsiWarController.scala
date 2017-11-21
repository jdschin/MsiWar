package de.htwg.se.msiwar.controller

import scala.swing.Publisher

trait MsiWarController extends Publisher{
  def highlightCell(rowIndex: Int, columnIndex: Int) : Unit
  def isCellInRange(rowIndex: Int, columnIndex: Int) : Boolean

  def startActionMode(actionId: Int) : Unit
  def stopActionMode(actionId: Int) : Unit
  def executeAction(actionId: Int) : Unit

  def getActionDescription(actionId: Int) : String
  def getActionIconPath(actionId: Int) : String

  def getColumnCount : Int
  def getRowCount : Int
  def getBackgroundPath : String

  def reset : Unit
}
