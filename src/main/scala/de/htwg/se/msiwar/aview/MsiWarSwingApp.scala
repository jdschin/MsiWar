package de.htwg.se.msiwar.aview

import de.htwg.se.msiwar.aview.MsiWarTuiApp.configProvider
import de.htwg.se.msiwar.aview.swing.GridView
import de.htwg.se.msiwar.controller.MsiWarControllerImpl
import de.htwg.se.msiwar.model.MsiWarModelImpl

import scala.swing._

object MsiWarSwingApp extends SimpleSwingApplication {
  def top = new MainFrame {
    title = "MSI WAR"
    preferredSize = new Dimension(600, 600)
    minimumSize = new Dimension(400, 400)

    // TODO inject
    val model = MsiWarModelImpl(configProvider.rowCount, configProvider.colCount, configProvider.gameObjects)
    val controller = new MsiWarControllerImpl(model)
    contents = new GridView(controller).content
  }
}