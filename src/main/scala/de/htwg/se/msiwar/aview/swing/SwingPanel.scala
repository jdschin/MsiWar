package de.htwg.se.msiwar.aview.swing


import java.awt.Dimension
import java.io.File
import javax.imageio.ImageIO
import javax.swing.ImageIcon

import de.htwg.se.msiwar.controller.{Controller, _}
import de.htwg.se.msiwar.util.Direction

import scala.swing.event.MousePressed
import scala.swing.{BorderPanel, Graphics2D, GridPanel, Label, Reactor}

class SwingPanel(controller: Controller) extends BorderPanel with Reactor {
  private val backgroundImage = ImageIO.read(new File(controller.levelBackgroundImagePath))
  private val labels = Array.ofDim[Label](controller.rowCount, controller.columnCount)
  private val actionPanel = new SwingActionBarPanel(controller)
  private val menuBar = new SwingMenuBar(controller)
  private val gridPanel = new GridPanel(controller.rowCount, controller.columnCount) {
    preferredSize = new Dimension(controller.rowCount * 60, controller.columnCount * 60)

    override protected def paintComponent(g: Graphics2D): Unit = {
      super.paintComponent(g)
      g.drawImage(backgroundImage, null, 0, 0)
    }
  }

  add(menuBar, BorderPanel.Position.North)
  add(gridPanel, BorderPanel.Position.Center)
  add(actionPanel, BorderPanel.Position.South)

  listenTo(controller)
  reactions += {
    case e: CellChanged => {
      e.rowColumnIndexes.foreach(t => {
        updateLabel(t._1, t._2)
      })
    }
    case e: TurnStarted => {
      actionPanel.updateActionBar(e.playerNumber)
    }
    case e: CellsInRange => {
      clearCellsInRange
      e.rowColumnIndexes.foreach(t => {
        labels(t._1)(t._2).border = new javax.swing.border.LineBorder(java.awt.Color.GREEN, 4, true)
      })
    }
    case e: PlayerWon => {
      // TODO display game won message
    }
  }
  fillBoard

  private def fillBoard: Unit = {
    for (i <- gridPanel.rows - 1 to 0 by -1) {
      for (j <- 0 until gridPanel.columns) {
        labels(i)(j) = new Label {
          border = new javax.swing.border.LineBorder(java.awt.Color.BLACK, 1, true)
          listenTo(mouse.clicks)
          reactions += {
            case e: MousePressed => {
              val activeActionId = actionPanel.activeActionId
              if (activeActionId.isDefined && controller.canExecuteAction(activeActionId.get, Direction.UP)) {
                controller.executeAction(actionPanel.activeActionId.get, Direction.UP)
              } else {
                // TODO play sound
              }
            }
          }
        }
        gridPanel.contents += labels(i)(j)
        updateLabel(i, j)
      }
    }
  }

  private def updateLabel(rowIndex: Int, columnIndex: Int): Unit = {
    val label = labels(rowIndex)(columnIndex)
    val imagePath = controller.cellContentImagePath(rowIndex, columnIndex)
    if (imagePath.isDefined) {
      label.icon = new ImageIcon(imagePath.get)
    } else {
      label.icon = null
    }
    label.repaint()
  }

  private def clearCellsInRange: Unit = {
    for (i <- gridPanel.rows - 1 to 0 by -1) {
      for (j <- 0 until gridPanel.columns) {
        labels(i)(j).border = new javax.swing.border.LineBorder(java.awt.Color.BLACK, 1, true)
      }
    }
  }
}
