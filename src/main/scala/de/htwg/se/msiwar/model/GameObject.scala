package de.htwg.se.msiwar.model

abstract class GameObject(val name: String, val imagePath: String, val position: Position)

case class BlockObject(override val name: String, override val imagePath: String, override val position: Position) extends GameObject(name, imagePath, position)

case class PlayerObject(override val name: String, override val imagePath: String, override val position: Position, playerNumber: Int, private val maxActionPoints: Int, var healthPoints: Int, actions: List[Action]) extends GameObject(name, imagePath, position) {

  var currentActionPoints: Int = maxActionPoints

  def hasActionPointsLeft: Boolean = {
    currentActionPoints > 0
  }

  def hasHealthPointsLeft: Boolean = {
    currentActionPoints > 0
  }

  def resetActionPoints: Unit = {
    currentActionPoints = maxActionPoints
  }
}
