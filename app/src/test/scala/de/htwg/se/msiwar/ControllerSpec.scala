package de.htwg.se.msiwar

import java.nio.file.{Files, Paths}

import de.htwg.se.msiwar.controller.ControllerImpl
import de.htwg.se.msiwar.model._
import de.htwg.se.msiwar.util.Direction
import org.scalatest.{FlatSpec, Matchers}

import scala.concurrent.duration._
import scala.concurrent.{Await, Promise}

class ControllerSpec extends FlatSpec with Matchers {
  private val resourcePathPrefix = "src/main/resources/"

  ControllerImpl.getClass.getSimpleName should "return turn counter of 1 at game start" in {
    val testConfigProvider = new TestConfigProvider
    testConfigProvider.load2PlayerEmptyMapScenario()

    val model = GameModelImpl(testConfigProvider, GameBoard(testConfigProvider.rowCount, testConfigProvider.colCount, testConfigProvider.gameObjects), Option.empty[Action], 1, 1)
    val controller = ControllerImpl(model)
    controller.turnCounter should be(1)
  }

  it should "return a valid path for opening background" in {
    val testConfigProvider = new TestConfigProvider
    testConfigProvider.load2PlayerEmptyMapScenario()

    val model = GameModelImpl(testConfigProvider, GameBoard(testConfigProvider.rowCount, testConfigProvider.colCount, testConfigProvider.gameObjects), Option.empty[Action], 1, 1)
    val controller = ControllerImpl(model)
    Files.exists(Paths.get(resourcePathPrefix + controller.openingBackgroundImagePath)) should be(true)
  }

  it should "return a valid path for level background" in {
    val testConfigProvider = new TestConfigProvider
    testConfigProvider.load2PlayerEmptyMapScenario()

    val model = GameModelImpl(testConfigProvider, GameBoard(testConfigProvider.rowCount, testConfigProvider.colCount, testConfigProvider.gameObjects), Option.empty[Action], 1, 1)
    val controller = ControllerImpl(model)
    Files.exists(Paths.get(resourcePathPrefix + controller.levelBackgroundImagePath)) should be(true)
  }

  it should "return a valid path for action bar background" in {
    val testConfigProvider = new TestConfigProvider
    testConfigProvider.load2PlayerEmptyMapScenario()

    val model = GameModelImpl(testConfigProvider, GameBoard(testConfigProvider.rowCount, testConfigProvider.colCount, testConfigProvider.gameObjects), Option.empty[Action], 1, 1)
    val controller = ControllerImpl(model)
    Files.exists(Paths.get(resourcePathPrefix + controller.actionbarBackgroundImagePath)) should be(true)
  }

  it should "return a valid path for app icon" in {
    val testConfigProvider = new TestConfigProvider
    testConfigProvider.load2PlayerEmptyMapScenario()

    val model = GameModelImpl(testConfigProvider, GameBoard(testConfigProvider.rowCount, testConfigProvider.colCount, testConfigProvider.gameObjects), Option.empty[Action], 1, 1)
    val controller = ControllerImpl(model)
    Files.exists(Paths.get(resourcePathPrefix + controller.appIconImagePath)) should be(true)
  }

  it should "return the name of the active player" in {
    val testConfigProvider = new TestConfigProvider
    testConfigProvider.load2PlayerDamageTestScenario()

    val model = GameModelImpl(testConfigProvider, GameBoard(testConfigProvider.rowCount, testConfigProvider.colCount, testConfigProvider.gameObjects), Option.empty[Action], 1, 1)
    val controller = ControllerImpl(model)
    controller.activePlayerName should be("Player1")
  }

  it should "return cell content image path" in {
    val testConfigProvider = new TestConfigProvider
    testConfigProvider.load2PlayerDamageTestScenario()

    val model = GameModelImpl(testConfigProvider, GameBoard(testConfigProvider.rowCount, testConfigProvider.colCount, testConfigProvider.gameObjects), Option.empty[Action], 1, 1)
    val controller = ControllerImpl(model)
    val imagePathOpt = controller.cellContentImagePath(0, 0)
    imagePathOpt.isDefined should be(true)
    imagePathOpt.get should be("images/light_tank_red_180.png")
  }

  it should "return cell content as text" in {
    val testConfigProvider = new TestConfigProvider
    testConfigProvider.load2PlayerDamageTestScenario()

    val model = GameModelImpl(testConfigProvider, GameBoard(testConfigProvider.rowCount, testConfigProvider.colCount, testConfigProvider.gameObjects), Option.empty[Action], 1, 1)
    val controller = ControllerImpl(model)
    controller.cellContentToText(0, 0) should be("1")
  }

