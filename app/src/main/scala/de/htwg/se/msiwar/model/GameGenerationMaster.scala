package de.htwg.se.msiwar.model

import akka.actor.{Actor, Props}
import akka.routing.RoundRobinPool

class GameGenerationMaster(numberOfWorkers: Int, numberOfMessages: Int, rowCount: Int, columnCount: Int, actions: List[Action], completion: (Option[List[GameObject]]) => Unit)
  extends Actor {

  private val workerRouter = context.actorOf(Props[GameGenerationWorker].withRouter(RoundRobinPool(numberOfWorkers)), name = "workerRouter")
  private var messageCounter = 0

  def receive: PartialFunction[Any, Unit] = {
    case Generate =>
      for (_ <- 0 until numberOfMessages) workerRouter ! Work(rowCount, columnCount)
    case Result(gameObjectsOpt) =>
      messageCounter += 1
      if (gameObjectsOpt.isDefined) {
        completion(gameObjectsOpt)
        context.stop(self)
      } else if (messageCounter == numberOfMessages) {
        completion(Option.empty)
        context.stop(self)
      }
  }

}