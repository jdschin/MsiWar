package de.htwg.se.msiwar

import java.nio.file.{Files, Paths}

import de.htwg.se.msiwar.model._
import de.htwg.se.msiwar.util.Direction
import org.scalatest.{FlatSpec, Matchers}

import scala.concurrent.duration._
import scala.concurrent.{Await, Promise}


class ModelSpec extends FlatSpec with Matchers {
  private val resourcePathPrefix = "src/main/resources/"

  GameModelImpl.getClass.getSimpleName should "return turn counter of 1 at game start" in {
    val testConfigProvider = new TestConfigProvider
    testConfigProvider.load2PlayerEmptyMapScenario()

    val model = GameModelImpl(testConfigProvider)
    model.turnCounter should be(1)
  }

  it should "increase turn counter by 1 when all players uses all action points one time" in {
    val testConfigProvider = new TestConfigProvider
    testConfigProvider.load2PlayerEmptyMapScenario()

    val model = GameModelImpl(testConfigProvider)
    model.canExecuteAction(3, Direction.DOWN) should be(true)
    model.executeAction(3, Direction.DOWN)
    model.turnCounter should be(1)
    model.canExecuteAction(3, Direction.DOWN) should be(true)
    model.executeAction(3, Direction.DOWN)
    model.turnCounter should be(2)
  }

  it should "not return a winner id when more than 1 player is alive " in {
    val testConfigProvider = new TestConfigProvider
    testConfigProvider.load2PlayerEmptyMapScenario()

    val model = GameModelImpl(testConfigProvider)
    model.winnerId.isDefined should be(false)
  }

  it should "return a winner id when only 1 player is alive" in {
    val testConfigProvider = new TestConfigProvider
    testConfigProvider.loadInstantWinScenario()

    val model = GameModelImpl(testConfigProvider)
    model.winnerId.isDefined should be(true)
  }

  it should "not allow action execution when game is won" in {
    val testConfigProvider = new TestConfigProvider
    testConfigProvider.loadInstantWinScenario()

    val model = GameModelImpl(testConfigProvider)
    model.canExecuteAction(1, Direction.DOWN) should be(false)
  }

  it should "return a lower amount of action points for active player after an action has been executed" in {
    val testConfigProvider = new TestConfigProvider
    testConfigProvider.load2PlayerDamageTestScenario()

    val model = GameModelImpl(testConfigProvider)

    val actionIds = model.actionIdsForPlayer(1)
    val actionIdsIterator = actionIds.iterator
    while (actionIdsIterator.hasNext) {
      val actionId = actionIdsIterator.next()
      val actionPointsBefore = model.activePlayerActionPoints
      model.canExecuteAction(actionId, Direction.DOWN) should be(true)
      model.executeAction(actionId, Direction.DOWN)
      actionPointsBefore should be > model.activePlayerActionPoints
      model.reset()
    }
  }

  it should "return a lower health amount for player after hit" in {
    val testConfigProvider = new TestConfigProvider
    testConfigProvider.load2PlayerDamageTestScenario()

    val model = GameModelImpl(testConfigProvider)
    val player2 = testConfigProvider.gameObjects.collect({ case s: PlayerObject => s }).find(_.playerNumber == 2).get
    player2.currentHealthPoints should be(3)
    model.canExecuteAction(2, Direction.DOWN) should be(true)
    model.executeAction(2, Direction.DOWN)
    player2.currentHealthPoints should be(1)
  }

  it should "return the health of the active player" in {
    val testConfigProvider = new TestConfigProvider
    testConfigProvider.load2PlayerDamageTestScenario()

    val model = GameModelImpl(testConfigProvider)
    model.activePlayerHealthPoints should be(3)
  }

  it should "return winner id of player 1 when player 2 gets destroyed" in {
    val testConfigProvider = new TestConfigProvider
    testConfigProvider.load2PlayerDamageTestScenario()

    val model = GameModelImpl(testConfigProvider)
    model.canExecuteAction(2, Direction.DOWN) should be(true)
    model.executeAction(2, Direction.DOWN)
    model.canExecuteAction(2, Direction.DOWN) should be(true)
    model.executeAction(2, Direction.DOWN)
    model.winnerId.isDefined should be(true)
    model.winnerId.get should be(1)
  }

  it should "return row count 10 at game start" in {
    val testConfigProvider = new TestConfigProvider
    testConfigProvider.load2PlayerEmptyMapScenario()

    val model = GameModelImpl(testConfigProvider)
    model.rowCount should be(10)
  }

  it should "return column count 2 at game start" in {
    val testConfigProvider = new TestConfigProvider
    testConfigProvider.load2PlayerEmptyMapScenario()

    val model = GameModelImpl(testConfigProvider)
    model.columnCount should be(2)
  }

