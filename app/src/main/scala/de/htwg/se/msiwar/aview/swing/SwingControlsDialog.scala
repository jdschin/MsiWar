package de.htwg.se.msiwar.aview.swing

import java.awt.Dimension

import de.htwg.se.msiwar.util.ImageUtils

import scala.swing.{BorderPanel, Dialog, Graphics2D}

class SwingControlsDialog extends Dialog{
  private val backgroundImage = ImageUtils.loadImage("images/controls.png")
  private val contentPanel = new BorderPanel{
    override protected def paintComponent(g: Graphics2D): Unit = {
      super.paintComponent(g)
      if (backgroundImage.isDefined) {
        g.drawImage(backgroundImage.get, null, 0, 0)
      }
    }
  }

  title = "Pixel Tank War V.1.0 - Steuerung"
  preferredSize = new Dimension(600, 650)
  modal = true
  resizable = false
  contents = contentPanel

  peer.setLocationRelativeTo(null)
}
