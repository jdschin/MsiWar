package de.htwg.se.msiwar.aview

import de.htwg.se.msiwar.aview.swing.SwingPanel
import de.htwg.se.msiwar.controller.Controller

import scala.swing._

class SwingApp(controller: Controller) extends SimpleSwingApplication {
  def top: MainFrame = new MainFrame {
    title = "Pixel Tank War"
    resizable = false
    contents = new SwingPanel(controller)
    pack()

    // Center on monitor
    peer.setLocationRelativeTo(null)
  }
}