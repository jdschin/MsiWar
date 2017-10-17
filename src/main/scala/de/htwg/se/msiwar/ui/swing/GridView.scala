package de.htwg.se.msiwar.ui.swing

import scala.swing.{GridPanel, Label}
import scala.swing.event.MouseEntered

class GridView(rows: Int, columns: Int) extends GridPanel(rows, columns) {
  private val labels = Array.ofDim[Label](rows, columns)

  def init {
    for (i <- 0 until rows; j <- 0 until columns) {
      labels(i)(j) = new Label {
        listenTo(mouse.moves)
        reactions += {
          case e: MouseEntered => updateBorder(i, j)
        }
      }
      labels(i)(j).background = java.awt.Color.WHITE
      contents += labels(i)(j)
    }
  }

  def updateBorder(rowIndex: Int, columIndex: Int): Unit = {
    for (i <- 0 until rows; j <- 0 until columns) {
      val label = labels(i)(j)
      if(rowIndex == i && columIndex == j) {
        label.border = new javax.swing.border.LineBorder(java.awt.Color.BLUE)
      } else {
        label.border = new javax.swing.border.LineBorder(java.awt.Color.WHITE)
      }
    }
  }

  init
}
