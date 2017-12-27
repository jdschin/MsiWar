package de.htwg.se.msiwar.aview

import java.io.FileNotFoundException
import javax.swing.SwingUtilities

import de.htwg.se.msiwar.aview.tui.Tui
import de.htwg.se.msiwar.controller.ControllerImpl
import de.htwg.se.msiwar.model.GameModelImpl
import de.htwg.se.msiwar.util.{GameConfigProvider, JSONException}

object MainApp {
  def main(args: Array[String]): Unit = {

    try {
      GameConfigProvider.loadFromFile("src/main/resources/scenarios/2_black_wood_battle.json")
    }
    catch {
      case e: FileNotFoundException => print(e.getMessage)
      case e: JSONException => print(e.getMessage)
      case e: NoSuchElementException => print(e.getMessage)
    }

    val model = GameModelImpl(GameConfigProvider.rowCount, GameConfigProvider.colCount, GameConfigProvider.gameObjects, GameConfigProvider.levelBackgroundImagePath, GameConfigProvider.actionbarBackgroundImagePath, GameConfigProvider.attackImagePath, GameConfigProvider.attackSoundPath)
    val controller = new ControllerImpl(model)

    new SwingApp(controller).main(args)
    val tui = new Tui(controller)

    // Starts the initial game, needs to be invoked later to allow gui to be shown before
    val task = new Runnable {
      def run(): Unit = controller.reset
    }
    SwingUtilities.invokeLater(task)
    while (tui.executeCommand(readLine())) {}
  }
}
