package de.htwg.se.msiwar.aview.swing


import java.io.File
import javax.imageio.ImageIO
import javax.swing.ImageIcon

import de.htwg.se.msiwar.controller.Controller

import scala.swing.event.MouseEntered
import scala.swing.{Graphics2D, GridPanel, Label}

class SwingGui(controller: Controller) {
  private val gridPanel = new GridPanel(controller.rowCount, controller.columnCount) {
    // TODO get background image from controller
    private val backgroundImage = ImageIO.read(new File("src/main/resources/images/background_woodlands.png"))

    override protected def paintComponent(g: Graphics2D): Unit = {
      super.paintComponent(g)
      g.drawImage(backgroundImage, null, 0, 0)
    }
  }
  private val labels = Array.ofDim[Label](gridPanel.rows, gridPanel.columns)

  for (i <- 0 until gridPanel.rows; j <- 0 until gridPanel.columns) {
    labels(i)(j) = new Label {
      val imagePath = controller.cellContentImagePath(i,j)
      if(imagePath.isDefined) {
        icon = new ImageIcon(imagePath.get)
      }
      listenTo(mouse.moves)
      reactions += {
        case MouseEntered(_, _, _) => updateBorder(i, j)
      }
    }
    gridPanel.contents += labels(i)(j)
  }

  def updateBorder(rowIndex: Int, columIndex: Int): Unit = {
    for (i <- 0 until gridPanel.rows; j <- 0 until gridPanel.columns) {
      val label = labels(i)(j)
      if (rowIndex == i && columIndex == j) {
        label.border = new javax.swing.border.LineBorder(java.awt.Color.BLUE, 4, true)
      } else {
        label.border = new javax.swing.border.LineBorder(java.awt.Color.WHITE)
      }
    }
  }

  def content: GridPanel = {gridPanel}
}
