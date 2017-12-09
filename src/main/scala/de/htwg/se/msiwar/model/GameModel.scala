package de.htwg.se.msiwar.model

trait GameModel {
  def gameObjectAt(rowIndex: Int, columnIndex: Int): Option[GameObject]

  def actionIdsForPlayer(playerNumber: Int): List[Int]
  def actionHotKey(actionId: Int) : String
  def actionDescription(actionId: Int) : String
  def actionIconPath(actionId: Int) : String

  def activePlayerNumber: Int

  def rowCount: Int
  def columnCount: Int

  def reset: Unit
}
