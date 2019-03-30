package de.htwg.se.msiwar

import de.htwg.se.msiwar.model._
import de.htwg.se.msiwar.util.Direction
import org.scalatest.{FlatSpec, Matchers}


class ModelSpec extends FlatSpec with Matchers {
  private val turn = 1

  GameModelImpl.getClass.getSimpleName should "return turn counter of 1 at game start" in {
    val testConfigProvider = new TestConfigProvider
    testConfigProvider.load2PlayerEmptyMapScenario()

    val player = testConfigProvider.gameObjects.collect({ case s: PlayerObject => s }).find(_.playerNumber == 1).get
    val model = GameModelImpl(testConfigProvider, GameBoard(testConfigProvider.rowCount, testConfigProvider.colCount, testConfigProvider.gameObjects), Option.empty[Action], 1, 1)
    model.turnCounter should be(1)
  }

  it should "not return a winner id when more than 1 player is alive " in {
    val testConfigProvider = new TestConfigProvider
    testConfigProvider.load2PlayerEmptyMapScenario()

    val player = testConfigProvider.gameObjects.collect({ case s: PlayerObject => s }).find(_.playerNumber == 1).get
    val model = GameModelImpl(testConfigProvider, GameBoard(testConfigProvider.rowCount, testConfigProvider.colCount, testConfigProvider.gameObjects), Option.empty[Action], 1, 1)
    model.winnerId.isDefined should be(false)
  }

  it should "return a winner id when only 1 player is alive" in {
    val testConfigProvider = new TestConfigProvider
    testConfigProvider.loadInstantWinScenario()

    val player = testConfigProvider.gameObjects.collect({ case s: PlayerObject => s }).find(_.playerNumber == 1).get
    val model = GameModelImpl(testConfigProvider, GameBoard(testConfigProvider.rowCount, testConfigProvider.colCount, testConfigProvider.gameObjects), Option.empty[Action], 1, 1)
    model.winnerId.isDefined should be(true)
  }

  it should "not allow action execution when game is won" in {
    val testConfigProvider = new TestConfigProvider
    testConfigProvider.loadInstantWinScenario()

    val player = testConfigProvider.gameObjects.collect({ case s: PlayerObject => s }).find(_.playerNumber == 1).get
    val model = GameModelImpl(testConfigProvider, GameBoard(testConfigProvider.rowCount, testConfigProvider.colCount, testConfigProvider.gameObjects), Option.empty[Action], 1, 1)
    model.canExecuteAction(1, Direction.DOWN) should be(false)
  }

  it should "return the health of the active player" in {
    val testConfigProvider = new TestConfigProvider
    testConfigProvider.load2PlayerDamageTestScenario()

    val player = testConfigProvider.gameObjects.collect({ case s: PlayerObject => s }).find(_.playerNumber == 1).get
    val model = GameModelImpl(testConfigProvider, GameBoard(testConfigProvider.rowCount, testConfigProvider.colCount, testConfigProvider.gameObjects), Option.empty[Action], 1, 1)
    model.activePlayerHealthPoints should be(3)
  }

  it should "return row count 10 at game start" in {
    val testConfigProvider = new TestConfigProvider
    testConfigProvider.load2PlayerEmptyMapScenario()

    val player = testConfigProvider.gameObjects.collect({ case s: PlayerObject => s }).find(_.playerNumber == 1).get
    val model = GameModelImpl(testConfigProvider, GameBoard(testConfigProvider.rowCount, testConfigProvider.colCount, testConfigProvider.gameObjects), Option.empty[Action], 1, 1)
    model.rowCount should be(10)
  }

  it should "return column count 2 at game start" in {
    val testConfigProvider = new TestConfigProvider
    testConfigProvider.load2PlayerEmptyMapScenario()

    val player = testConfigProvider.gameObjects.collect({ case s: PlayerObject => s }).find(_.playerNumber == 1).get
    val model = GameModelImpl(testConfigProvider, GameBoard(testConfigProvider.rowCount, testConfigProvider.colCount, testConfigProvider.gameObjects), Option.empty[Action], 1, 1)
    model.columnCount should be(2)
  }

  it should "return a list of scenario ids available" in {
    val testConfigProvider = new TestConfigProvider
    testConfigProvider.load2PlayerEmptyMapScenario()

    val player = testConfigProvider.gameObjects.collect({ case s: PlayerObject => s }).find(_.playerNumber == 1).get
    val model = GameModelImpl(testConfigProvider, GameBoard(testConfigProvider.rowCount, testConfigProvider.colCount, testConfigProvider.gameObjects), Option.empty[Action], 1, 1)
    model.scenarioIds.size should be(2)
  }

