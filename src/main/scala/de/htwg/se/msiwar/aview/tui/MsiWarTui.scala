package de.htwg.se.msiwar.aview.tui;

import de.htwg.se.msiwar.controller.MsiWarController

import scala.swing.Reactor;

class MsiWarTui(msiWarController: MsiWarController) extends Reactor {
    listenTo(msiWarController)
}
