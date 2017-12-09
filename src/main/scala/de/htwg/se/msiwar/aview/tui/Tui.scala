package de.htwg.se.msiwar.aview.tui

import de.htwg.se.msiwar.controller.{CellChanged, Controller, TurnEnded, TurnStarted}

import scala.swing.Reactor;

class Tui(controller: Controller) extends Reactor {
  listenTo(controller)
  reactions += {
    case e: CellChanged => printBoard
    case e: TurnStarted => println("Player" + e.playerNumber + "'s turn")
    case e: TurnEnded => println("Player" + e.playerNumber + "'s turn ended")
  }

  def printWelcomeMessage = {
    println("___  ___ _____ _____   _    _  ___  ______ \n|  \\/  |/  ___|_   _| | |  | |/ _ \\ | ___ \\\n| .  . |\\ `--.  | |   | |  | / /_\\ \\| |_/ /\n| |\\/| | `--. \\ | |   | |/\\| |  _  ||    / \n| |  | |/\\__/ /_| |_  \\  /\\  / | | || |\\ \\ \n\\_|  |_/\\____/ \\___/   \\/  \\/\\_| |_/\\_| \\_|\n")
    println("           -Prepare to die!-           ")
  }

  def printUserActions = {
    println("Available Actions: ")
    // TODO print actions for each player
    controller.actionIds(1).foreach(i => println("Action: " + controller.actionDescription(i)))
  }

  def printHelp = {
    println("Help:")
    println("q => Quit the game")
    println("b => Print the current board")
    println("h => Show help")
    println("a => Print available user actions")
    println("t => Print the active player")
    println
  }

  def printActivePlayer = {
    val playerNumber = controller.activePlayerNumber
    val playerName = controller.playerName(playerNumber)
    println("Spieler" + playerNumber + " '" + playerName + "' ist am Zug")

  }

  def printBoard = {
    for (i <- 0 until controller.rowCount) {
      print("| ")
      for (j <- 0 until controller.columnCount) {
        print(controller.cellContentToText(i, j) + " | ")
      }
      println
    }
  }

  def executeCommand(input: String) = {
    var continue = true
    input match {
      case "q" | "Q" => continue = false
      case "h" | "H" => printHelp
      case "b" | "b" => printBoard
      case "a" | "A" => printUserActions
      case "t" | "T" => printActivePlayer
      case _ => println("Unbekanntes Command")
    }
    continue
  }

  printWelcomeMessage
  printHelp
}
