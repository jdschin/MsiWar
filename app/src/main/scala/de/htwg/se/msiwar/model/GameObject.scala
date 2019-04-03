package de.htwg.se.msiwar.model

import de.htwg.se.msiwar.util.Direction.Direction

abstract case class GameObject(name: String, imagePath: String, position: Position)

case class BlockObject(override val name: String, override val imagePath: String, override val position: Position) extends GameObject(name, imagePath, position)

case class PlayerObject(override val name: String,
                        override val imagePath: String,
                        override val position: Position,
                        viewDirection: Direction,
                        playerNumber: Int,
                        wonImagePath: String,
                        actionPoints: Int,
                        maxActionPoints: Int,
                        healthPoints: Int,
                        maxHealthPoints: Int,
                        actions: List[Action]) extends GameObject(name, imagePath, position)