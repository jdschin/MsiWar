package de.htwg.se.msiwar.controller

class MsiWarControllerImpl extends MsiWarController {
  override def highlightCell(rowIndex: Int, columnIndex: Int): Unit = {}
  override def isCellInRange(rowIndex: Int, columnIndex: Int): Boolean = {true}

  override def startActionMode(actionId: Int): Unit = {}
  override def stopActionMode(actionId: Int): Unit = {}
  override def executeAction(actionId: Int): Unit = {}

  override def getActionDescription(actionId: Int): String = {""}
  override def getActionIconPath(actionId: Int): String = {""}

  override def getColumnCount: Int = {1}
  override def getRowCount: Int = {1}
  override def getBackgroundPath: String = {""}

  override def reset: Unit = {}
}
