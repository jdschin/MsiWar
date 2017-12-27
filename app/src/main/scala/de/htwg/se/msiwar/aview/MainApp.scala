package de.htwg.se.msiwar.aview

import de.htwg.se.msiwar.aview.swing.SwingFrame
import de.htwg.se.msiwar.aview.tui.Tui
import de.htwg.se.msiwar.controller.ControllerImpl

object MainApp {
  def main(args: Array[String]): Unit = {
    val controller = new ControllerImpl

    new SwingFrame(controller).visible = true
    val tui = new Tui(controller)

    while (tui.executeCommand(readLine())) {}
  }
}
