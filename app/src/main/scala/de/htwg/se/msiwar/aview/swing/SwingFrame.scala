package de.htwg.se.msiwar.aview.swing

import de.htwg.se.msiwar.controller.{Controller, GameStarted, PlayerWon}

import scala.swing._

class SwingFrame(controller: Controller) extends Frame {
  private val contentPanel = new SwingPanel(controller)

  title = "Pixel Tank War"
  resizable = true
  contents = contentPanel
  packAndCenter

  listenTo(controller)
  reactions += {
    case _: GameStarted => packAndCenter
    case _: PlayerWon => packAndCenter
  }

  private def packAndCenter : Unit = {
    pack()
    contentPanel.resize(peer.getWidth, peer.getHeight)
    // Center on monitor
    peer.setLocationRelativeTo(null)
  }
}