package de.htwg.se.msiwar.model

abstract class GameObject(val name: String, val imagePath: String, val position: Position) {
}

case class PlayerObject(override val name: String, override val imagePath: String, override val position: Position, playerNumber: Int, actionPoints: Int, healthPoints: Int, skills: List[Action]) extends GameObject(name, imagePath, position) {
  def hasActionPointsLeft: Boolean = {
    actionPoints > 0
  }

  def hasHealthPointsLeft: Boolean = {
    healthPoints > 0
  }
}
