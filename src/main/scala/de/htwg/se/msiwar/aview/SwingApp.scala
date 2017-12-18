package de.htwg.se.msiwar.aview

import de.htwg.se.msiwar.aview.TuiApp.configProvider
import de.htwg.se.msiwar.aview.swing.SwingGui
import de.htwg.se.msiwar.controller.ControllerImpl
import de.htwg.se.msiwar.model.GameModelImpl

import scala.swing._

object SwingApp extends SimpleSwingApplication {
  def top = new MainFrame {
    title = "MSI WAR"
    preferredSize = new Dimension(600, 600)
    minimumSize = new Dimension(400, 400)

    // TODO inject
    val model = GameModelImpl(configProvider.rowCount, configProvider.colCount, configProvider.gameObjects)
    val controller = new ControllerImpl(model)
    contents = new SwingGui(controller).content
  }
}