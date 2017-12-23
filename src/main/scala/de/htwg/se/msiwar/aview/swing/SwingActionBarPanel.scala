package de.htwg.se.msiwar.aview.swing

import java.awt.Dimension
import java.io.File
import javax.imageio.ImageIO
import javax.swing.ImageIcon

import de.htwg.se.msiwar.controller.Controller

import scala.swing.event.ButtonClicked
import scala.swing.{Alignment, FlowPanel, Graphics2D, GridPanel, ToggleButton}

class SwingActionBarPanel(controller: Controller) extends FlowPanel {
  private val backgroundImage = ImageIO.read(new File(controller.actionbarBackgroundImagePath))
  private val actionBarButtons = scala.collection.mutable.Map[Int, ToggleButton]()
  private var currentActionId: Option[Int] = Option.empty

  preferredSize = new Dimension(50, 50)
  reactions += {
    case e: ButtonClicked => updateActionActiveStates(e.source.asInstanceOf[ToggleButton])
  }

  override protected def paintComponent(g: Graphics2D): Unit = {
    super.paintComponent(g)
    g.drawImage(backgroundImage, null, 0, 0)
  }

  def updateActionBar(playerNumber: Int): Unit = {
    // Clear previous content
    actionBarButtons.clear()
    _contents.clear()

    val actionIds = controller.actionIds(playerNumber)
    val actionBar = new GridPanel(1, actionIds.size)
    actionIds.foreach(actionId => {
      val actionBtn = new ToggleButton() {
        private val imagePath = controller.actionIconPath(actionId)
        if (imagePath.isDefined) {
          icon = new ImageIcon(imagePath.get)
          text = actionId.toString
          horizontalTextPosition = Alignment.Right
          verticalTextPosition = Alignment.Bottom
        } else {
          icon = null
          text = "Action" + actionId
        }
        tooltip = controller.actionDescription(actionId)
      }
      listenTo(actionBtn)
      actionBar.contents += actionBtn
      actionBarButtons.put(actionId, actionBtn)
    })

    _contents += actionBar
    revalidate()
  }

  def activeActionId: Option[Int] = {
    currentActionId
  }

  private def updateActionActiveStates(source: ToggleButton): Unit = {
    if (source.enabled) {
      val foundEntry = actionBarButtons.find(p => p._2 == source)
      if(foundEntry.isDefined){
        currentActionId = Option(foundEntry.get._1)
      } else {
        currentActionId = Option.empty
      }
      controller.cellsInRange(currentActionId)
      // Deselect all other toggle buttons
      actionBarButtons.foreach(t => {
        if (t._2 != source) {
          t._2.selected = false
        }
      })
    }
  }
}
