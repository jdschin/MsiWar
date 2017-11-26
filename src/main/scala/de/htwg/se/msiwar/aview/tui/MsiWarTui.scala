package de.htwg.se.msiwar.aview.tui

import de.htwg.se.msiwar.controller.{CellChanged, MsiWarController}

import scala.swing.Reactor;

class MsiWarTui(msiWarController: MsiWarController) extends Reactor {
  listenTo(msiWarController)
  reactions += {
    case e: CellChanged => printBoard
  }

  def printWelcomeMessage: Unit = {
    println("___  ___ _____ _____   _    _  ___  ______ \n|  \\/  |/  ___|_   _| | |  | |/ _ \\ | ___ \\\n| .  . |\\ `--.  | |   | |  | / /_\\ \\| |_/ /\n| |\\/| | `--. \\ | |   | |/\\| |  _  ||    / \n| |  | |/\\__/ /_| |_  \\  /\\  / | | || |\\ \\ \n\\_|  |_/\\____/ \\___/   \\/  \\/\\_| |_/\\_| \\_|\n")
    println("           -Prepare to die!-           ")
  }

  def printAvailableActions: Unit = {
    println("\nAvailable Actions: ")
    msiWarController.getActionIds.foreach(i => println("\nAction:" + msiWarController.getActionDescription(i)))
  }

  def printBoard: Unit = {
    for (i <- 0 until msiWarController.getRowCount; j <- 0 until msiWarController.getColumnCount) {
      println(msiWarController.getCellContentToText(i, j))
    }
  }

  def executeCommand(input: String): Boolean = {
    println(input)
    if (input == "q" || input == "Q") {
      false
    } else {
      true
    }
  }

  printWelcomeMessage
  printAvailableActions
}
