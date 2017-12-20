package de.htwg.se.msiwar.aview.swing


import java.io.File
import javax.imageio.ImageIO
import javax.swing.ImageIcon

import de.htwg.se.msiwar.controller.{Controller, _}

import scala.swing.event.MouseEntered
import scala.swing.{BorderPanel, Graphics2D, GridPanel, Label, Reactor}

class SwingPanel(controller: Controller) extends BorderPanel with Reactor {
  private val backgroundImage = ImageIO.read(new File(controller.backgroundImagePath))
  private val labels = Array.ofDim[Label](controller.rowCount, controller.columnCount)
  private val gridPanel = new GridPanel(controller.rowCount, controller.columnCount) {
    override protected def paintComponent(g: Graphics2D): Unit = {
      super.paintComponent(g)
      g.drawImage(backgroundImage, null, 0, 0)
    }
  }
  private val actionPanel = new SwingActionBarPanel
  private val menuBar = new SwingMenuBar(controller)

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
    case e: TurnStarted => print("Turn started")
    case e: BlockHit => print("Block hit")
    case e: PlayerHit => print("Player hit")
  }
  fillBoard

  def fillBoard: Unit = {
    for (i <- gridPanel.rows - 1 to 0 by -1) {
      for (j <- 0 until gridPanel.columns) {
        labels(i)(j) = new Label {
          border = new javax.swing.border.LineBorder(java.awt.Color.BLACK, 1, true)
          listenTo(mouse.moves)
          reactions += {
            case MouseEntered(_, _, _) => updateBorder(i, j)
          }
        }
        gridPanel.contents += labels(i)(j)
        updateLabel(i, j)
      }
    }
  }

  def updateBorder(rowIndex: Int, columnIndex: Int): Unit = {
    for (i <- 0 until gridPanel.rows; j <- 0 until gridPanel.columns) {
      val label = labels(i)(j)
      if (rowIndex == i && columnIndex == j) {
        label.border = new javax.swing.border.LineBorder(java.awt.Color.BLUE, 4, true)
      } else {
        label.border = new javax.swing.border.LineBorder(java.awt.Color.BLACK, 1, true)
      }
    }
  }

  def updateLabel(rowIndex: Int, columnIndex: Int): Unit = {
    val label = labels(rowIndex)(columnIndex)
    val imagePath = controller.cellContentImagePath(rowIndex, columnIndex)
    if (imagePath.isDefined) {
      label.icon = new ImageIcon(imagePath.get)
    } else {
      label.icon = null
    }
    label.repaint()
  }
}
