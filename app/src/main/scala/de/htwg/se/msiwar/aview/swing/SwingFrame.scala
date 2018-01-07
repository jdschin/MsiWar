package de.htwg.se.msiwar.aview.swing

import javax.swing.WindowConstants

import de.htwg.se.msiwar.controller.{Controller, GameStarted, PlayerWon}
import de.htwg.se.msiwar.util.ImageUtils

import scala.swing._

class SwingFrame(controller: Controller) extends Frame {
  private val contentPanel = new SwingPanel(controller)
  private val imageOpt = ImageUtils.loadImageIcon(controller.appIconImagePath)

  title = "Pixel Tank War"
  resizable = false
  contents = contentPanel

  if(imageOpt.isDefined) {
    iconImage = imageOpt.get.getImage
  }

  packAndCenter()
  peer.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE)

  listenTo(controller)
  reactions += {
    case _: GameStarted =>
      contentPanel.rebuild()
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