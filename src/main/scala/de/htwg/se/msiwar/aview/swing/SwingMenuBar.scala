package de.htwg.se.msiwar.aview.swing

import de.htwg.se.msiwar.controller.Controller

import scala.swing.event.Key
import scala.swing.{Action, Menu, MenuBar, MenuItem, Separator}

class SwingMenuBar(controller: Controller) extends MenuBar {
  contents += new Menu("File") {
    mnemonic = Key.F
    contents += new MenuItem(Action("New Game") {
      controller.reset
    })

    contents += new MenuItem(Action("Quit") {
      System.exit(0)
    })
  }

  contents += new Menu("Options") {
    mnemonic = Key.O

    contents += new MenuItem(Action("Random Level"){
      // TODO implement level generator with actors
    })

    contents += new Separator()

    contents += new MenuItem(Action("Black Wood Battle"){
      // TODO load and setup config
    })

    contents += new MenuItem(Action("Desert War"){
      // TODO load and setup config
    })
  }

  contents += new Menu("Help") {
    mnemonic = Key.H

    contents += new MenuItem(Action("Controls..."){
      // TODO show hotkey window
    })

    contents += new MenuItem(Action("About..."){
      // TODO show about window
    })
  }
}
