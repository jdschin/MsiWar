package de.htwg.se.msiwar.model

import akka.actor.Actor

sealed trait GenerationMessage

case object Generate extends GenerationMessage

case class Work(numberOfPlayers: Int, rowCount: Int, columnCount: Int) extends GenerationMessage

case class Result(gameObjectsOpt: Option[List[GameObject]]) extends GenerationMessage

case class GeneratedGameObjects(gameObjects: List[GameObject])

class GameGenerationWorker extends Actor {

  def generate(numberOfPlayers: Int, rowCount: Int, columnCount: Int): Option[List[GameObject]] = {
    GameGenerator(numberOfPlayers, rowCount, columnCount).generate()
  }

  def receive: PartialFunction[Any, Unit] = {
    case Work(numberOfPlayers: Int, rowCount: Int, columnCount: Int) =>
      sender ! Result(generate(numberOfPlayers: Int, rowCount: Int, columnCount: Int))
  }
}
