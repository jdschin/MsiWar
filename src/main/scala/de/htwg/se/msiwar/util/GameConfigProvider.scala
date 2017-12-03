package de.htwg.se.msiwar.util

import de.htwg.se.msiwar.model.{Action, PlayerObject, Position}

class GameConfigProvider(configFilePath: String) {
  // Setup board
  val rowCount = 9
  val colCount = 9

  // Setup actions
  val moveAction = new Action(1, "Panzer bewegen", "arrow.png", "move.wav", 1, 1)
  val shootAction = new Action(1, "Schießen", "bullet.png", "shoot.wav", 1, 1)
  val actions = List(moveAction, shootAction)

  // Setup players
  val player1 = new PlayerObject("Spieler1", "tank_red.png", Position(0, 0), 1, 3, 3, actions)
  val player2 = new PlayerObject("Spieler2", "tank_blue.png", Position(0, 0), 2, 3, 3, actions)
  val gameObjects = List(player1, player2)
}