  it should "execute an action" in {
    val testConfigProvider = new TestConfigProvider
    testConfigProvider.load2PlayerDamageTestScenario()

    val model = GameModelImpl(testConfigProvider, GameBoard(testConfigProvider.rowCount, testConfigProvider.colCount, testConfigProvider.gameObjects), Option.empty[Action], 1, 1)
    val controller = ControllerImpl(model)
    controller.canExecuteAction(2, Direction.DOWN) should be(true)
    controller.executeAction(2, Direction.DOWN)
    controller.canExecuteAction(2, 1, 0) should be(true)
    controller.executeAction(1, 1, 0)
  }

  it should "return the action ids for a player" in {
    val testConfigProvider = new TestConfigProvider
    testConfigProvider.load2PlayerDamageTestScenario()

    val model = GameModelImpl(testConfigProvider, GameBoard(testConfigProvider.rowCount, testConfigProvider.colCount, testConfigProvider.gameObjects), Option.empty[Action], 1, 1)
    val controller = ControllerImpl(model)
    controller.actionIds(1).size should be(1)
  }

  it should "return the number of the active player" in {
    val testConfigProvider = new TestConfigProvider
    testConfigProvider.load2PlayerDamageTestScenario()

    val model = GameModelImpl(testConfigProvider, GameBoard(testConfigProvider.rowCount, testConfigProvider.colCount, testConfigProvider.gameObjects), Option.empty[Action], 1, 1)
    val controller = ControllerImpl(model)
    controller.activePlayerNumber should be(1)
  }

  it should "return row count 10 at game start" in {
    val testConfigProvider = new TestConfigProvider
    testConfigProvider.load2PlayerEmptyMapScenario()

    val model = GameModelImpl(testConfigProvider, GameBoard(testConfigProvider.rowCount, testConfigProvider.colCount, testConfigProvider.gameObjects), Option.empty[Action], 1, 1)
    val controller = ControllerImpl(model)
    controller.rowCount should be(10)
  }

  it should "return column count 2 at game start" in {
    val testConfigProvider = new TestConfigProvider
    testConfigProvider.load2PlayerEmptyMapScenario()

    val model = GameModelImpl(testConfigProvider, GameBoard(testConfigProvider.rowCount, testConfigProvider.colCount, testConfigProvider.gameObjects), Option.empty[Action], 1, 1)
    val controller = ControllerImpl(model)
    controller.columnCount should be(2)
  }

  it should "return a list of scenario ids available" in {
    val testConfigProvider = new TestConfigProvider
    testConfigProvider.load2PlayerEmptyMapScenario()

    val model = GameModelImpl(testConfigProvider, GameBoard(testConfigProvider.rowCount, testConfigProvider.colCount, testConfigProvider.gameObjects), Option.empty[Action], 1, 1)
    val controller = ControllerImpl(model)
    controller.scenarioIds.size should be(2)
  }

  it should "return a scenario name for a valid scenario id" in {
    val testConfigProvider = new TestConfigProvider
    testConfigProvider.load2PlayerEmptyMapScenario()

    val model = GameModelImpl(testConfigProvider, GameBoard(testConfigProvider.rowCount, testConfigProvider.colCount, testConfigProvider.gameObjects), Option.empty[Action], 1, 1)

    val controller = ControllerImpl(model)
    val scenarioNameFound = controller.scenarioName(0)
    scenarioNameFound.isDefined should be(true)
    scenarioNameFound.get should be("S1 Scenario (2-Player)")
  }

  it should "not return a scenario name for a unknown scenario id" in {
    val testConfigProvider = new TestConfigProvider
    testConfigProvider.load2PlayerEmptyMapScenario()

    val model = GameModelImpl(testConfigProvider, GameBoard(testConfigProvider.rowCount, testConfigProvider.colCount, testConfigProvider.gameObjects), Option.empty[Action], 1, 1)
    val controller = ControllerImpl(model)
    val scenarioNameNotFound = controller.scenarioName(3)
    scenarioNameNotFound.isDefined should be(false)
  }

  it should "return the health of the active player" in {
    val testConfigProvider = new TestConfigProvider
    testConfigProvider.load2PlayerDamageTestScenario()

    val model = GameModelImpl(testConfigProvider, GameBoard(testConfigProvider.rowCount, testConfigProvider.colCount, testConfigProvider.gameObjects), Option.empty[Action], 1, 1)
    val controller = ControllerImpl(model)
    controller.activePlayerHealthPoints should be(3)
  }

  it should "return the actions points of the active player" in {
    val testConfigProvider = new TestConfigProvider
    testConfigProvider.load2PlayerDamageTestScenario()

    val model = GameModelImpl(testConfigProvider, GameBoard(testConfigProvider.rowCount, testConfigProvider.colCount, testConfigProvider.gameObjects), Option.empty[Action], 1, 1)
    val controller = ControllerImpl(model)
    controller.activePlayerActionPoints should be(3)
  }

