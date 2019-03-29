package de.htwg.se.msiwar.aview.swing


import java.awt.Dimension
import java.util.concurrent.{ScheduledThreadPoolExecutor, TimeUnit}

import de.htwg.se.msiwar.controller.Controller
import de.htwg.se.msiwar.model._
import de.htwg.se.msiwar.util.{ImageUtils, SoundPlayer}
import javax.swing.SwingUtilities

import scala.swing.event.{KeyTyped, MousePressed}
import scala.swing.{BorderPanel, Graphics2D, GridPanel, Label, Reactor}

class SwingPanel(controller: Controller) extends BorderPanel with Reactor {
  private val grid_size_factor = 60
  private val poolExecutor = new ScheduledThreadPoolExecutor(1)

  private val menuBar = new SwingMenuBar(controller)
  private val actionPanel = new SwingActionBarPanel(controller)
  private var backgroundImage = ImageUtils.loadImage(controller.openingBackgroundImagePath)

  private var labels = Array.ofDim[Label](controller.rowCount, controller.columnCount)
  private var gridPanel = new GridPanel(1, 1) {
    preferredSize = new Dimension(600, 650)

    override protected def paintComponent(g: Graphics2D): Unit = {
      super.paintComponent(g)
      if (backgroundImage.isDefined) {
        g.drawImage(backgroundImage.get, null, 0, 0)
      }
    }
  }

  // set focusable to allow process of keyEvents
  focusable = true

  // initial content when no level has been loaded yet
  add(menuBar, BorderPanel.Position.North)
  add(gridPanel, BorderPanel.Position.Center)

  listenTo(controller)
  listenTo(keys)
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
    case e: AttackResult =>
      SoundPlayer.playWav(e.attackSoundPath)
      updateLabelTemporary(e.rowIndex, e.columnIndex, e.attackImagePath, 1)
    case e: KeyTyped =>
      actionPanel.activateActionId(e.char)
  }

  def resize(width: Int, height: Int): Unit = {
    if(backgroundImage.isDefined) {
      backgroundImage = ImageUtils.scale(backgroundImage.get, width, height)
    }
    actionPanel.resize(width, height)
    repaint()
  }

  def showPlayerWon(e: PlayerWon): Unit = {
    _contents.clear()

    add(menuBar, BorderPanel.Position.North)
    _contents += new Label {
      private val imagePathOpt = ImageUtils.loadImageIcon(e.wonImagePath)
      if (imagePathOpt.isDefined) {
        icon = ImageUtils.loadImageIcon(e.wonImagePath).get
      } else {
        icon = null
      }
    }
    revalidate()
    repaint()
  }

  def rebuild(): Unit = {
    createContent()
    fillBoard()
  }

  private def createContent(): Unit = {
    // Clear previous content
    _contents.clear()
    backgroundImage = ImageUtils.loadImage(controller.levelBackgroundImagePath)
    labels = Array.ofDim[Label](controller.rowCount, controller.columnCount)
    gridPanel = new GridPanel(controller.rowCount, controller.columnCount) {
      preferredSize = new Dimension(controller.columnCount * grid_size_factor, controller.rowCount * grid_size_factor)

      override protected def paintComponent(g: Graphics2D): Unit = {
        super.paintComponent(g)
        if (backgroundImage.isDefined) {
          g.drawImage(backgroundImage.get, null, 0, 0)
        }
      }
    }

    add(menuBar, BorderPanel.Position.North)
    add(gridPanel, BorderPanel.Position.Center)
    add(actionPanel, BorderPanel.Position.South)

    revalidate()
    repaint()
  }

  private def fillBoard(): Unit = {
    for (i <- 0 until gridPanel.rows) {
      for (j <- 0 until gridPanel.columns) {
        labels(i)(j) = new Label {
          border = new javax.swing.border.LineBorder(java.awt.Color.BLACK, 1, true)
          listenTo(mouse.clicks)
          reactions += {
            case _: MousePressed =>
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

  private def updateLabelTemporary(rowIndex: Int, columnIndex: Int, pathOfImageToShow: String, seconds: Int): Unit = {
    val imageOpt = ImageUtils.loadImageIcon(pathOfImageToShow)
    if (imageOpt.isDefined) {
      // Temporary show set new icon, will be replaced with old one after configured delay
      labels(rowIndex)(columnIndex).icon = imageOpt.get
      val task = new Runnable {
        def run(): Unit = {
          // Restore old icon after configured delay
          val uiTask = new Runnable {
            def run(): Unit = updateLabel(rowIndex, columnIndex)
          }
          SwingUtilities.invokeLater(uiTask)
        }
      }
      poolExecutor.schedule(task, seconds, TimeUnit.SECONDS)
    }
  }

  private def updateLabel(rowIndex: Int, columnIndex: Int): Unit = {
    val label = labels(rowIndex)(columnIndex)
    val imagePath = controller.cellContentImagePath(rowIndex, columnIndex)
    if (imagePath.isDefined) {
      val imageOpt = ImageUtils.loadImageIcon(imagePath.get)
      if (imageOpt.isDefined) {
        label.icon = imageOpt.get
      } else {
        label.icon = null
      }
    } else {
      label.icon = null
    }
    label.repaint()
  }

  private def clearCellsInRange(): Unit = {
    for (i <- 0 until gridPanel.rows) {
      for (j <- 0 until gridPanel.columns) {
        labels(i)(j).border = new javax.swing.border.LineBorder(java.awt.Color.BLACK, 1, true)
      }
    }
  }
}
