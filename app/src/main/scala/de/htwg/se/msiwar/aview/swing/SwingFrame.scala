package de.htwg.se.msiwar.aview.swing

import de.htwg.se.msiwar.controller.{Controller, GameStarted, PlayerWon}

import scala.swing._

class SwingFrame(controller: Controller) extends Frame {
  title = "Pixel Tank War"
  resizable = true
  contents = new SwingPanel(controller)
  packAndCenter

  listenTo(controller)
  reactions += {
    case _: GameStarted => packAndCenter
    case _: PlayerWon => packAndCenter
  }

  private def packAndCenter : Unit = {
    pack()
    // Center on monitor
    peer.setLocationRelativeTo(null)
  }
}