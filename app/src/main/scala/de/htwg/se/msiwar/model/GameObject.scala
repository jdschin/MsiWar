package de.htwg.se.msiwar.model

import de.htwg.se.msiwar.util.Direction.Direction

abstract class GameObject(val name: String, val imagePath: String, val position: Position)

case class BlockObject(override val name: String, override val imagePath: String, override val position: Position) extends GameObject(name, imagePath, position)

case class PlayerObject(override val name: String, override val imagePath: String, override val position: Position, viewDirection: Direction, playerNumber: Int, wonImagePath: String, private val maxActionPoints: Int, private var maxHealthPoints: Int, actions: List[Action]) extends GameObject(name, imagePath, position) {

  var currentActionPoints: Int = maxActionPoints
  var currentHealthPoints: Int = maxHealthPoints

  def hasActionPointsLeft: Boolean = {
    currentActionPoints > 0
  }

  def hasHealthPointsLeft: Boolean = {
    currentHealthPoints > 0
  }

  def resetActionPoints(): Unit = {
    currentActionPoints = maxActionPoints
  }

  def resetHealthPoints(): Unit = {
    currentHealthPoints = maxHealthPoints
  }
}
