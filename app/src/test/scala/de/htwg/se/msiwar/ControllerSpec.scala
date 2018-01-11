package de.htwg.se.msiwar

import java.nio.file.{Files, Paths}

import de.htwg.se.msiwar.controller.ControllerImpl
import de.htwg.se.msiwar.model.GameModelImpl
import de.htwg.se.msiwar.util.{Direction, GameConfigProviderImpl}
import org.scalatest.{FlatSpec, Matchers}

class ControllerSpec extends FlatSpec with Matchers {
  private val resourcePathPrefix = "src/main/resources/"

  ControllerImpl.getClass.getSimpleName should "return turn counter of 1 at game start" in {
    val controller = ControllerImpl(GameModelImpl(new GameConfigProviderImpl))
    controller.turnCounter should be (1)
  }

  it should "return a valid path for opening background" in {
    val testConfigProvider = new TestConfigProvider
    testConfigProvider.load2PlayerEmptyMapScenario()

    val model = GameModelImpl(testConfigProvider)
    val controller = ControllerImpl(model)
    Files.exists(Paths.get(resourcePathPrefix + controller.openingBackgroundImagePath)) should be(true)
  }

  it should "return a valid path for level background" in {
    val testConfigProvider = new TestConfigProvider
    testConfigProvider.load2PlayerEmptyMapScenario()

    val model = GameModelImpl(testConfigProvider)
    val controller = ControllerImpl(model)
    Files.exists(Paths.get(resourcePathPrefix + controller.levelBackgroundImagePath)) should be(true)
  }

  it should "return a valid path for action bar background" in {
    val testConfigProvider = new TestConfigProvider
    testConfigProvider.load2PlayerEmptyMapScenario()

    val model = GameModelImpl(testConfigProvider)
    val controller = ControllerImpl(model)
    Files.exists(Paths.get(resourcePathPrefix + controller.actionbarBackgroundImagePath)) should be(true)
  }

  it should "return a valid path for app icon" in {
    val testConfigProvider = new TestConfigProvider
    testConfigProvider.load2PlayerEmptyMapScenario()

    val model = GameModelImpl(testConfigProvider)
    val controller = ControllerImpl(model)
    Files.exists(Paths.get(resourcePathPrefix + controller.appIconImagePath)) should be(true)
  }

  it should "return the name of the active player" in {
    val testConfigProvider = new TestConfigProvider
    testConfigProvider.load2PlayerDamageTestScenario()

    val model = GameModelImpl(testConfigProvider)
    val controller = ControllerImpl(model)
    controller.activePlayerName should be("Player1")
  }

  it should "return cell content image path" in {
    val testConfigProvider = new TestConfigProvider
    testConfigProvider.load2PlayerDamageTestScenario()

    val model = GameModelImpl(testConfigProvider)
    val controller = ControllerImpl(model)
    val imagePathOpt = controller.cellContentImagePath(0, 0)
    imagePathOpt.isDefined should be(true)
    imagePathOpt.get should be("images/light_tank_red_180.png")
  }

  it should "return cell content as text" in {
    val testConfigProvider = new TestConfigProvider
    testConfigProvider.load2PlayerDamageTestScenario()

    val model = GameModelImpl(testConfigProvider)
    val controller = ControllerImpl(model)
    controller.cellContentToText(0, 0) should be("1")
  }

  it should "execute an action" in {
    val testConfigProvider = new TestConfigProvider
    testConfigProvider.load2PlayerDamageTestScenario()

    val model = GameModelImpl(testConfigProvider)
    val controller = ControllerImpl(model)
    controller.canExecuteAction(2, Direction.DOWN) should be(true)
    controller.executeAction(2, Direction.DOWN)
    controller.canExecuteAction(2, 1, 0) should be(true)
    controller.executeAction(1,1,0)
  }

  it should "return the action ids for a player" in {
    val testConfigProvider = new TestConfigProvider
    testConfigProvider.load2PlayerDamageTestScenario()

    val model = GameModelImpl(testConfigProvider)
    val controller = ControllerImpl(model)
    controller.actionIds(1).size should be(1)
  }

  it should "return the number of the active player" in {
    val testConfigProvider = new TestConfigProvider
    testConfigProvider.load2PlayerDamageTestScenario()

    val model = GameModelImpl(testConfigProvider)
    val controller = ControllerImpl(model)
    controller.activePlayerNumber should be(1)
  }

  it should "return row count 10 at game start" in {
    val testConfigProvider = new TestConfigProvider
    testConfigProvider.load2PlayerEmptyMapScenario()

    val model = GameModelImpl(testConfigProvider)
    val controller = ControllerImpl(model)
    controller.rowCount should be(10)
  }

