package de.htwg.se.msiwar.aview.swing

import de.htwg.se.msiwar.controller.{Controller, GameStarted}

import scala.swing._

class SwingFrame(controller: Controller) extends Frame {
  title = "Pixel Tank War"
  resizable = true
  contents = new SwingPanel(controller)
  pack()

  // Center on monitor
  peer.setLocationRelativeTo(null)

  listenTo(controller)
  reactions += {
    case _: GameStarted =>
      pack()
      // Center on monitor
      peer.setLocationRelativeTo(null)
  }
}