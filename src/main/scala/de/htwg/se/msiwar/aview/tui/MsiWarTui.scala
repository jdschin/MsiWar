package de.htwg.se.msiwar.aview.tui

import de.htwg.se.msiwar.controller.{CellChanged, MsiWarController}

import scala.swing.Reactor;

class MsiWarTui(controller: MsiWarController) extends Reactor {
  listenTo(controller)
  reactions += {
    case e: CellChanged => printBoard
  }

  def printWelcomeMessage = {
    println("___  ___ _____ _____   _    _  ___  ______ \n|  \\/  |/  ___|_   _| | |  | |/ _ \\ | ___ \\\n| .  . |\\ `--.  | |   | |  | / /_\\ \\| |_/ /\n| |\\/| | `--. \\ | |   | |/\\| |  _  ||    / \n| |  | |/\\__/ /_| |_  \\  /\\  / | | || |\\ \\ \n\\_|  |_/\\____/ \\___/   \\/  \\/\\_| |_/\\_| \\_|\n")
    println("           -Prepare to die!-           ")
  }

  def printUserActions = {
    println("Available Actions: ")
    controller.getActionIds.foreach(i => println("\nAction: " + controller.getActionDescription(i)))
  }

  def printHelp = {
    println("Help:")
    println("q => Quit the game")
    println("b => Print the current board")
    println("h => Show help")
    println("a => Print available user actions")
    println
  }

  def printBoard = {
    for (i <- 0 until controller.getRowCount; j <- 0 until controller.getColumnCount) {
      println(controller.getCellContentToText(i, j))
    }
  }

  def executeCommand(input: String) = {
    var continue = true
    input match {
      case "q" => continue = false
      case "h" => printHelp
      case "b" => printBoard
      case "a" => printUserActions
    }
    continue
  }

  printWelcomeMessage
  printHelp
}
