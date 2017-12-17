package de.htwg.se.msiwar.aview.tui

import de.htwg.se.msiwar.controller.{CellChanged, Controller, TurnEnded, TurnStarted}

import scala.swing.Reactor;

class Tui(controller: Controller) extends Reactor {
  listenTo(controller)
  reactions += {
    case e: CellChanged => printBoard
    case e: TurnStarted => println("Player" + e.playerNumber + " turn " + controller.turnCounter + " started")
    case e: TurnEnded => println("Player" + e.playerNumber + " turn " + controller.turnCounter + " ended")
  }

  printWelcomeMessage
  printHelp
  controller.reset

  def printWelcomeMessage = {
    println("___  ___ _____ _____   _    _  ___  ______ \n|  \\/  |/  ___|_   _| | |  | |/ _ \\ | ___ \\\n| .  . |\\ `--.  | |   | |  | / /_\\ \\| |_/ /\n| |\\/| | `--. \\ | |   | |/\\| |  _  ||    / \n| |  | |/\\__/ /_| |_  \\  /\\  / | | || |\\ \\ \n\\_|  |_/\\____/ \\___/   \\/  \\/\\_| |_/\\_| \\_|\n")
    println("           -Prepare to die!-           ")
  }

  def printUserActions = {
    println("Available Actions: ")
    controller.actionIds(1).foreach(i => println("Action: id=" + i + ", desc=" + controller.actionDescription(i)))
  }

  def printHelp = {
    println("Help:")
    println("s | S => Start a new game")
    println("q | Q => Quit the game")
    println("b | B => Print the current board")
    println("h | H => Show help")
    println("a | A => Print available user actions")
    println("t | T => Print the active player")
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
    val executeActionRe = "(\\d+)(lu|ld|ru|rd|l|r|u|d)".r
    input match {
      case "s" | "s" => controller.reset
      case "q" | "Q" => continue = false
      case "h" | "H" => printHelp
      case "b" | "b" => printBoard
      case "a" | "A" => printUserActions
      case "t" | "T" => printActivePlayer
      case executeActionRe(actionId, d) => println(actionId, d)
      case _ => println("Unbekanntes Command")
    }
    continue
  }
}
