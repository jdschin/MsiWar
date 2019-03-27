package de.htwg.se.msiwar.aview

import de.htwg.se.msiwar.aview.swing.SwingFrame
import de.htwg.se.msiwar.aview.tui.Tui
import de.htwg.se.msiwar.controller.ControllerImpl
import de.htwg.se.msiwar.model.GameModelImpl
import de.htwg.se.msiwar.util.GameConfigProviderImpl

object MainApp {
  val createdModel = GameModelImpl(new GameConfigProviderImpl)
  val controller = ControllerImpl(createdModel)
  val swingFrame = new SwingFrame(controller)
  swingFrame.visible = true

  val tui = new Tui(controller)

  def main(args: Array[String]): Unit = {
    while (tui.executeCommand(readLine())) {}
  }
}
