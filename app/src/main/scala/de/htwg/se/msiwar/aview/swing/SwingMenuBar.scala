package de.htwg.se.msiwar.aview.swing

import de.htwg.se.msiwar.controller.Controller

import scala.swing.event.Key
import scala.swing.{Action, Menu, MenuBar, MenuItem, Separator}

class SwingMenuBar(controller: Controller) extends MenuBar {

  contents += new Menu("Game") {
    mnemonic = Key.G

    contents += new MenuItem(Action("Random Level...") {
      // TODO implement level generator with actors
    })

    contents += new Separator()

    contents += new MenuItem(Action("Black Wood Battle (2P)") {
      controller.startGame("src/main/resources/scenarios/2_black_wood_battle.json")
    })

    contents += new MenuItem(Action("Grand Canyon (2P)") {
      controller.startGame("src/main/resources/scenarios/2_grand_canyon.json")
    })

    contents += new MenuItem(Action("Showdown in the Alps (3P)") {
      controller.startGame("src/main/resources/scenarios/3_showdown_in_the_alps.json")
    })

    contents += new MenuItem(Action("Desert War (3P)") {
      controller.startGame("src/main/resources/scenarios/3_desert_war.json")
    })

    contents += new MenuItem(Action("Black Hawk Down (4P)") {
      controller.startGame("src/main/resources/scenarios/4_black_hawk_down.json")
    })
  }

  contents += new Menu("Help") {
    mnemonic = Key.H

    contents += new MenuItem(Action("Controls...") {
      // TODO show hotkey window
    })

    contents += new MenuItem(Action("About...") {
      // TODO show about window
    })
  }
}
