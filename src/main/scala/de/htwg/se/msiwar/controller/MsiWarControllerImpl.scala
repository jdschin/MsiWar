package de.htwg.se.msiwar.controller

import de.htwg.se.msiwar.model.MsiWarModel

class MsiWarControllerImpl(model: MsiWarModel) extends MsiWarController {

  override def cellContentToText(rowIndex: Int, columnIndex: Int) = {"row:" + rowIndex + " column:" + columnIndex}
  override def highlightCell(rowIndex: Int, columnIndex: Int) = {publish(CellChanged(rowIndex, columnIndex, true))}
  override def isCellInRange(rowIndex: Int, columnIndex: Int) = {true}

  override def startActionMode(actionId: Int) = {}
  override def stopActionMode(actionId: Int) = {}
  override def executeAction(actionId: Int) = {}

  override def actionIds(playerNumber: Int) : List[Int] = {model.actionIdsForPlayer(playerNumber)}
  override def actionHotKey(actionId: Int) : String = {model.actionHotKey(actionId)}
  override def actionDescription(actionId: Int) : String = {model.actionDescription(actionId)}
  override def actionIconPath(actionId: Int) : String = {model.actionIconPath(actionId)}

  override def columnCount = {1}
  override def rowCount = {1}
  override def backgroundPath = {""}

  override def reset = {
    model.reset
  }
}
