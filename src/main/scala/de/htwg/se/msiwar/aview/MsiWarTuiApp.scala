package de.htwg.se.msiwar.aview

import de.htwg.se.msiwar.aview.tui.MsiWarTui
import de.htwg.se.msiwar.controller.MsiWarControllerImpl
import de.htwg.se.msiwar.model.MsiWarModelImpl

object MsiWarTuiApp {
  val model = new MsiWarModelImpl
  val controller = new MsiWarControllerImpl(model)
  val tui = new MsiWarTui(controller)

  def main(args: Array[String]) {
    while (tui.executeCommand(readLine())) {}
  }
}