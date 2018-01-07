package de.htwg.se.msiwar.model

import akka.actor.{Actor, Props}
import akka.routing.RoundRobinPool

class GameGenerationMaster(numberOfWorkers: Int, numberOfMessages: Int, numberOfPlayers: Int, rowCount: Int, columnCount: Int, actions: List[Action], completion: (List[GameObject]) => Unit)
  extends Actor {

  val workerRouter = context.actorOf(Props[GameGenerationWorker].withRouter(RoundRobinPool(numberOfWorkers)), name = "workerRouter")

  def receive = {
    case Generate =>
      for (_  <- 0 until numberOfMessages) workerRouter ! Work(numberOfPlayers, rowCount, columnCount, actions)
    case Result(gameObjectsOpt) =>

      if (gameObjectsOpt.isDefined) {
        completion(gameObjectsOpt.get)
        context.stop(self)
      }
  }

}