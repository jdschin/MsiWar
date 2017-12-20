package de.htwg.se.msiwar.aview.swing


import java.io.File
import javax.imageio.ImageIO
import javax.swing.ImageIcon

import de.htwg.se.msiwar.controller.{CellChanged, Controller}

import scala.swing.event.MouseEntered
import scala.swing.{Graphics2D, GridPanel, Label, Reactor}

class SwingGui(controller: Controller) extends GridPanel(controller.rowCount, controller.columnCount) with Reactor {
  // TODO get background image from controller
  private val backgroundImage = ImageIO.read(new File(controller.backgroundImagePath))
  private val labels = Array.ofDim[Label](rows, columns)

  override protected def paintComponent(g: Graphics2D): Unit = {
    super.paintComponent(g)
    g.drawImage(backgroundImage, null, 0, 0)
  }

  listenTo(controller)
  reactions += {
    case e: CellChanged => {
      e.rowColumnIndexes.foreach(t => {
        updateLabel(t._1, t._2)
      })
    }
  }
  fillBoard

  def fillBoard: Unit = {
    for (i <- rows - 1 to 0 by -1) {
      for (j <- 0 until columns) {
        labels(i)(j) = new Label {
          border = new javax.swing.border.LineBorder(java.awt.Color.BLACK, 1, true)
          listenTo(mouse.moves)
          reactions += {
            case MouseEntered(_, _, _) => updateBorder(i, j)
          }
        }
        contents += labels(i)(j)
        updateLabel(i, j)
      }
    }
  }

  def updateBorder(rowIndex: Int, columIndex: Int): Unit = {
    for (i <- 0 until rows; j <- 0 until columns) {
      val label = labels(i)(j)
      if (rowIndex == i && columIndex == j) {
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