  it should "return a scenario name for a valid scenario id" in {
    val testConfigProvider = new TestConfigProvider
    testConfigProvider.load2PlayerEmptyMapScenario()

    val player = testConfigProvider.gameObjects.collect({ case s: PlayerObject => s }).find(_.playerNumber == 1).get
    val model = GameModelImpl(testConfigProvider, GameBoard(testConfigProvider.rowCount, testConfigProvider.colCount, testConfigProvider.gameObjects), Option.empty[Action], 1, 1)

    val scenarioNameFound = model.scenarioName(0)
    scenarioNameFound.isDefined should be(true)
    scenarioNameFound.get should be("S1 Scenario (2-Player)")
  }

  it should "not return a scenario name for a unknown scenario id" in {
    val testConfigProvider = new TestConfigProvider
    testConfigProvider.load2PlayerEmptyMapScenario()

    val player = testConfigProvider.gameObjects.collect({ case s: PlayerObject => s }).find(_.playerNumber == 1).get
    val model = GameModelImpl(testConfigProvider, GameBoard(testConfigProvider.rowCount, testConfigProvider.colCount, testConfigProvider.gameObjects), Option.empty[Action], 1, 1)
    val scenarioNameNotFound = model.scenarioName(3)
    scenarioNameNotFound.isDefined should be(false)
  }

  it should "not return a won image path when no winner id is set" in {
    val testConfigProvider = new TestConfigProvider
    testConfigProvider.load2PlayerEmptyMapScenario()

    val player = testConfigProvider.gameObjects.collect({ case s: PlayerObject => s }).find(_.playerNumber == 1).get
    val model = GameModelImpl(testConfigProvider, GameBoard(testConfigProvider.rowCount, testConfigProvider.colCount, testConfigProvider.gameObjects), Option.empty[Action], 1, 1)
    model.wonImagePath should be("")
  }

  it should "return damage value for an action id" in {
    val testConfigProvider = new TestConfigProvider
    testConfigProvider.load2PlayerDamageTestScenario()

    val player = testConfigProvider.gameObjects.collect({ case s: PlayerObject => s }).find(_.playerNumber == 1).get
    val model = GameModelImpl(testConfigProvider, GameBoard(testConfigProvider.rowCount, testConfigProvider.colCount, testConfigProvider.gameObjects), Option.empty[Action], 1, 1)
    model.actionDamage(2) should be(2)
  }

  it should "return damage value of 0 for an unknown action id" in {
    val testConfigProvider = new TestConfigProvider
    testConfigProvider.load2PlayerDamageTestScenario()

    val player = testConfigProvider.gameObjects.collect({ case s: PlayerObject => s }).find(_.playerNumber == 1).get
    val model = GameModelImpl(testConfigProvider, GameBoard(testConfigProvider.rowCount, testConfigProvider.colCount, testConfigProvider.gameObjects), Option.empty[Action], 1, 1)
    model.actionDamage(1) should be(0)
  }

  it should "return range value for an action id" in {
    val testConfigProvider = new TestConfigProvider
    testConfigProvider.load2PlayerDamageTestScenario()

    val player = testConfigProvider.gameObjects.collect({ case s: PlayerObject => s }).find(_.playerNumber == 1).get
    val model = GameModelImpl(testConfigProvider, GameBoard(testConfigProvider.rowCount, testConfigProvider.colCount, testConfigProvider.gameObjects), Option.empty[Action], 1, 1)
    model.actionRange(2) should be(3)
  }

  it should "return range value of 0 for an unknown action id" in {
    val testConfigProvider = new TestConfigProvider
    testConfigProvider.load2PlayerDamageTestScenario()

    val player = testConfigProvider.gameObjects.collect({ case s: PlayerObject => s }).find(_.playerNumber == 1).get
    val model = GameModelImpl(testConfigProvider, GameBoard(testConfigProvider.rowCount, testConfigProvider.colCount, testConfigProvider.gameObjects), Option.empty[Action], 1, 1)
    model.actionRange(1) should be(0)
  }

