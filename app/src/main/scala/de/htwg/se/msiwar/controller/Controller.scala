package de.htwg.se.msiwar.controller

import de.htwg.se.msiwar.util.Direction.Direction

import scala.swing.Publisher
import scala.swing.event.Event

case class GameStarted() extends Event
case class CellChanged(rowColumnIndexes: List[(Int, Int)]) extends Event
case class CellsInRange(rowColumnIndexes: List[(Int, Int)]) extends Event
case class TurnStarted(playerNumber: Int) extends Event
case class PlayerWon(playerNumber: Int, wonImagePath: String) extends Event
case class PlayerStatsChanged(playerNumber: Int, newActionPoints: Int) extends Event
case class AttackResult(rowIndex: Int, columnIndex: Int, hit: Boolean, attackImagePath: String, attackSoundPath: String) extends Event

trait Controller extends Publisher{

  /**
    * @param rowIndex row to get text for
    * @param columnIndex column to get text for
    * @return the text representation of the cell
    */
  def cellContentToText(rowIndex: Int, columnIndex: Int) : String

  /**
    * @param rowIndex row to get path of the image representation
    * @param columnIndex column to get path of the image representation
    * @return the path of the image representation of the cell
    */
  def cellContentImagePath(rowIndex: Int, columnIndex: Int) : Option[String]

  /**
    * Calculates the cells in range for active player and given actionId
    * when no action id is enabled, result is always empty
    * Result is published by CellsInRange Event
    * @param actionId the id of the action to calculate cells in range for
    **/
  def cellsInRange(actionId: Option[Int]) : Unit

  /**
    * Executes the given action id in the given direction
    * NOTE: always call Controller#canExecuteAction(Int, Direction) before
    * @param actionId the id of the action to execute
    * @param direction the direction of the action to execute
    */
  def executeAction(actionId: Int, direction:Direction) : Unit

  /**
    * Executes the given action id in the given direction
    * NOTE: always call Controller#canExecuteAction(Int, Int, Int) before
    * @param actionId the id of the action to execute
    * @param rowIndex the target row
    * @param columnIndex the target column
    */
  def executeAction(actionId: Int, rowIndex: Int, columnIndex: Int) : Unit

  /**
    * Verifies if the action for given id in given direction can be executed or will result in an error
    * @param actionId the id of the action to check
    * @param direction the direction of the action to check
    * @return true when the action can be executed otherwise false
    */
  def canExecuteAction(actionId: Int, direction:Direction) : Boolean

  /**
    * Verifies if the action for given id in given direction can be executed or will result in an error
    * @param actionId the id of the action to check
    * @param rowIndex the target row of the action to check
    * @param columnIndex the target column of the action to check
    * @return true when the action can be executed otherwise false
    */
  def canExecuteAction(actionId: Int, rowIndex: Int, columnIndex: Int) : Boolean

  /**
    * @param playerNumber the number of the player to get action ids for
    * @return the actions ids for given player id
    */
  def actionIds(playerNumber: Int) : Set[Int]

  /**
    * @param actionId the action id to get action point cost for
    * @return the action point cost for given action id
    */
  def actionPointCost(actionId: Int) : Int

  /**
    * @param actionId the action id to get description for
    * @return the description for given action id
    */
  def actionDescription(actionId: Int) : String

  /**
    * @param actionId the action id to get icon path for
    * @return the icon path for given action id
    */
  def actionIconPath(actionId: Int) : Option[String]

  /**
    * @param actionId the action id to get damage value for
    * @return the damage value for given action id
    */
  def actionDamage(actionId: Int) : Int

  /**
    * @param actionId the action id to get range value for
    * @return the range value for the given action id
    */
  def actionRange(actionId: Int) : Int

  /**
    * @return the list of the available scenarios inside the scenarios folder (resources/scenarios)
    */
  def scenarioIds : Set[Int]

  /**
    * @param scenarioId the id to get scenario name for
    * @return the found scenario name if available
    */
  def scenarioName(scenarioId: Int) : Option[String]

  /**
    * @return the column count of the game board
    */
  def columnCount : Int

  /**
    * @return the row count of the game board
    */
  def rowCount : Int

  /**
    * @return the image path for the opening background
    */
  def openingBackgroundImagePath : String

  /**
    * @return the image path for the level background
    */
  def levelBackgroundImagePath : String

  /**
    * @return the image path of the actionbar background
    */
  def actionbarBackgroundImagePath : String

  /**
    * @return the image path of the application icon
    */
  def appIconImagePath : String

  /**
    * @return the number (=id) of the active player
    */
  def activePlayerNumber : Int

  /**
    * @return the current action points of the active player
    */
  def activePlayerActionPoints : Int

  /**
    * @return the health points of the active player
    */
  def activePlayerHealthPoints : Int

  /**
    * @return the player name for active player
    */
  def activePlayerName: String

  /**
    * @return the current turn counter value
    */
  def turnCounter : Int

  /**
    * Starts a game configured by given scenario id
    * @param scenarioId the scenario id to configure game from
    */
  def startGame(scenarioId: Int) : Unit
}
