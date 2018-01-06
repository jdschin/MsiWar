package de.htwg.se.msiwar

import de.htwg.se.msiwar.model.{GameModelImpl, PlayerObject}
import de.htwg.se.msiwar.util.Direction
import org.scalatest.{FlatSpec, Matchers}

class ModelSpec extends FlatSpec with Matchers {

  GameModelImpl.getClass.getSimpleName should "return turn counter of 1 at game start" in {
    val testConfigProvider = new TestConfigProvider
    testConfigProvider.load2PlayerEmptyMapScenario()

    val model = GameModelImpl(testConfigProvider)
    model.turnCounter should be(1)
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

  it should "return a lower amount of action points for active player after an action has been executed" in {
    val testConfigProvider = new TestConfigProvider
    testConfigProvider.load2PlayerEmptyMapScenario()

    val model = GameModelImpl(testConfigProvider)

    val actionIds = model.actionIdsForPlayer(1)
    val actionIdsIterator = actionIds.iterator
    while (actionIdsIterator.hasNext) {
      val actionId = actionIdsIterator.next()
      val actionPointsBefore = model.activePlayerActionPoints
      model.executeAction(actionId, Direction.DOWN)
      actionPointsBefore should be > model.activePlayerActionPoints
      model.reset()
    }
  }

  it should "return turn over with value 'true' when active player 1 spent all action points" in {
    val testConfigProvider = new TestConfigProvider
    testConfigProvider.load2PlayerEmptyMapScenario()

    val model = GameModelImpl(testConfigProvider)

    val actionIds = model.actionIdsForPlayer(1)
    val actionIdsIterator = actionIds.iterator
    while (actionIdsIterator.hasNext) {
      val actionId = actionIdsIterator.next()
      while(model.activePlayerActionPoints > 0) {
        model.executeAction(actionId, Direction.DOWN)
      }
      model.turnOver should be(true)
      model.reset()
    }
  }

  it should "return a lower health amount for player after hit" in {
    val testConfigProvider = new TestConfigProvider
    testConfigProvider.load2PlayerDamageTestScenario()

    val model = GameModelImpl(testConfigProvider)
    val player2 = testConfigProvider.gameObjects.collect({ case s: PlayerObject => s }).find(_.playerNumber == 2).get
    player2.currentHealthPoints should be(3)
    model.executeAction(2, Direction.DOWN)
    player2.currentHealthPoints should be(1)
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
}