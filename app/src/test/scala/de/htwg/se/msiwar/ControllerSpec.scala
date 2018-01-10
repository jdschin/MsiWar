package de.htwg.se.msiwar

import java.nio.file.{Files, Paths}

import de.htwg.se.msiwar.controller.ControllerImpl
import de.htwg.se.msiwar.model.GameModelImpl
import de.htwg.se.msiwar.util.GameConfigProviderImpl
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
}