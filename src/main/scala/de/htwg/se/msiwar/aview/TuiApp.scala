package de.htwg.se.msiwar.aview

import de.htwg.se.msiwar.aview.tui.Tui
import de.htwg.se.msiwar.controller.ControllerImpl
import de.htwg.se.msiwar.model.{Action, GameModelImpl, PlayerObject, Position}
import de.htwg.se.msiwar.util.GameConfigProvider

object TuiApp {
  // TODO setup game
  // TODO get rows and columns from config
  val configProvider = new GameConfigProvider("config.txt")

  val model = GameModelImpl(configProvider.rowCount, configProvider.colCount, configProvider.gameObjects)
  val controller = new ControllerImpl(model)
  val tui = new Tui(controller)

  def main(args: Array[String]) {
    while (tui.executeCommand(readLine())) {}
  }
}