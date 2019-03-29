package de.htwg.se.msiwar.aview

import de.htwg.se.msiwar.aview.swing.SwingFrame
import de.htwg.se.msiwar.aview.tui.Tui
import de.htwg.se.msiwar.controller.ControllerImpl
import de.htwg.se.msiwar.model._
import de.htwg.se.msiwar.util.{GameConfigProvider, GameConfigProviderImpl}

object MainApp {
  var gameConfigProvider: GameConfigProvider = new GameConfigProviderImpl(List[GameObject](),"","","",
    "", "", "", 1, 1)
  val scenarioName = gameConfigProvider.listScenarios(0)
  gameConfigProvider = gameConfigProvider.loadFromFile(scenarioName)

  val player = gameConfigProvider.gameObjects.collect({ case s: PlayerObject => s }).find(_.playerNumber == 1).get
  val turn = 1

  val createdModel = GameModelImpl(gameConfigProvider, GameBoard(gameConfigProvider.rowCount, gameConfigProvider.colCount,
    gameConfigProvider.gameObjects), Option.empty[Action], player, turn)

  val controller = ControllerImpl(createdModel)
  val swingFrame = new SwingFrame(controller)
  swingFrame.visible = true

  val tui = new Tui(controller)

  def main(args: Array[String]): Unit = {
    while (tui.executeCommand(readLine())) {}
  }
}
