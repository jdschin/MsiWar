package de.htwg.se.msiwar.util

import de.htwg.se.msiwar.model.{Action, BlockObject, PlayerObject, Position}
import de.htwg.se.msiwar.model.ActionType._


class GameConfigProvider(configFilePath: String) {
  //TODO: sort players by number
  //TODO: verify player numbers
  //TODO: verify object positions by row and column count
  //TODO: generate levels

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

  val wood1 = BlockObject("B", "src/main/resources/images/block_wood.png", Position(0, 0))
  val wood2 = BlockObject("B", "src/main/resources/images/block_wood.png", Position(0, 1))
  val wood3 = BlockObject("B", "src/main/resources/images/block_wood.png", Position(3, 7))
  val wood4 = BlockObject("B", "src/main/resources/images/block_wood.png", Position(8, 8))
  val wood5 = BlockObject("B", "src/main/resources/images/block_wood.png", Position(5, 4))
  val wood6 = BlockObject("B", "src/main/resources/images/block_wood.png", Position(3, 2))
  val wood7 = BlockObject("B", "src/main/resources/images/block_wood.png", Position(3, 3))
  val wood8 = BlockObject("B", "src/main/resources/images/block_wood.png", Position(5, 0))
  val wood9 = BlockObject("B", "src/main/resources/images/block_wood.png", Position(6, 0))
  val wood10 = BlockObject("B", "src/main/resources/images/block_wood.png", Position(5, 8))
  val wood11 = BlockObject("B", "src/main/resources/images/block_wood.png", Position(6, 8))

  val mountain1 = BlockObject("B", "src/main/resources/images/block_mountain.png", Position(7, 2))
  val mountain2 = BlockObject("B", "src/main/resources/images/block_mountain.png", Position(6, 6))
  val mountain3 = BlockObject("B", "src/main/resources/images/block_mountain.png", Position(5, 3))
  val mountain4 = BlockObject("B", "src/main/resources/images/block_mountain.png", Position(3, 1))
  val mountain5 = BlockObject("B", "src/main/resources/images/block_mountain.png", Position(6, 2))
  val mountain6 = BlockObject("B", "src/main/resources/images/block_mountain.png", Position(0, 8))
  val mountain7 = BlockObject("B", "src/main/resources/images/block_mountain.png", Position(1, 8))
  val mountain8 = BlockObject("B", "src/main/resources/images/block_mountain.png", Position(0, 3))
  val mountain9 = BlockObject("B", "src/main/resources/images/block_mountain.png", Position(0, 4))

  val lake1 = BlockObject("B", "src/main/resources/images/block_lake.png", Position(1, 6))
  val lake2 = BlockObject("B", "src/main/resources/images/block_lake.png", Position(8, 1))

  val city1 = BlockObject("B", "src/main/resources/images/block_city.png", Position(3, 5))

  val gameObjects = List(player1, player2, wood1, wood2, wood3, wood4, wood5, wood6, wood7, wood8, wood9, wood10, wood11, mountain1, mountain2, mountain3, mountain4, mountain5, mountain6, mountain7, mountain8, mountain9, lake1, lake2, city1)
}
