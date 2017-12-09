package de.htwg.se.msiwar.model

case class GameModelImpl(numRows: Int, numCols: Int, gameObjects: List[GameObject]) extends GameModel {
  private val gameBoard = GameBoard(numRows, numCols, gameObjects)
  private var activePlayer = player(1)

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

  override def actionIdsForPlayer(playerNumber: Int): List[Int] = {
    player(playerNumber).skills.map(_.id)
  }

  override def actionHotKey(actionId: Int): String = {
    val foundAction = actions.find(_.id == actionId)
    if(foundAction.isDefined){
      // TODO create hotkey
      foundAction.get.description
    } else {
      ""
    }
  }

  override def actionDescription(actionId: Int): String = {
    val foundAction = actions.find(_.id == actionId)
    if(foundAction.isDefined){
      foundAction.get.description
    } else {
      ""
    }
  }

  override def actionIconPath(actionId: Int): String = {
    val foundAction = actions.find(_.id == actionId)
    if(foundAction.isDefined){
      foundAction.get.imagePath
    } else {
      ""
    }
  }

  override def gameObjectAt(rowIndex: Int, columnIndex: Int) : Option[GameObject] = {
    gameBoard.gameObjectAt(rowIndex, columnIndex)
  }

  override def rowCount = gameBoard.rows

  override def columnCount = gameBoard.columns
}
