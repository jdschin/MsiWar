package de.htwg.se.msiwar.aview.tui

import de.htwg.se.msiwar.controller.{CellChanged, Controller, PlayerWon, TurnStarted}
import de.htwg.se.msiwar.util.Direction._

import scala.swing.Reactor

class Tui(controller: Controller) extends Reactor {
  listenTo(controller)
  reactions += {
    case _: CellChanged => printBoard()
    case e: TurnStarted =>
      println("Player" + e.playerNumber + " turn " + controller.turnCounter + " started\n")
      printBoard()
    case e: PlayerWon => println(Console.GREEN + "Player" + e.playerNumber + " wins!\n" + Console.WHITE)
  }

  printWelcomeMessage()
  printHelp()

  def printWelcomeMessage(): Unit = {
    println(" _____ _          _   _______          _     __          __        \n|  __ (_)        | | |__   __|        | |    \\ \\        / /        \n| |__) |__  _____| |    | | __ _ _ __ | | __  \\ \\  /\\  / /_ _ _ __ \n|  ___/ \\ \\/ / _ \\ |    | |/ _` | '_ \\| |/ /   \\ \\/  \\/ / _` | '__|\n| |   | |>  <  __/ |    | | (_| | | | |   <     \\  /\\  / (_| | |   \n|_|   |_/_/\\_\\___|_|    |_|\\__,_|_| |_|_|\\_\\     \\/  \\/ \\__,_|_|\n\n\n")
  }

  def printUserActions(): Unit = {
    println("Available Actions: ")
    controller.actionIds(1).foreach(i => println("Action: id=" + i + ", desc=" + controller.actionDescription(i)))
  }

  def printHelp(): Unit = {
    println("Help:")
    println("s | S => Start a new game")
    println("q | Q => Quit the game")
    println("b | B => Print the current board")
    println("h | H => Show help")
    println("a | A => Print available user actions")
    println("t | T => Print the active player")
    println
  }

  def printActivePlayer(): Unit = {
    val playerNumber = controller.activePlayerNumber
    val playerName = controller.playerName(playerNumber)
    println("Player" + playerNumber + " '" + playerName + "' is at the turn")

  }

  def printBoard(): Unit = {
    println
    for (i <- 0 until controller.rowCount) {
      print("| ")
      for (j <- 0 until controller.columnCount) {
        print(controller.cellContentToText(i, j) + " | ")
      }
      println
    }
  }

  def executeCommand(input: String): Boolean = {
    var continue = true
    val executeActionRe = "(\\d+)(lu|ld|ru|rd|l|r|u|d)".r
    input match {
      case "s" | "s" => controller.reset()
      case "q" | "Q" => continue = false
      case "h" | "H" => printHelp()
      case "b" | "b" => printBoard()
      case "a" | "A" => printUserActions()
      case "t" | "T" => printActivePlayer()
      case executeActionRe(actionId: String, direction: String) => executeAction(actionId.toInt, direction)
      case _ => println("Unknown Command")
    }
    continue
  }

  def executeAction(actionId: Int, direction: String): Unit = {
    val convertedDirection = convertToDirection(direction)
    if (convertedDirection.isDefined) {
      if (controller.canExecuteAction(actionId, convertedDirection.get)) {
        println("Executing action " + actionId + " '" + controller.actionDescription(actionId) + "' in direction '" + convertedDirection.get + "'")
        controller.executeAction(actionId, convertedDirection.get)
      } else {
        println("Action can not be executed")
      }
    }
  }

  def convertToDirection(direction: String): Option[Direction] = {
    var dirOption: Option[Direction] = None
    direction match {
      case "lu" => dirOption = Option(LEFT_UP)
      case "ld" => dirOption = Option(LEFT_DOWN)
      case "ru" => dirOption = Option(RIGHT_UP)
      case "rd" => dirOption = Option(RIGHT_DOWN)
      case "l" => dirOption = Option(LEFT)
      case "r" => dirOption = Option(RIGHT)
      case "u" => dirOption = Option(UP)
      case "d" => dirOption = Option(DOWN)
      case _ => println("Unknown direction")
    }
    dirOption
  }
}
