package de.htwg.se.msiwar.model

import java.util.UUID

import akka.actor.Actor
import de.htwg.se.msiwar.util.Direction

sealed trait GenerationMessage

case object Generate extends GenerationMessage

case class Work(numberOfPlayers: Int, rowCount: Int, columnCount: Int, actions: List[Action]) extends GenerationMessage

case class Result(gameObjectsOpt: Option[List[GameObject]]) extends GenerationMessage

case class GeneratedGameObjects(gameObjects: List[GameObject])

class GameGenerationWorker extends Actor {

  def generate(numberOfPlayers: Int, rowCount: Int, columnCount: Int, actions: List[Action]): Option[List[GameObject]] = {
    // TODO: generate level
    val player1 = PlayerObject("Spieler1", "images/light_tank_red.png", Position(1, 2), Direction.DOWN, playerNumber = 1, "images/background_won_red.png", maxActionPoints = 3, maxHealthPoints = 3, actions)
    val player2 = PlayerObject("Spieler2", "images/medium_tank_blue.png", Position(7, 6), Direction.LEFT, playerNumber = 2, "images/background_won_blue.png", maxActionPoints = 3, maxHealthPoints = 3, actions)

    val wood1 = BlockObject("B", "images/block_wood.png", Position(0, 0))
    val wood2 = BlockObject("B", "images/block_wood.png", Position(0, 1))
    val wood3 = BlockObject("B", "images/block_wood.png", Position(3, 7))
    val wood4 = BlockObject("B", "images/block_wood.png", Position(8, 8))
    val wood5 = BlockObject("B", "images/block_wood.png", Position(5, 4))
    val wood6 = BlockObject("B", "images/block_wood.png", Position(3, 2))
    val wood7 = BlockObject("B", "images/block_wood.png", Position(3, 3))
    val wood8 = BlockObject("B", "images/block_wood.png", Position(5, 0))
    val wood9 = BlockObject("B", "images/block_wood.png", Position(6, 0))
    val wood10 = BlockObject("B", "images/block_wood.png", Position(5, 8))
    val wood11 = BlockObject("B", "images/block_wood.png", Position(6, 8))

    val mountain1 = BlockObject("B", "images/block_mountain.png", Position(7, 2))
    val mountain2 = BlockObject("B", "images/block_mountain.png", Position(6, 6))
    val mountain3 = BlockObject("B", "images/block_mountain.png", Position(5, 3))
    val mountain4 = BlockObject("B", "images/block_mountain.png", Position(3, 1))
    val mountain5 = BlockObject("B", "images/block_mountain.png", Position(6, 2))
    val mountain6 = BlockObject("B", "images/block_mountain.png", Position(0, 8))
    val mountain7 = BlockObject("B", "images/block_mountain.png", Position(1, 8))
    val mountain8 = BlockObject("B", "images/block_mountain.png", Position(0, 3))
    val mountain9 = BlockObject("B", "images/block_mountain.png", Position(0, 4))

    val lake1 = BlockObject("B", "images/block_lake.png", Position(1, 6))
    val lake2 = BlockObject("B", "images/block_lake.png", Position(8, 1))

    val city1 = BlockObject("B", "images/block_city.png", Position(3, 5))
    val r = scala.util.Random
    if (r.nextBoolean()) {
      Option(List(player1, player2, wood1, wood2, wood3, wood4, wood5, wood6, wood7, wood8, wood9, wood10, wood11, mountain1, mountain2, mountain3, mountain4, mountain5, mountain6, mountain7, mountain8, mountain9, lake1, lake2, city1))
    } else {
      Option.empty
    }
  }

  def receive: PartialFunction[Any, Unit] = {
    case Work(numberOfPlayers: Int, rowCount: Int, columnCount: Int, actions: List[Action]) =>
      sender ! Result(generate(numberOfPlayers: Int, rowCount: Int, columnCount: Int, actions))
  }
}
