package de.htwg.se.msiwar.aview.swing

import de.htwg.se.msiwar.controller.MsiWarController

import scala.swing.event.MouseEntered
import scala.swing.{GridPanel, Label}

class GridView(msiWarController: MsiWarController) {
  private val gridPanel = new GridPanel(msiWarController.getRowCount, msiWarController.getColumnCount)
  private val labels = Array.ofDim[Label](gridPanel.rows,gridPanel.columns)

  gridPanel.background = java.awt.Color.WHITE

  for (i <- 0 until gridPanel.rows; j <- 0 until gridPanel.columns) {
    labels(i)(j) = new Label {
      listenTo(mouse.moves)
      reactions += {
        case MouseEntered(_,_,_) => updateBorder(i, j)
      }
    }
    labels(i)(j).background = java.awt.Color.WHITE
    gridPanel.contents += labels(i)(j)
  }

  def updateBorder(rowIndex: Int, columIndex: Int): Unit = {
    for (i <- 0 until gridPanel.rows; j <- 0 until gridPanel.columns) {
      val label = labels(i)(j)
      if (rowIndex == i && columIndex == j) {
        label.border = new javax.swing.border.LineBorder(java.awt.Color.BLUE)
      } else {
        label.border = new javax.swing.border.LineBorder(java.awt.Color.WHITE)
      }
    }
  }

  def content: GridPanel = {gridPanel}
}
