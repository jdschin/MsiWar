package de.htwg.se.msiwar.aview.swing

import java.awt.Dimension
import java.io.File
import javax.imageio.ImageIO
import javax.swing.ImageIcon

import de.htwg.se.msiwar.controller.Controller

import scala.swing.{FlowPanel, Graphics2D, GridPanel, ToggleButton}

class SwingActionBarPanel(controller: Controller) extends FlowPanel {
  private val backgroundImage = ImageIO.read(new File(controller.actionbarBackgroundImagePath.get))

  preferredSize = new Dimension(50,50)

  override protected def paintComponent(g: Graphics2D): Unit = {
    super.paintComponent(g)
    g.drawImage(backgroundImage, null, 0, 0)
  }

  def updateActionBar(playerNumber: Int): Unit = {
    val actionIds = controller.actionIds(playerNumber)
    val actionBar = new GridPanel(1, actionIds.size)
    actionIds.foreach(a => {
      val actionBtn = new ToggleButton(){
        val imagePath = controller.actionIconPath(a)
        if(imagePath.isDefined){
          icon = new ImageIcon(imagePath.get)
        } else {
          icon = null
          text = "Action" + a
        }
      }
      actionBar.contents += actionBtn
    })

    _contents += actionBar
    revalidate()
  }
}
