package de.htwg.se.msiwar.model

import scala.util.control.Breaks

case class GameModelImpl(numRows: Int, numCols: Int, gameObjects: List[GameObject]) extends GameModel {
  private val gameBoard = GameBoard(numRows, numCols, gameObjects)
  private var activePlayer = player(1)
  private var turnNumber = 0

  override def reset: Unit = {
    gameBoard.reset
  }

  private def player(playerNumber: Int): PlayerObject = {
    val foundPlayer = gameObjects.collect({ case s: PlayerObject => s }).find(_.playerNumber == playerNumber)
    foundPlayer.get
  }

  private def actions: List[Action] = {
    val players = gameObjects.collect({ case s: PlayerObject => s })
    players.flatMap(_.skills)
  }

  override def activePlayerNumber: Int = {
    activePlayer.playerNumber
  }

  override def activePlayerName: String = {
    activePlayer.name
  }

  override def actionIdsForPlayer(playerNumber: Int): List[Int] = {
    player(playerNumber).skills.map(_.id)
  }

  override def actionHotKey(actionId: Int): String = {
    val foundAction = actions.find(_.id == actionId)
    if (foundAction.isDefined) {
      // TODO create hotkey
      foundAction.get.description
    } else {
      ""
    }
  }

  override def actionDescription(actionId: Int): String = {
    val foundAction = actions.find(_.id == actionId)
    if (foundAction.isDefined) {
      foundAction.get.description
    } else {
      ""
    }
  }

  override def actionIconPath(actionId: Int): String = {
    val foundAction = actions.find(_.id == actionId)
    if (foundAction.isDefined) {
      foundAction.get.imagePath
    } else {
      ""
    }
  }

  override def turnCounter: Int = turnNumber

  override def nextTurn: Int = {
    var foundNextPlayer = false
    // TODO: maybe use Option
    if (activePlayer == null) {
      activePlayer = player(1)
      turnNumber += 1
    } else {
      Breaks.breakable(
        for (playerObject <- gameObjects.collect({ case s: PlayerObject => s })) {
          if (playerObject.playerNumber > activePlayerNumber) {
            activePlayer = playerObject
            foundNextPlayer = true
            Breaks.break()
          }
        }
      )

      // If every player did his turn, start the next turn with player 1
      if (!foundNextPlayer) {
        activePlayer = player(1)
        turnNumber += 1
      }
    }
    turnCounter
  }

  override def turnOver = {
    !activePlayer.hasActionPointsLeft
  }

  override def gameObjectAt(rowIndex: Int, columnIndex: Int): Option[GameObject] = {
    gameBoard.gameObjectAt(rowIndex, columnIndex)
  }

  override def rowCount = gameBoard.rows

  override def columnCount = gameBoard.columns

}