package de.htwg.se.msiwar.util

import de.htwg.se.msiwar.model.{Action, PlayerObject, Position}
import de.htwg.se.msiwar.model.ActionType._


class GameConfigProvider(configFilePath: String) {
  //TODO: sort players by number
  //TODO: verify player numbers

  // Setup board
  val rowCount = 9
  val colCount = 9

  // Setup actions
  val moveAction = Action(1, "Panzer bewegen", "arrow.png", "move.wav", 1, 1, MOVE, 0)
  val shootAction = Action(2, "Schießen", "bullet.png", "shoot.wav", 1, 1, SHOOT, 3)
  val actions = List(moveAction, shootAction)

  // Setup players
  val player1 = PlayerObject("Spieler1", "src/main/resources/images/tank_red.png", Position(1, 2), 1, 3, 3, actions)
  val player2 = PlayerObject("Spieler2", "src/main/resources/images/tank_blue.png", Position(7, 6), 2, 3, 3, actions)
  val gameObjects = List(player1, player2)
}
