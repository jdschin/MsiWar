package de.htwg.se.msiwar.aview.swing

import de.htwg.se.msiwar.controller.Controller

import scala.swing.event.Key
import scala.swing.{Action, Menu, MenuBar, MenuItem, Separator}

class SwingMenuBar(controller: Controller) extends MenuBar {

  contents += new Menu("Game") {
    mnemonic = Key.G

    contents += new MenuItem(Action("Random Level..."){
      // TODO implement level generator with actors
    })

    contents += new Separator()

    contents += new MenuItem(Action("Black Wood Battle (2P)"){
      // TODO load and setup config
    })

    contents += new MenuItem(Action("Grand Canyon (2P)"){
      // TODO load and setup config
    })

    contents += new MenuItem(Action("Showdown in the Alps (3P)"){
      // TODO load and setup config
    })

    contents += new MenuItem(Action("Desert War (3P)"){
      // TODO load and setup config
    })

    contents += new MenuItem(Action("Black Hawk Down (4P)"){
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