  it should "return damage value for an action id" in {
    val testConfigProvider = new TestConfigProvider
    testConfigProvider.load2PlayerDamageTestScenario()

    val model = GameModelImpl(testConfigProvider, GameBoard(testConfigProvider.rowCount, testConfigProvider.colCount, testConfigProvider.gameObjects), Option.empty[Action], 1, 1)
    val controller = ControllerImpl(model)
    controller.actionDamage(2) should be(2)
  }

  it should "increase turn counter by 1 when all players uses all action points one time" in {
    val testConfigProvider = new TestConfigProvider
    testConfigProvider.load2PlayerEmptyMapScenario()

    var model: GameModel = GameModelImpl(testConfigProvider, GameBoard(testConfigProvider.rowCount, testConfigProvider.colCount, testConfigProvider.gameObjects), Option.empty[Action], 1, 1)
    model.canExecuteAction(3, Direction.DOWN) should be(true)
    model = model.executeAction(3, Direction.DOWN)._1
    model.turnCounter should be(1)
    model.canExecuteAction(3, Direction.DOWN) should be(true)
    model = model.executeAction(3, Direction.DOWN)._1
    model.turnCounter should be(2)
  }

  it should "return a lower amount of action points for active player after an action has been executed" in {
    val testConfigProvider = new TestConfigProvider
    testConfigProvider.load2PlayerDamageTestScenario()

    var model: GameModel = GameModelImpl(testConfigProvider, GameBoard(testConfigProvider.rowCount, testConfigProvider.colCount, testConfigProvider.gameObjects), Option.empty[Action], 1, 1)

    val actionIds = model.actionIdsForPlayer(1).get
    val actionIdsIterator = actionIds.iterator
    while (actionIdsIterator.hasNext) {
      val actionId = actionIdsIterator.next()
      val actionPointsBefore = model.activePlayerActionPoints
      model.canExecuteAction(actionId, Direction.DOWN) should be(true)
      model = model.executeAction(actionId, Direction.DOWN)._1
      actionPointsBefore should be > model.activePlayerActionPoints
      model = model.init(testConfigProvider)
    }
  }

  it should "return a lower health amount for player after hit" in {
    val testConfigProvider = new TestConfigProvider
    testConfigProvider.load2PlayerDamageTestScenario()

    val gameBoard = GameBoard(testConfigProvider.rowCount, testConfigProvider.colCount, testConfigProvider.gameObjects)
    var model: GameModel = GameModelImpl(testConfigProvider, gameBoard, Option.empty[Action], 1, 1)
    val player2BeforeDamagingOpt = model.cellContent(1,0).get
    player2BeforeDamagingOpt match {
      case p: PlayerObject => p.healthPoints should be(3)
    }

    model.canExecuteAction(2, Direction.DOWN) should be(true)
    model = model.executeAction(2, Direction.DOWN)._1

    val player2AfterDamagingOpt = model.cellContent(1,0).get
    player2AfterDamagingOpt match {
      case p: PlayerObject => p.healthPoints should be(1)
    }
  }

  it should "return winner id of player 1 when player 2 gets destroyed" in {
    val testConfigProvider = new TestConfigProvider
    testConfigProvider.load2PlayerDamageTestScenario()

    var model: GameModel = GameModelImpl(testConfigProvider, GameBoard(testConfigProvider.rowCount, testConfigProvider.colCount, testConfigProvider.gameObjects), Option.empty[Action], 1, 1)
    model.canExecuteAction(2, Direction.DOWN) should be(true)
    model = model.executeAction(2, Direction.DOWN)._1
    model.canExecuteAction(2, Direction.DOWN) should be(true)
    model = model.executeAction(2, Direction.DOWN)._1
    model.winnerId.isDefined should be(true)
    model.winnerId.get should be(1)
  }

  it should "return no action ids for a dead player" in {
    val testConfigProvider = new TestConfigProvider
    testConfigProvider.load3PlayerTestScenario()

    var model: GameModel = GameModelImpl(testConfigProvider, GameBoard(testConfigProvider.rowCount, testConfigProvider.colCount, testConfigProvider.gameObjects), Option.empty[Action], 1, 1)
    model = model.executeAction(2, Direction.DOWN)._1
    model.actionIdsForPlayer(2).isEmpty should be(true)
  }

  it should "return range value for an action id" in {
    val testConfigProvider = new TestConfigProvider
    testConfigProvider.load2PlayerDamageTestScenario()

    val model = GameModelImpl(testConfigProvider, GameBoard(testConfigProvider.rowCount, testConfigProvider.colCount, testConfigProvider.gameObjects), Option.empty[Action], 1, 1)
    val controller = ControllerImpl(model)
    controller.actionRange(2) should be(3)
  }

