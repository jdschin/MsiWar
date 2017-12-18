package de.htwg.se.msiwar.model

case class GameBoard(rows: Int, columns: Int, gameObjects: List[GameObject]) {
  private val board = Array.ofDim[GameObject](rows,columns)

  gameObjects.foreach(placeGameObject(_))

  def placeGameObject(gameObject: GameObject) = {
    board(gameObject.position.x)(gameObject.position.y) = gameObject
  }

  def gameObjectAt(position: Position) : Option[GameObject] = {
    gameObjectAt(position.x, position.y)
  }

  def isInBound(position: Position) : Boolean = {
    (position.x >= 0 && position.y >= 0) &&
    (position.x < columns && position.y < rows)
  }

  def gameObjectAt(rowIndex: Int, columnIndex: Int) : Option[GameObject] = {
    val objectAt = board(columnIndex)(rowIndex)
    Option(objectAt)
  }

  def moveGameObject(gameObject: GameObject, newPosition: Position) = {
    board(gameObject.position.x)(gameObject.position.y) = null
    gameObject.position.x = newPosition.x
    gameObject.position.y = newPosition.y
    placeGameObject(gameObject)
  }
}
