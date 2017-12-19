package de.htwg.se.msiwar.aview.swing


import java.io.File
import javax.imageio.ImageIO
import javax.swing.ImageIcon

import de.htwg.se.msiwar.controller.{CellChanged, Controller}

import scala.swing.event.MouseEntered
import scala.swing.{Graphics2D, GridPanel, Label, Reactor}

class SwingGui(controller: Controller) extends Reactor {

  listenTo(controller)
  reactions += {
    case e: CellChanged => {
      e.rowColumnIndexes.foreach(t => {
        updateLabel(t._1, t._2, labels(t._1)(t._2))
      })
    }
  }

  private val gridPanel = new GridPanel(controller.rowCount, controller.columnCount) {
    // TODO get background image from controller
    private val backgroundImage = ImageIO.read(new File("src/main/resources/images/background_woodlands.png"))

    override protected def paintComponent(g: Graphics2D): Unit = {
      super.paintComponent(g)
      g.drawImage(backgroundImage, null, 0, 0)
    }
  }
  private val labels = Array.ofDim[Label](gridPanel.rows, gridPanel.columns)

  for (i <- gridPanel.rows - 1 to 0 by -1) {
    for (j <- 0 until gridPanel.columns) {
      labels(i)(j) = new Label {
        border = new javax.swing.border.LineBorder(java.awt.Color.BLACK, 1, true)
        updateLabel(i, j, this)
        listenTo(mouse.moves)
        reactions += {
          case MouseEntered(_, _, _) => updateBorder(i, j)
        }
      }
      gridPanel.contents += labels(i)(j)
    }
  }

  def updateBorder(rowIndex: Int, columIndex: Int): Unit = {
    for (i <- 0 until gridPanel.rows; j <- 0 until gridPanel.columns) {
      val label = labels(i)(j)
      if (rowIndex == i && columIndex == j) {
        label.border = new javax.swing.border.LineBorder(java.awt.Color.BLUE, 4, true)
      } else {
        label.border = new javax.swing.border.LineBorder(java.awt.Color.BLACK, 1, true)
      }
    }
  }

  def content: GridPanel = {
    gridPanel
  }

  def updateLabel(rowIndex: Int, columnIndex: Int, label: Label): Unit = {
    val imagePath = controller.cellContentImagePath(rowIndex, columnIndex)
    if (imagePath.isDefined) {
      label.icon = new ImageIcon(imagePath.get)
    } else {
      label.icon = null
    }
    label.repaint()
  }
}
