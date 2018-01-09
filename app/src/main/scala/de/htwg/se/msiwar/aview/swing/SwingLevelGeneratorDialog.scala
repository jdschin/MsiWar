package de.htwg.se.msiwar.aview.swing

import java.awt.Dimension

import de.htwg.se.msiwar.controller.Controller

import scala.swing.Dialog

class SwingLevelGeneratorDialog(controller: Controller) extends Dialog{

  title = "Random Level Settings"
  preferredSize = new Dimension(600, 650)
  modal = true
  resizable = false
  //contents += new GridPanel(2,3)

  peer.setLocationRelativeTo(null)
}
