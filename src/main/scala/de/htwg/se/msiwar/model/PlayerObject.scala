package de.htwg.se.msiwar.model

case class PlayerObject(name: String, imagePath: String, position: Position, playerNumber: Int, actionPoints: Int, healthPoints: Int, skills: List[Action]) extends GameObject(name, imagePath, position) {
  def hasActionPointsLeft: Boolean = {
    actionPoints > 0
  }

  def hasHealthPointsLeft: Boolean = {
    healthPoints > 0
  }
}