  it should "return action cost value for an action id" in {
    val testConfigProvider = new TestConfigProvider
    testConfigProvider.load2PlayerDamageTestScenario()

    val player = testConfigProvider.gameObjects.collect({ case s: PlayerObject => s }).find(_.playerNumber == 1).get
    val model = GameModelImpl(testConfigProvider, GameBoard(testConfigProvider.rowCount, testConfigProvider.colCount, testConfigProvider.gameObjects), Option.empty[Action], 1, 1)
    model.actionPointCost(2) should be(1)
  }

  it should "return action cost value of 0 for an unknown action id" in {
    val testConfigProvider = new TestConfigProvider
    testConfigProvider.load2PlayerDamageTestScenario()

    val player = testConfigProvider.gameObjects.collect({ case s: PlayerObject => s }).find(_.playerNumber == 1).get
    val model = GameModelImpl(testConfigProvider, GameBoard(testConfigProvider.rowCount, testConfigProvider.colCount, testConfigProvider.gameObjects), Option.empty[Action], 1, 1)
    model.actionPointCost(1) should be(0)
  }

  it should "return action description for an action id" in {
    val testConfigProvider = new TestConfigProvider
    testConfigProvider.load2PlayerDamageTestScenario()

    val player = testConfigProvider.gameObjects.collect({ case s: PlayerObject => s }).find(_.playerNumber == 1).get
    val model = GameModelImpl(testConfigProvider, GameBoard(testConfigProvider.rowCount, testConfigProvider.colCount, testConfigProvider.gameObjects), Option.empty[Action], 1, 1)
    model.actionDescription(2) should be("Shoot")
  }

  it should "not return action description for an unknown action id" in {
    val testConfigProvider = new TestConfigProvider
    testConfigProvider.load2PlayerDamageTestScenario()

    val player = testConfigProvider.gameObjects.collect({ case s: PlayerObject => s }).find(_.playerNumber == 1).get
    val model = GameModelImpl(testConfigProvider, GameBoard(testConfigProvider.rowCount, testConfigProvider.colCount, testConfigProvider.gameObjects), Option.empty[Action], 1, 1)
    model.actionDescription(1) should be("")
  }

  it should "return action icon path for an action id" in {
    val testConfigProvider = new TestConfigProvider
    testConfigProvider.load2PlayerDamageTestScenario()

    val player = testConfigProvider.gameObjects.collect({ case s: PlayerObject => s }).find(_.playerNumber == 1).get
    val model = GameModelImpl(testConfigProvider, GameBoard(testConfigProvider.rowCount, testConfigProvider.colCount, testConfigProvider.gameObjects), Option.empty[Action], 1, 1)
    model.actionIconPath(2).isDefined should be(true)
  }

  it should "not return action icon path for an unknown action id" in {
    val testConfigProvider = new TestConfigProvider
    testConfigProvider.load2PlayerDamageTestScenario()

    val player = testConfigProvider.gameObjects.collect({ case s: PlayerObject => s }).find(_.playerNumber == 1).get
    val model = GameModelImpl(testConfigProvider, GameBoard(testConfigProvider.rowCount, testConfigProvider.colCount, testConfigProvider.gameObjects), Option.empty[Action], 1, 1)
    model.actionIconPath(1).isDefined should be(false)
  }

  it should "return the name of the active player" in {
    val testConfigProvider = new TestConfigProvider
    testConfigProvider.load2PlayerDamageTestScenario()

    val player = testConfigProvider.gameObjects.collect({ case s: PlayerObject => s }).find(_.playerNumber == 1).get
    val model = GameModelImpl(testConfigProvider, GameBoard(testConfigProvider.rowCount, testConfigProvider.colCount, testConfigProvider.gameObjects), Option.empty[Action], 1, 1)
    model.activePlayerName should be("Player1")
  }

  it should "allow to shoot at position where no target is and miss" in {
    val testConfigProvider = new TestConfigProvider
    testConfigProvider.load2PlayerEmptyMapScenario()

    val player = testConfigProvider.gameObjects.collect({ case s: PlayerObject => s }).find(_.playerNumber == 1).get
    val model = GameModelImpl(testConfigProvider, GameBoard(testConfigProvider.rowCount, testConfigProvider.colCount, testConfigProvider.gameObjects), Option.empty[Action], 1, 1)
    model.executeAction(2, Direction.DOWN)
    model.canExecuteAction(2, Direction.DOWN) should be(true)
  }

