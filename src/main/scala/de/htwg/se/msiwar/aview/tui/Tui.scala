package de.htwg.se.msiwar.aview.tui

import de.htwg.se.msiwar.controller.{CellChanged, Controller, TurnEnded, TurnStarted}
import de.htwg.se.msiwar.util.Direction._

import scala.swing.Reactor

class Tui(controller: Controller) extends Reactor {
  listenTo(controller)
  reactions += {
    case e: CellChanged => printBoard
    case e: TurnStarted => println("Player" + e.playerNumber + " turn " + controller.turnCounter + " started\n")
    case e: TurnEnded => println("Player" + e.playerNumber + " turn " + controller.turnCounter + " ended\n")
  }

  printWelcomeMessage
  printHelp
  controller.reset
  printBoard

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
      case executeActionRe(actionId:String, direction:String) => executeAction(actionId.toInt, direction)
      case _ => println("Unknown Command")
    }
    continue
  }

  def executeAction(actionId:Int, direction:String) ={
    val convertedDirection = convertToDirection(direction)
    if(convertedDirection.isDefined){
      println("Executing action " + actionId + " '" + controller.actionDescription(actionId) + "' in direction '" + convertedDirection.get + "'")
      controller.executeAction(actionId,convertedDirection.get)
    }
  }

  def convertToDirection(direction:String): Option[Direction] ={
    var dirOption:Option[Direction] = None
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
