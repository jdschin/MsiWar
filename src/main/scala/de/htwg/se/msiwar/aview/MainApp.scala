package de.htwg.se.msiwar.aview

import de.htwg.se.msiwar.controller.ControllerImpl
import de.htwg.se.msiwar.model.GameModelImpl
import de.htwg.se.msiwar.util.GameConfigProvider

object MainApp {
  def main(args: Array[String]): Unit = {
    // TODO setup game
    // TODO get rows and columns from config
    val configProvider = new GameConfigProvider("config.txt")

    val model = GameModelImpl(configProvider.rowCount, configProvider.colCount, configProvider.gameObjects)
    val controller = new ControllerImpl(model)
    new SwingApp(controller).main(args)
    TuiApp.main(args, controller)
  }
}
