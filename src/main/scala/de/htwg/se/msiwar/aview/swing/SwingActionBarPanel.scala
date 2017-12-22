package de.htwg.se.msiwar.aview.swing

import java.awt.Dimension
import javax.swing.ImageIcon

import de.htwg.se.msiwar.controller.Controller

import scala.swing.{FlowPanel, GridPanel, Label}

class SwingActionBarPanel(controller: Controller) extends FlowPanel {
  preferredSize = new Dimension(32,32)

  def updateActionBar(playerNumber: Int): Unit = {
    val actionIds = controller.actionIds(playerNumber)
    val actionBar = new GridPanel(1, actionIds.size)
    actionIds.foreach(a => {
      val actionLabel = new Label{
        preferredSize = new Dimension(32,32)
        border = new javax.swing.border.LineBorder(java.awt.Color.BLACK, 1, true)
        val imagePath = controller.actionIconPath(a)
        if(imagePath.isDefined){
          icon = new ImageIcon(imagePath.get)
        } else {
          icon = null
          text = "Action" + a
        }
      }
      actionBar.contents += actionLabel
    })

    _contents += actionBar
    revalidate()
    //repaint()
  }
}
