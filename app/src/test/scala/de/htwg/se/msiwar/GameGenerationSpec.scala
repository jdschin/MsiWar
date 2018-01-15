package de.htwg.se.msiwar

import java.util.UUID

import akka.actor.{ActorSystem, Props}
import akka.testkit.{ImplicitSender, TestKit}
import de.htwg.se.msiwar.model.ActionType.SHOOT
import de.htwg.se.msiwar.model._
import org.scalatest.{Matchers, WordSpecLike}

import scala.concurrent.duration._
import scala.concurrent.{Await, Promise}
import scala.util.Try

class GameGenerationSpec extends TestKit(ActorSystem("MySpec")) with ImplicitSender
  with WordSpecLike with Matchers {
  val actions = List(Action(id = 2, "Shoot", "images/action_attack.png", "shoot.wav",
    actionPoints = 1, range = 3, SHOOT, damage = 2))

  "GameGenerationMaster" should {


    "return a random game" in {

      val gameObjectPromise = Promise[Option[List[GameObject]]]
      val master = system.actorOf(Props(new GameGenerationMaster(numberOfWorkers = 4,
        numberOfMessages = 100, 5, 5, actions, (gameObjects) => {
          gameObjectPromise.complete(Try(gameObjects))
        })), name = UUID.randomUUID().toString)

      master ! Generate

      val result = Await.result(gameObjectPromise.future, 500 millis)

      result.isDefined should be(true)
    }

    "fail while generating a game with wrong configuration" in {

      val gameObjectPromise = Promise[Option[List[GameObject]]]
      val master = system.actorOf(Props(new GameGenerationMaster(numberOfWorkers = 4,
        numberOfMessages = 100, 0, 0, actions, (gameObjects) => {
          gameObjectPromise.complete(Try(gameObjects))
        })), name = UUID.randomUUID().toString)

      master ! Generate

      val result = Await.result(gameObjectPromise.future, 500 millis)

      result.isDefined should be(false)
    }

    "use the correct image paths" in {

      val path = RandomImagePaths.backgroundImagePath()
      (path == "images/background_desert.png" || path == "images/background_woodlands.png") should be(true)
    }
  }


}