package de.htwg.se.msiwar.model

case class GameBoard(rows: Int, columns: Int, gameObjects: List[GameObject]) {
  def reset: Unit = {}
}
