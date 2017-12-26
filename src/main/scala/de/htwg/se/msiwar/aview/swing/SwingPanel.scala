package de.htwg.se.msiwar.aview.swing


import java.awt.Dimension
import java.io.File
import java.util.concurrent.{ScheduledThreadPoolExecutor, TimeUnit}
import javax.imageio.ImageIO
import javax.swing.{ImageIcon, SwingUtilities}

import de.htwg.se.msiwar.controller.{Controller, _}
import de.htwg.se.msiwar.util.SoundPlayer

import scala.swing.event.MousePressed
import scala.swing.{BorderPanel, Graphics2D, GridPanel, Label, Reactor}

class SwingPanel(controller: Controller) extends BorderPanel with Reactor {
  private val poolExecutor = new ScheduledThreadPoolExecutor(1)
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

  listenTo(controller)
  reactions += {
    case e: CellChanged =>
      e.rowColumnIndexes.foreach(t => {
        updateLabel(t._1, t._2)
      })
    case e: TurnStarted =>
      actionPanel.updateActionBar(e.playerNumber)

    case e: CellsInRange =>
      clearCellsInRange()
      e.rowColumnIndexes.foreach(t => {
        labels(t._1)(t._2).border = new javax.swing.border.LineBorder(java.awt.Color.GREEN, 4, true)
      })
    case e: PlayerWon =>
      _contents.clear()

      add(menuBar, BorderPanel.Position.North)
      _contents += new Label {
        icon = new ImageIcon(e.wonImagePath)
      }
      revalidate()
      repaint()

    case e: AttackActionResult =>
      SoundPlayer.playWav(e.attackSoundPath)
      updateLabelTemporary(e.rowIndex, e.columnIndex, e.attackImagePath, 1)
  }
  createContent()
  fillBoard()

  private def createContent(): Unit = {
    add(menuBar, BorderPanel.Position.North)
    add(gridPanel, BorderPanel.Position.Center)
    add(actionPanel, BorderPanel.Position.South)
  }

  private def fillBoard(): Unit = {
    for (i <- 0 until gridPanel.rows) {
      for (j <- 0 until gridPanel.columns) {
        labels(i)(j) = new Label {
          border = new javax.swing.border.LineBorder(java.awt.Color.BLACK, 1, true)
          listenTo(mouse.clicks)
          reactions += {
            case e: MousePressed =>
              val activeActionId = actionPanel.activeActionId
              if (activeActionId.isDefined && controller.canExecuteAction(activeActionId.get, i, j)) {
                controller.executeAction(actionPanel.activeActionId.get, i, j)
              } else {
                // TODO play sound
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

  private def updateLabelTemporary(rowIndex: Int, columnIndex: Int, pathOfImageToShow: String, seconds: Int): Unit = {
    // Temporary show set new icon, will be replaced with old one after configured delay
    labels(rowIndex)(columnIndex).icon = new ImageIcon(pathOfImageToShow)
    val task = new Runnable {
      def run(): Unit = {
        // Restore old icon after configured delay
        val uiTask = new Runnable {
          def run(): Unit = updateLabel(rowIndex,columnIndex)
        }
        SwingUtilities.invokeLater(uiTask)
      }
    }
    poolExecutor.schedule(task, seconds, TimeUnit.SECONDS)
  }

  private def clearCellsInRange(): Unit = {
    for (i <- 0 until gridPanel.rows) {
      for (j <- 0 until gridPanel.columns) {
        labels(i)(j).border = new javax.swing.border.LineBorder(java.awt.Color.BLACK, 1, true)
      }
    }
  }
}
