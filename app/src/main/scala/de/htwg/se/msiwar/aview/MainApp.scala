package de.htwg.se.msiwar.aview

import de.htwg.se.msiwar.aview.swing.SwingFrame
import de.htwg.se.msiwar.aview.tui.Tui
import de.htwg.se.msiwar.controller.ControllerImpl

object MainApp {
  val controller = new ControllerImpl
  private val swingFrame = new SwingFrame(controller)
  swingFrame.visible = true

  private val tui = new Tui(controller)

  def main(args: Array[String]): Unit = {
    while (tui.executeCommand(readLine())) {}
  }
}
