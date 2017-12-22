package de.htwg.se.msiwar.aview

import javax.swing.SwingUtilities

import de.htwg.se.msiwar.aview.tui.Tui
import de.htwg.se.msiwar.controller.ControllerImpl
import de.htwg.se.msiwar.model.GameModelImpl
import de.htwg.se.msiwar.util.GameConfigProvider

object MainApp {
  def main(args: Array[String]): Unit = {
    val configProvider = new GameConfigProvider("config.txt")

    val model = GameModelImpl(configProvider.rowCount, configProvider.colCount, configProvider.gameObjects)
    val controller = new ControllerImpl(model)

    new SwingApp(controller).main(args)
    val tui = new Tui(controller)

    // Starts the initial game, needs to be invoked later to allow gui to be shown before
    SwingUtilities.invokeLater(new Runnable {
      override def run(): Unit = {
        controller.reset
      }
    })

    while (tui.executeCommand(readLine())) {}
  }
}
