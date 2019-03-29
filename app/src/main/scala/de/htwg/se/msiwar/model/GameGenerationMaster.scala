package de.htwg.se.msiwar.model

import akka.actor.{Actor, Props}
import akka.routing.RoundRobinPool

class GameGenerationMaster(numberOfWorkers: Int, numberOfMessages: Int, rowCount: Int, columnCount: Int, completion: (Option[List[GameObject]]) => Unit)
  extends Actor {

  private val workerRouter = context.actorOf(Props[GameGenerationWorker].withRouter(RoundRobinPool(numberOfWorkers)), name = "workerRouter")
  private var messageCounter = 0

  def receive: PartialFunction[Any, Unit] = {
    case Generate =>
      for (_ <- 0 until numberOfMessages) workerRouter ! Work(rowCount, columnCount)
    case Result(gameObjectsOpt) =>
      messageCounter += 1
      if (gameObjectsOpt.isDefined) {
        context.stop(self)
        completion(gameObjectsOpt)
      } else if (messageCounter == numberOfMessages) {
        context.stop(self)
        completion(Option.empty)
      }
  }

}