  it should "return a valid path for opening background" in {
    val testConfigProvider = new TestConfigProvider
    testConfigProvider.load2PlayerEmptyMapScenario()

    val model = GameModelImpl(testConfigProvider)
    Files.exists(Paths.get(resourcePathPrefix + model.openingBackgroundImagePath)) should be(true)
  }

  it should "return a list of scenario ids available" in {
    val testConfigProvider = new TestConfigProvider
    testConfigProvider.load2PlayerEmptyMapScenario()

    val model = GameModelImpl(testConfigProvider)
    model.scenarioIds.size should be(2)
  }

  it should "return a scenario name for a valid scenario id" in {
    val testConfigProvider = new TestConfigProvider
    testConfigProvider.load2PlayerEmptyMapScenario()

    val model = GameModelImpl(testConfigProvider)

    val scenarioNameFound = model.scenarioName(0)
    scenarioNameFound.isDefined should be(true)
    scenarioNameFound.get should be("S1 Scenario (2-Player)")
  }

  it should "not return a scenario name for a unknown scenario id" in {
    val testConfigProvider = new TestConfigProvider
    testConfigProvider.load2PlayerEmptyMapScenario()

    val model = GameModelImpl(testConfigProvider)
    val scenarioNameNotFound = model.scenarioName(3)
    scenarioNameNotFound.isDefined should be(false)
  }

  it should "not return a won image path when no winner id is set" in {
    val testConfigProvider = new TestConfigProvider
    testConfigProvider.load2PlayerEmptyMapScenario()

    val model = GameModelImpl(testConfigProvider)
    model.wonImagePath should be("")
  }

  it should "return a valid path for level background" in {
    val testConfigProvider = new TestConfigProvider
    testConfigProvider.load2PlayerEmptyMapScenario()

    val model = GameModelImpl(testConfigProvider)
    Files.exists(Paths.get(resourcePathPrefix + model.levelBackgroundImagePath)) should be(true)
  }

  it should "return a valid path for action bar background" in {
    val testConfigProvider = new TestConfigProvider
    testConfigProvider.load2PlayerEmptyMapScenario()

    val model = GameModelImpl(testConfigProvider)
    Files.exists(Paths.get(resourcePathPrefix + model.actionbarBackgroundImagePath)) should be(true)
  }

  it should "return a valid path for app icon" in {
    val testConfigProvider = new TestConfigProvider
    testConfigProvider.load2PlayerEmptyMapScenario()

    val model = GameModelImpl(testConfigProvider)
    Files.exists(Paths.get(resourcePathPrefix + model.appIconImagePath)) should be(true)
  }

  it should "return a valid path for attack image" in {
    val testConfigProvider = new TestConfigProvider
    testConfigProvider.load2PlayerEmptyMapScenario()

    val model = GameModelImpl(testConfigProvider)
    Files.exists(Paths.get(resourcePathPrefix + model.attackImagePath)) should be(true)
  }

  it should "return a valid path for attack sound" in {
    val testConfigProvider = new TestConfigProvider
    testConfigProvider.load2PlayerEmptyMapScenario()

    val model = GameModelImpl(testConfigProvider)
    Files.exists(Paths.get(resourcePathPrefix + model.attackSoundPath)) should be(true)
  }

  it should "return damage value for an action id" in {
    val testConfigProvider = new TestConfigProvider
    testConfigProvider.load2PlayerDamageTestScenario()

    val model = GameModelImpl(testConfigProvider)
    model.actionDamage(2) should be(2)
  }

  it should "return damage value of 0 for an unknown action id" in {
    val testConfigProvider = new TestConfigProvider
    testConfigProvider.load2PlayerDamageTestScenario()

    val model = GameModelImpl(testConfigProvider)
    model.actionDamage(1) should be(0)
  }

  it should "return range value for an action id" in {
    val testConfigProvider = new TestConfigProvider
    testConfigProvider.load2PlayerDamageTestScenario()

    val model = GameModelImpl(testConfigProvider)
    model.actionRange(2) should be(3)
  }

  it should "return range value of 0 for an unknown action id" in {
    val testConfigProvider = new TestConfigProvider
    testConfigProvider.load2PlayerDamageTestScenario()

    val model = GameModelImpl(testConfigProvider)
    model.actionRange(1) should be(0)
  }

  it should "return action cost value for an action id" in {
    val testConfigProvider = new TestConfigProvider
    testConfigProvider.load2PlayerDamageTestScenario()

    val model = GameModelImpl(testConfigProvider)
    model.actionPointCost(2) should be(1)
  }

