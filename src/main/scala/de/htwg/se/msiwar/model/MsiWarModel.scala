package de.htwg.se.msiwar.model

trait MsiWarModel {
  def actionIdsForPlayer(playerNumber: Int): List[Int]
  def actionHotKey(actionId: Int) : String
  def actionDescription(actionId: Int) : String
  def actionIconPath(actionId: Int) : String

  def reset: Unit

  def activePlayerNumber: Int
}
