package de.htwg.se.msiwar.aview

import de.htwg.se.msiwar.aview.tui.MsiWarTui
import de.htwg.se.msiwar.controller.MsiWarControllerImpl
import de.htwg.se.msiwar.model.{Action, MsiWarModelImpl, PlayerObject, Position}
import de.htwg.se.msiwar.util.GameConfigProvider

object MsiWarTuiApp {
  // TODO setup game
  // TODO get rows and columns from config
  val configProvider = new GameConfigProvider("config.txt")

  val model = MsiWarModelImpl(configProvider.rowCount, configProvider.colCount, configProvider.gameObjects)
  val controller = new MsiWarControllerImpl(model)
  val tui = new MsiWarTui(controller)

  def main(args: Array[String]) {
    while (tui.executeCommand(readLine())) {}
  }
}