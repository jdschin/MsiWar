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
    case _: GameStarted => println(Console.GREEN + "\nNew Game started!\n" + Console.WHITE)
    case e: AttackResult =>
      if (e.hit) {
        println(Console.GREEN + "\nAttack hits\n" + Console.WHITE)
      } else {
        println(Console.GREEN + "\nAttack misses\n" + Console.WHITE)
      }
  }

  printWelcomeMessage()
  printHelp()

  def executeCommand(input: String): Boolean = {
    var continue = true
    val scenarioRe = "[n|N]{1}(\\d){1}".r
    val executeActionRe = "(\\d+)(lu|ld|ru|rd|l|r|u|d)".r
    input match {
      case scenarioRe(scenarioId: String) => controller.startGame(scenarioId.toInt)
      // TODO start random game
      case "n" | "N" => controller.startGame(1)
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

  private def printUserActions(): Unit = {
    println("Available Actions: ")
    controller.actionIds(1).foreach(i => println("Action: id=" + i + ", desc=" + controller.actionDescription(i)))
  }

  private def printHelp(): Unit = {
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

  private def printActivePlayer(): Unit = {
    val playerNumber = controller.activePlayerNumber
    val playerName = controller.activePlayerName
    println("Player" + playerNumber + " '" + playerName + "' is at the turn")
  }

  private def printScenarioList(): Unit = {
    println("Available scenarios: ")
    controller.scenarioIds.foreach(s => printScenario(s))
  }

  private def printScenario(scenarioId: Int): Unit = {
    val sb = StringBuilder.newBuilder
    sb.append("Scenario: id=")
    sb.append(scenarioId)
    sb.append(", name=")
    if (controller.scenarioName(scenarioId).isDefined) {
      sb.append(controller.scenarioName(scenarioId).get)
    } else {
      sb.append("not available")
    }
    println(sb.toString())
  }

  private def printBoard(): Unit = {
    for (i <- 0 until controller.rowCount) {
      print("| ")
      for (j <- 0 until controller.columnCount) {
        print(controller.cellContentToText(i, j) + " | ")
      }
      println
    }
    println
  }

  private def executeAction(actionId: Int, direction: String): Unit = {
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

  private def convertToDirection(direction: String): Option[Direction] = {
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

  private def printWelcomeMessage(): Unit = {
    println(" _____ _          _   _______          _     __          __        \n|  __ (_)        | | |__   __|        | |    \\ \\        / /        \n| |__) |__  _____| |    | | __ _ _ __ | | __  \\ \\  /\\  / /_ _ _ __ \n|  ___/ \\ \\/ / _ \\ |    | |/ _` | '_ \\| |/ /   \\ \\/  \\/ / _` | '__|\n| |   | |>  <  __/ |    | | (_| | | | |   <     \\  /\\  / (_| | |   \n|_|   |_/_/\\_\\___|_|    |_|\\__,_|_| |_|_|\\_\\     \\/  \\/ \\__,_|_|\n\n\n")
  }
}