  it should "return column count 2 at game start" in {
    val testConfigProvider = new TestConfigProvider
    testConfigProvider.load2PlayerEmptyMapScenario()

    val model = GameModelImpl(testConfigProvider)
    val controller = ControllerImpl(model)
    controller.columnCount should be(2)
  }

  it should "return a list of scenario ids available" in {
    val testConfigProvider = new TestConfigProvider
    testConfigProvider.load2PlayerEmptyMapScenario()

    val model = GameModelImpl(testConfigProvider)
    val controller = ControllerImpl(model)
    controller.scenarioIds.size should be(2)
  }

  it should "return a scenario name for a valid scenario id" in {
    val testConfigProvider = new TestConfigProvider
    testConfigProvider.load2PlayerEmptyMapScenario()

    val model = GameModelImpl(testConfigProvider)

    val controller = ControllerImpl(model)
    val scenarioNameFound = controller.scenarioName(0)
    scenarioNameFound.isDefined should be(true)
    scenarioNameFound.get should be("S1 Scenario (2-Player)")
  }

  it should "not return a scenario name for a unknown scenario id" in {
    val testConfigProvider = new TestConfigProvider
    testConfigProvider.load2PlayerEmptyMapScenario()

    val model = GameModelImpl(testConfigProvider)
    val controller = ControllerImpl(model)
    val scenarioNameNotFound = controller.scenarioName(3)
    scenarioNameNotFound.isDefined should be(false)
  }

  it should "return the health of the active player" in {
    val testConfigProvider = new TestConfigProvider
    testConfigProvider.load2PlayerDamageTestScenario()

    val model = GameModelImpl(testConfigProvider)
    val controller = ControllerImpl(model)
    controller.activePlayerHealthPoints should be(3)
  }

  it should "return the actions points of the active player" in {
    val testConfigProvider = new TestConfigProvider
    testConfigProvider.load2PlayerDamageTestScenario()

    val model = GameModelImpl(testConfigProvider)
    val controller = ControllerImpl(model)
    controller.activePlayerActionPoints should be(3)
  }

  it should "return damage value for an action id" in {
    val testConfigProvider = new TestConfigProvider
    testConfigProvider.load2PlayerDamageTestScenario()

    val model = GameModelImpl(testConfigProvider)
    val controller = ControllerImpl(model)
    controller.actionDamage(2) should be(2)
  }

  it should "return range value for an action id" in {
    val testConfigProvider = new TestConfigProvider
    testConfigProvider.load2PlayerDamageTestScenario()

    val model = GameModelImpl(testConfigProvider)
    val controller = ControllerImpl(model)
    controller.actionRange(2) should be(3)
  }

  it should "return action cost value for an action id" in {
    val testConfigProvider = new TestConfigProvider
    testConfigProvider.load2PlayerDamageTestScenario()

    val model = GameModelImpl(testConfigProvider)
    val controller = ControllerImpl(model)
    controller.actionPointCost(2) should be(1)
  }

  it should "return action description for an action id" in {
    val testConfigProvider = new TestConfigProvider
    testConfigProvider.load2PlayerDamageTestScenario()

    val model = GameModelImpl(testConfigProvider)
    val controller = ControllerImpl(model)
    controller.actionDescription(2) should be("Shoot")
  }

  it should "return action icon path for an action id" in {
    val testConfigProvider = new TestConfigProvider
    testConfigProvider.load2PlayerDamageTestScenario()

    val model = GameModelImpl(testConfigProvider)
    val controller = ControllerImpl(model)
    controller.actionIconPath(2).isDefined should be(true)
  }

  it should "not return action icon path for an unknown action id" in {
    val testConfigProvider = new TestConfigProvider
    testConfigProvider.load2PlayerDamageTestScenario()

    val model = GameModelImpl(testConfigProvider)
    val controller = ControllerImpl(model)
    controller.actionIconPath(1).isDefined should be(false)
  }

  it should "start a new game for valid scenario id" in {
    val testConfigProvider = new TestConfigProvider
    testConfigProvider.load2PlayerDamageTestScenario()

    val model = GameModelImpl(testConfigProvider)
    val controller = ControllerImpl(model)
    controller.startGame(0)
  }

  it should "start a random game" in {
    val testConfigProvider = new TestConfigProvider
    testConfigProvider.load2PlayerDamageTestScenario()

    val model = GameModelImpl(testConfigProvider)
    val controller = ControllerImpl(model)
    controller.startRandomGame()
  }
}