  it should "return action cost value of 0 for an unknown action id" in {
    val testConfigProvider = new TestConfigProvider
    testConfigProvider.load2PlayerDamageTestScenario()

    val model = GameModelImpl(testConfigProvider)
    model.actionPointCost(1) should be(0)
  }

  it should "return action description for an action id" in {
    val testConfigProvider = new TestConfigProvider
    testConfigProvider.load2PlayerDamageTestScenario()

    val model = GameModelImpl(testConfigProvider)
    model.actionDescription(2) should be("Shoot")
  }

  it should "not return action description for an unknown action id" in {
    val testConfigProvider = new TestConfigProvider
    testConfigProvider.load2PlayerDamageTestScenario()

    val model = GameModelImpl(testConfigProvider)
    model.actionDescription(1) should be("")
  }

  it should "return action icon path for an action id" in {
    val testConfigProvider = new TestConfigProvider
    testConfigProvider.load2PlayerDamageTestScenario()

    val model = GameModelImpl(testConfigProvider)
    model.actionIconPath(2).isDefined should be(true)
  }

  it should "not return action icon path for an unknown action id" in {
    val testConfigProvider = new TestConfigProvider
    testConfigProvider.load2PlayerDamageTestScenario()

    val model = GameModelImpl(testConfigProvider)
    model.actionIconPath(1).isDefined should be(false)
  }


  it should "start a new game for valid scenario id and return the correct initial events" in {
    val testConfigProvider = new TestConfigProvider
    testConfigProvider.load2PlayerDamageTestScenario()

    val gameStartedPromise = Promise[Boolean]()
    val turnStartedPromise = Promise[Int]()

    val model = GameModelImpl(testConfigProvider)

    TestEventHandler(model, Option(gameStartedPromise), Option.empty, Option(turnStartedPromise))

    model.startGame(0)

    val gameStarted = Await.result(gameStartedPromise.future, 500 millis)
    val playerNumber = Await.result(turnStartedPromise.future, 500 millis)
    gameStarted should be(true)
    playerNumber should be(1)
  }

  it should "start a random game" in {

    val gameStartedPromise = Promise[Boolean]()

    val testConfigProvider = new TestConfigProvider
    testConfigProvider.load2PlayerDamageTestScenario()

    val model = GameModelImpl(testConfigProvider)
    TestEventHandler(model, Option(gameStartedPromise), Option.empty, Option.empty)

    model.startRandomGame()


    val result = Await.result(gameStartedPromise.future, 500 millis)
    result should be(true)
  }

  it should " fail at starting a random game with invalid row and column configuration" in {

    val couldNotGenerateGamePromise = Promise[Boolean]()

    val testConfigProvider = new TestConfigProvider
    testConfigProvider.load2PlayerDamageTestScenario()

    val model = GameModelImpl(testConfigProvider)

    TestEventHandler(model, Option.empty, Option(couldNotGenerateGamePromise), Option.empty)

    model.startRandomGame(0, 0)

    val result = Await.result(couldNotGenerateGamePromise.future, 500 millis)
    result should be(true)
  }

  it should "return the name of the active player" in {
    val testConfigProvider = new TestConfigProvider
    testConfigProvider.load2PlayerDamageTestScenario()

    val model = GameModelImpl(testConfigProvider)
    model.activePlayerName should be("Player1")
  }

  it should "allow to shoot at position where no target is and miss" in {
    val testConfigProvider = new TestConfigProvider
    testConfigProvider.load2PlayerEmptyMapScenario()

    val model = GameModelImpl(testConfigProvider)
    model.executeAction(2, Direction.DOWN)
    model.canExecuteAction(2, Direction.DOWN) should be(true)
  }

  it should "allow to shoot at a blocking object which is not a player" in {
    val testConfigProvider = new TestConfigProvider
    testConfigProvider.load2PlayerSmallMapScenario()

    val model = GameModelImpl(testConfigProvider)
    model.executeAction(2, Direction.UP)
    model.canExecuteAction(2, Direction.UP) should be(true)
  }

  it should "allow the player to move" in {
    val testConfigProvider = new TestConfigProvider
    testConfigProvider.load2PlayerEmptyMapScenario()

    val model = GameModelImpl(testConfigProvider)
    model.executeAction(1, Direction.DOWN)
    model.canExecuteAction(1, Direction.DOWN) should be(true)
  }

  it should "not return a last executed action id at game start" in {
    val testConfigProvider = new TestConfigProvider
    testConfigProvider.load2PlayerEmptyMapScenario()

    val model = GameModelImpl(testConfigProvider)
    model.lastExecutedActionId.isDefined should be(false)
  }
}