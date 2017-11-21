package de.htwg.se.msiwar.aview

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
    val controller = new MsiWarControllerImpl(new MsiWarModelImpl)
    contents = new GridView(controller).content
  }
}