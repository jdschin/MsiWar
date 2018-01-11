package de.htwg.se.msiwar.model

import akka.actor.Actor

sealed trait GenerationMessage

case object Generate extends GenerationMessage

case class Work(rowCount: Int, columnCount: Int) extends GenerationMessage

case class Result(gameObjectsOpt: Option[List[GameObject]]) extends GenerationMessage

case class GeneratedGameObjects(gameObjects: List[GameObject])

class GameGenerationWorker extends Actor {

  def generate(rowCount: Int, columnCount: Int): Option[List[GameObject]] = {
    try {
      GameGenerator(rowCount, columnCount).generate()
    } catch {
      case _: Exception => Option.empty
    }
  }

  def receive: PartialFunction[Any, Unit] = {
    case Work(rowCount: Int, columnCount: Int) =>
      sender ! Result(generate(rowCount, columnCount))
  }
}
