package de.htwg.se.msiwar.model

case class GameBoard(rows: Int, columns: Int, gameObjects: List[GameObject]) {
  private val board = Array.ofDim[GameObject](rows,columns)

  gameObjects.foreach(placeGameObject(_))

  def placeGameObject(gameObject: GameObject) = {
    board(gameObject.position.x)(gameObject.position.y) = gameObject
  }

  def gameObjectAt(position: Position) : Option[GameObject] = {
    gameObjectAt(position.x,position.y)
  }

  def gameObjectAt(rowIndex: Int, columnIndex: Int) : Option[GameObject] = {
    val objectAt = board(rowIndex)(columnIndex)
    Option(objectAt)
  }

  def moveGameObject(gameObject: GameObject, newPosition: Position) = {
    board(gameObject.position.x)(gameObject.position.y) = null
    board(newPosition.x)(newPosition.y) = gameObject
  }
}