  it should "allow to shoot at a blocking object which is not a player" in {
    val testConfigProvider = new TestConfigProvider
    testConfigProvider.load2PlayerSmallMapScenario()

    val player = testConfigProvider.gameObjects.collect({ case s: PlayerObject => s }).find(_.playerNumber == 1).get
    val model = GameModelImpl(testConfigProvider, GameBoard(testConfigProvider.rowCount, testConfigProvider.colCount, testConfigProvider.gameObjects), Option.empty[Action], 1, 1)
    model.executeAction(2, Direction.UP)
    model.canExecuteAction(2, Direction.UP) should be(true)
  }

  it should "allow the player to move" in {
    val testConfigProvider = new TestConfigProvider
    testConfigProvider.load2PlayerEmptyMapScenario()

    val player = testConfigProvider.gameObjects.collect({ case s: PlayerObject => s }).find(_.playerNumber == 1).get
    val model = GameModelImpl(testConfigProvider, GameBoard(testConfigProvider.rowCount, testConfigProvider.colCount, testConfigProvider.gameObjects), Option.empty[Action], 1, 1)
    model.executeAction(1, Direction.DOWN)
    model.canExecuteAction(1, Direction.DOWN) should be(true)
  }

  it should "not return a last executed action id at game start" in {
    val testConfigProvider = new TestConfigProvider
    testConfigProvider.load2PlayerEmptyMapScenario()

    val player = testConfigProvider.gameObjects.collect({ case s: PlayerObject => s }).find(_.playerNumber == 1).get
    val model = GameModelImpl(testConfigProvider, GameBoard(testConfigProvider.rowCount, testConfigProvider.colCount, testConfigProvider.gameObjects), Option.empty[Action], 1, 1)
    model.lastExecutedActionId.isDefined should be(false)
  }

  it should "return an empty list when action id is not set" in {
    val testConfigProvider = new TestConfigProvider
    testConfigProvider.load2PlayerEmptyMapScenario()

    val player = testConfigProvider.gameObjects.collect({ case s: PlayerObject => s }).find(_.playerNumber == 1).get
    val model = GameModelImpl(testConfigProvider, GameBoard(testConfigProvider.rowCount, testConfigProvider.colCount, testConfigProvider.gameObjects), Option.empty[Action], 1, 1)
    model.cellsInRange(Option.empty[Int]) should be (List())
  }

  it should "return value of X when a cell content has no object" in {
    val testConfigProvider = new TestConfigProvider
    testConfigProvider.load2PlayerSmallMapScenario()

    val player = testConfigProvider.gameObjects.collect({ case s: PlayerObject => s }).find(_.playerNumber == 1).get
    val model = GameModelImpl(testConfigProvider, GameBoard(testConfigProvider.rowCount, testConfigProvider.colCount, testConfigProvider.gameObjects), Option.empty[Action], 1, 1)
    model.cellContentToText(2,0) should be ("X")
  }

  it should "return name of block object when a cell content has an object" in {
    val testConfigProvider = new TestConfigProvider
    testConfigProvider.load2PlayerSmallMapScenario()

    val player = testConfigProvider.gameObjects.collect({ case s: PlayerObject => s }).find(_.playerNumber == 1).get
    val model = GameModelImpl(testConfigProvider, GameBoard(testConfigProvider.rowCount, testConfigProvider.colCount, testConfigProvider.gameObjects), Option.empty[Action], 1, 1)
    model.cellContentToText(0,0) should be ("B")
  }

  it should "return an empty Option[String] when there is no cell content at position" in {
    val testConfigProvider = new TestConfigProvider
    testConfigProvider.load2PlayerSmallMapScenario()

    val player = testConfigProvider.gameObjects.collect({ case s: PlayerObject => s }).find(_.playerNumber == 1).get
    val model = GameModelImpl(testConfigProvider, GameBoard(testConfigProvider.rowCount, testConfigProvider.colCount, testConfigProvider.gameObjects), Option.empty[Action], 1, 1)
    model.cellContentImagePath(2,0) should be (Option.empty[String])
  }

  it should "return an image path when there is a cell content at position" in {
    val testConfigProvider = new TestConfigProvider
    testConfigProvider.load2PlayerSmallMapScenario()

    val player = testConfigProvider.gameObjects.collect({ case s: PlayerObject => s }).find(_.playerNumber == 1).get
    val model = GameModelImpl(testConfigProvider, GameBoard(testConfigProvider.rowCount, testConfigProvider.colCount, testConfigProvider.gameObjects), Option.empty[Action], 1, 1)
    model.cellContentImagePath(0,0).isDefined should be (true)
    model.cellContentImagePath(0,0).get should be ("images/block_wood.png")
  }
}