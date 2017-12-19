package de.htwg.se.msiwar.aview

import de.htwg.se.msiwar.aview.swing.SwingGui
import de.htwg.se.msiwar.controller.Controller

import scala.swing._

class SwingApp(controller: Controller) extends SimpleSwingApplication {
  def top = new MainFrame {
    title = "Pixel Tank War"
    preferredSize = new Dimension(600, 600)
    minimumSize = new Dimension(400, 400)
    contents = new SwingGui(controller).content
  }
}