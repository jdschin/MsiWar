package de.htwg.se.msiwar.aview

import de.htwg.se.msiwar.aview.tui.Tui
import de.htwg.se.msiwar.controller.Controller

object TuiApp {

  def main(args: Array[String], controller: Controller) {
    val tui = new Tui(controller)
    while (tui.executeCommand(readLine())) {}
  }
}