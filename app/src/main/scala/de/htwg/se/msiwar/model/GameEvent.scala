package de.htwg.se.msiwar.model

import scala.swing.event.Event

case class GameStarted() extends Event
case class CouldNotGenerateGame() extends Event
case class CellChanged(rowColumnIndexes: List[(Int, Int)]) extends Event
case class CellsInRange(rowColumnIndexes: List[(Int, Int)]) extends Event
case class TurnStarted(playerNumber: Int) extends Event
case class PlayerWon(playerNumber: Int, wonImagePath: String) extends Event
case class PlayerStatsChanged(playerNumber: Int, newActionPoints: Int) extends Event
case class AttackResult(rowIndex: Int, columnIndex: Int, hit: Boolean, attackImagePath: String, attackSoundPath: String) extends Event

trait GameEvent extends Event {

}
