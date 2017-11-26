package de.htwg.se.msiwar.controller

import de.htwg.se.msiwar.model.MsiWarModel

import scala.collection.immutable.HashSet

class MsiWarControllerImpl(model: MsiWarModel) extends MsiWarController {

  override def getCellContentToText(rowIndex: Int, columnIndex: Int) = {"row:" + rowIndex + " column:" + columnIndex}
  override def highlightCell(rowIndex: Int, columnIndex: Int) = {publish(new CellChanged(rowIndex, columnIndex))}
  override def isCellInRange(rowIndex: Int, columnIndex: Int) = {true}

  override def startActionMode(actionId: Int) = {}
  override def stopActionMode(actionId: Int) = {}
  override def executeAction(actionId: Int) = {}

  override def getActionIds = {new HashSet[Int]}
  override def getActionHotkey(actionId: Int) = {""}
  override def getActionDescription(actionId: Int) = {""}
  override def getActionIconPath(actionId: Int) = {""}

  override def getColumnCount = {1}
  override def getRowCount = {1}
  override def getBackgroundPath = {""}

  override def reset = {}
}
