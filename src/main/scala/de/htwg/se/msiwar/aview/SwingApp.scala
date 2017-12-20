package de.htwg.se.msiwar.aview

import java.awt.Dimension

import de.htwg.se.msiwar.aview.swing.SwingPanel
import de.htwg.se.msiwar.controller.Controller

import scala.swing._

class SwingApp(controller: Controller) extends SimpleSwingApplication {
  def top = new MainFrame {
    title = "Pixel Tank War"
    resizable = false
    minimumSize = new Dimension(controller.rowCount * 60, controller.columnCount * 60)
    contents = new SwingPanel(controller)
  }
}