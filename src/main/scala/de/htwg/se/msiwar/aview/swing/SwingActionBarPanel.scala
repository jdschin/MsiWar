package de.htwg.se.msiwar.aview.swing

import java.awt.Dimension
import java.io.File
import javax.imageio.ImageIO
import javax.swing.ImageIcon

import de.htwg.se.msiwar.controller.Controller

import scala.collection.mutable.Buffer
import scala.swing.event.ButtonClicked
import scala.swing.{AbstractButton, FlowPanel, Graphics2D, GridPanel, ToggleButton}

class SwingActionBarPanel(controller: Controller) extends FlowPanel {
  private val backgroundImage = ImageIO.read(new File(controller.actionbarBackgroundImagePath))
  private var actionBarButtons: Buffer[ToggleButton] = Buffer[ToggleButton]()

  preferredSize = new Dimension(50, 50)
  reactions += {
    case e: ButtonClicked => updateActionActiveStates(e.source)
  }

  override protected def paintComponent(g: Graphics2D): Unit = {
    super.paintComponent(g)
    g.drawImage(backgroundImage, null, 0, 0)
  }

  def updateActionBar(playerNumber: Int): Unit = {
    actionBarButtons.clear()

    val actionIds = controller.actionIds(playerNumber)
    val actionBar = new GridPanel(1, actionIds.size)
    actionIds.foreach(a => {
      val actionBtn = new ToggleButton() {
        val imagePath = controller.actionIconPath(a)
        if (imagePath.isDefined) {
          icon = new ImageIcon(imagePath.get)
        } else {
          icon = null
          text = "Action" + a
        }
      }
      listenTo(actionBtn)
      actionBar.contents += actionBtn
      actionBarButtons += actionBtn
    })

    _contents += actionBar
    revalidate()
  }

  private def updateActionActiveStates(source: AbstractButton): Unit = {
    if (source.enabled) {
      // TODO set correct action id here
      controller.cellsInRange(Option(1))
      actionBarButtons.foreach(t => {
        if (t != source) {
          t.selected = false
        }
      })
    }
  }
}
