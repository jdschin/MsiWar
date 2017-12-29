package de.htwg.se.msiwar.aview.swing

import java.awt.Dimension

import de.htwg.se.msiwar.controller.Controller
import de.htwg.se.msiwar.util.ImageUtils

import scala.swing.event.ButtonClicked
import scala.swing.{Alignment, FlowPanel, Graphics2D, GridPanel, ToggleButton}

class SwingActionBarPanel(controller: Controller) extends FlowPanel {
  private val actionBarButtons = scala.collection.mutable.Map[Int, ToggleButton]()
  private var backgroundImage = ImageUtils.loadImage(controller.actionbarBackgroundImagePath)
  private var currentActionId: Option[Int] = Option.empty

  preferredSize = new Dimension(50, 50)
  reactions += {
    case e: ButtonClicked => updateActionActiveStates(e.source.asInstanceOf[ToggleButton])
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
          val imagePathOpt = ImageUtils.loadImageIcon(imagePath.get)
          if(imagePathOpt.isDefined){
            icon = imagePathOpt.get
          } else {
            icon = null
          }
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
    repaint()
  }

  def resize(width: Int, height: Int): Unit = {
    if (backgroundImage.isDefined) {
      backgroundImage = ImageUtils.scale(backgroundImage.get, width, height)
    }
    repaint()
  }

  def activeActionId: Option[Int] = {
    currentActionId
  }

  def activateActionId(actionInput: Char): Unit = {
    actionInput match {
      case actionId if '0' <= actionId && actionId <= '9' =>
        val foundActionBtn = actionBarButtons.find(p => p._1 == actionId.asDigit)
        if (foundActionBtn.isDefined) {
          foundActionBtn.get._2.selected = true
          updateActionActiveStates(foundActionBtn.get._2)
        }
      case _ => None
    }
  }

  private def updateActionActiveStates(source: ToggleButton): Unit = {
    if (source.selected) {
      val foundEntry = actionBarButtons.find(p => p._2 == source)
      if (foundEntry.isDefined) {
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

  override protected def paintComponent(g: Graphics2D): Unit = {
    super.paintComponent(g)
    if (backgroundImage.isDefined) {
      g.drawImage(backgroundImage.get, null, 0, 0)
    }
  }
}