  it should "return action cost value for an action id" in {
    val testConfigProvider = new TestConfigProvider
    testConfigProvider.load2PlayerDamageTestScenario()

    val model = GameModelImpl(testConfigProvider, GameBoard(testConfigProvider.rowCount, testConfigProvider.colCount, testConfigProvider.gameObjects), Option.empty[Action], 1, 1)
    val controller = ControllerImpl(model)
    controller.actionPointCost(2) should be(1)
  }

  it should "return action description for an action id" in {
    val testConfigProvider = new TestConfigProvider
    testConfigProvider.load2PlayerDamageTestScenario()

    val model = GameModelImpl(testConfigProvider, GameBoard(testConfigProvider.rowCount, testConfigProvider.colCount, testConfigProvider.gameObjects), Option.empty[Action], 1, 1)
    val controller = ControllerImpl(model)
    controller.actionDescription(2) should be("Shoot")
  }

  it should "return action icon path for an action id" in {
    val testConfigProvider = new TestConfigProvider
    testConfigProvider.load2PlayerDamageTestScenario()

    val model = GameModelImpl(testConfigProvider, GameBoard(testConfigProvider.rowCount, testConfigProvider.colCount, testConfigProvider.gameObjects), Option.empty[Action], 1, 1)
    val controller = ControllerImpl(model)
    controller.actionIconPath(2).isDefined should be(true)
  }

  it should "not return action icon path for an unknown action id" in {
    val testConfigProvider = new TestConfigProvider
    testConfigProvider.load2PlayerDamageTestScenario()

    val model = GameModelImpl(testConfigProvider, GameBoard(testConfigProvider.rowCount, testConfigProvider.colCount, testConfigProvider.gameObjects), Option.empty[Action], 1, 1)
    val controller = ControllerImpl(model)
    controller.actionIconPath(1).isDefined should be(false)
  }

  it should "start a new game for valid scenario id" in {
    val testConfigProvider = new TestConfigProvider
    testConfigProvider.load2PlayerDamageTestScenario()

    val model = GameModelImpl(testConfigProvider, GameBoard(testConfigProvider.rowCount, testConfigProvider.colCount, testConfigProvider.gameObjects), Option.empty[Action], 1, 1)
    val controller = ControllerImpl(model)
    controller.startGame(0)
  }

  it should "start a random game" in {
    val gameStartedPromise = Promise[Boolean]()

    val testConfigProvider = new TestConfigProvider
    testConfigProvider.load2PlayerDamageTestScenario()

    val model = GameModelImpl(testConfigProvider, GameBoard(testConfigProvider.rowCount, testConfigProvider.colCount, testConfigProvider.gameObjects), Option.empty[Action], 1, 1)
    val controller = ControllerImpl(model)

    TestEventHandler(model, controller, Option(gameStartedPromise), Option.empty, Option.empty)

    controller.startRandomGame()

    val result = Await.result(gameStartedPromise.future, 5000 millis)
    result should be(true)
  }

  it should "start a new game for valid scenario id and return the correct initial events" in {
    val testConfigProvider = new TestConfigProvider
    testConfigProvider.load2PlayerDamageTestScenario()

    val gameStartedPromise = Promise[Boolean]()
    val turnStartedPromise = Promise[Int]()

    val model = GameModelImpl(testConfigProvider, GameBoard(testConfigProvider.rowCount, testConfigProvider.colCount, testConfigProvider.gameObjects), Option.empty[Action], 1, 1)
    val controller = ControllerImpl(model)

    TestEventHandler(model, controller, Option(gameStartedPromise), Option.empty, Option(turnStartedPromise))

    model.startGame(0)

    val gameStarted = Await.result(gameStartedPromise.future, 500 millis)
    val playerNumber = Await.result(turnStartedPromise.future, 500 millis)
    gameStarted should be(true)
    playerNumber should be(1)
  }

  it should " fail at starting a random game with invalid row and column configuration" in {

    val couldNotGenerateGamePromise = Promise[Boolean]()

    val testConfigProvider = new TestConfigProvider
    testConfigProvider.load2PlayerDamageTestScenario()

    val model = GameModelImpl(testConfigProvider, GameBoard(testConfigProvider.rowCount, testConfigProvider.colCount, testConfigProvider.gameObjects), Option.empty[Action], 1, 1)
    val controller = ControllerImpl(model)

    TestEventHandler(model, controller, Option.empty, Option(couldNotGenerateGamePromise), Option.empty)

    model.startRandomGame(0, 0)

    val result = Await.result(couldNotGenerateGamePromise.future, 500 millis)
    result should be(true)
  }
}