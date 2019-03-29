package de.htwg.se.msiwar.aview

import de.htwg.se.msiwar.aview.swing.SwingFrame
import de.htwg.se.msiwar.aview.tui.Tui
import de.htwg.se.msiwar.controller.ControllerImpl
import de.htwg.se.msiwar.model.{Action, GameBoard, GameModelImpl, PlayerObject}
import de.htwg.se.msiwar.util.GameConfigProviderImpl

object MainApp {
  val gameConfigProvider = new GameConfigProviderImpl
  val player = gameConfigProvider.gameObjects.collect({ case s: PlayerObject => s }).find(_.playerNumber == 1).get
  val turn = 1

  val createdModel = GameModelImpl(gameConfigProvider, GameBoard(gameConfigProvider.rowCount, gameConfigProvider.colCount, gameConfigProvider.gameObjects), Option.empty[Action], player, turn)
  val controller = ControllerImpl(createdModel)
  val swingFrame = new SwingFrame(controller)
  swingFrame.visible = true

  val tui = new Tui(controller)

  def main(args: Array[String]): Unit = {
    while (tui.executeCommand(readLine())) {}
  }
}
