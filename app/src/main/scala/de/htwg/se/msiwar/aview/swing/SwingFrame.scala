package de.htwg.se.msiwar.aview.swing

import javax.swing.WindowConstants

import de.htwg.se.msiwar.controller.{Controller, GameStarted, PlayerWon}

import scala.swing._

class SwingFrame(controller: Controller) extends Frame {
  private val contentPanel = new SwingPanel(controller)

  title = "Pixel Tank War"
  resizable = false
  contents = contentPanel
  packAndCenter()
  peer.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE)

  listenTo(controller)
  reactions += {
    case _: GameStarted =>
      contentPanel.rebuild
      packAndCenter()
    case e: PlayerWon =>
      contentPanel.showPlayerWon(e)
      packAndCenter()
  }

  private def packAndCenter() : Unit = {
    pack()
    contentPanel.resize(peer.getWidth, peer.getHeight)
    // Center on monitor
    peer.setLocationRelativeTo(null)
  }
}