package de.htwg.se.msiwar.aview.tui

import de.htwg.se.msiwar.controller._
import de.htwg.se.msiwar.util.Direction._

import scala.swing.Reactor

class Tui(controller: Controller) extends Reactor {
  listenTo(controller)
  reactions += {
    case _: CellChanged => printBoard()
    case e: TurnStarted =>
      println(Console.GREEN + "\nPlayer" + e.playerNumber + " turn " + controller.turnCounter + " started\n" + Console.WHITE)
      printBoard()
    case e: PlayerWon => println(Console.GREEN + "\nPlayer" + e.playerNumber + " wins!\n" + Console.WHITE)
    case _: GameStarted =>  println(Console.GREEN + "\nNew Game started!\n" + Console.WHITE)
    case e: AttackActionResult =>
      if(e.hit) {
        println(Console.GREEN + "\nAttack hits\n" + Console.WHITE)
      } else {
        println(Console.GREEN + "\nAttack misses\n" + Console.WHITE)
      }
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
    println("n  |  N => Start a new game (random map)")
    println("nX | NX => Start a scenario (X = scenario Number)")
    println("s  |  S => Print scenario list")
    println("q  |  Q => Quit the game")
    println("b  |  B => Print the current board")
    println("h  |  H => Show help")
    println("a  |  A => Print available user actions (active player)")
    println("t  |  T => Print the active player")
    println
  }

  def printActivePlayer(): Unit = {
    val playerNumber = controller.activePlayerNumber
    val playerName = controller.playerName(playerNumber)
    println("Player" + playerNumber + " '" + playerName + "' is at the turn")

  }

  def printScenarioList(): Unit = {
    println("1 - Black wood battle (2 Players)")
    println("2 - Grand canyon (2 Players)")
    println("3 - Desert war (3 Players)")
    println("4 - Showdown in the alps (4 Players)")
    println("5 - Black hawk down (4 Players)")
  }

  def printBoard(): Unit = {
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
    val scenarioRe = "[s|S]{1}\\d{1}".r
    val executeActionRe = "(\\d+)(lu|ld|ru|rd|l|r|u|d)".r
    input match {
        // TODO allow scenario selection
      case "n" | "N" => controller.startGame("src/main/resources/scenarios/2_black_wood_battle.json")
      case scenarioRe(scenarioId: String) => println("playing " + scenarioId)
      case "s" | "S" => printScenarioList()
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
