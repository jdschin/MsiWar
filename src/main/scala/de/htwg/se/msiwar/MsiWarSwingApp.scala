package de.htwg.se.msiwar

import de.htwg.se.msiwar.ui.swing.GridView

import scala.swing._

object MsiWarSwingApp extends SimpleSwingApplication {
  def top = new MainFrame {
    title = "MSI WAR"
    preferredSize = new Dimension(600, 600)
    minimumSize = new Dimension(400, 400)
    contents = new GridView(9,9)
  }
}