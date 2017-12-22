package de.htwg.se.msiwar.aview

import de.htwg.se.msiwar.aview.tui.Tui
import de.htwg.se.msiwar.controller.ControllerImpl
import de.htwg.se.msiwar.model.GameModelImpl
import de.htwg.se.msiwar.util.GameConfigProvider

object MainApp {
  def main(args: Array[String]): Unit = {
    val configProvider = new GameConfigProvider("config.txt")

    val model = GameModelImpl(configProvider.rowCount, configProvider.colCount, configProvider.gameObjects)
    val controller = new ControllerImpl(model)

    val swingGui = new SwingApp(controller)
    swingGui.main(args)

    val tui = new Tui(controller)
    while (tui.executeCommand(readLine())) {}
  }
}
