package de.htwg.se.msiwar.aview

import de.htwg.se.msiwar.aview.swing.GridView
import de.htwg.se.msiwar.controller.MsiWarControllerImpl

import scala.swing._

object MsiWarSwingApp extends SimpleSwingApplication {
  def top = new MainFrame {
    title = "MSI WAR"
    preferredSize = new Dimension(600, 600)
    minimumSize = new Dimension(400, 400)

    // TODO inject
    val controller = new MsiWarControllerImpl
    contents = new GridView(controller)
  }
}