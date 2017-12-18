package de.htwg.se.msiwar.model

case class GameBoard(rows: Int, columns: Int, gameObjects: List[GameObject]) {
  private val board = Array.ofDim[GameObject](rows, columns)

  gameObjects.foreach(placeGameObject(_))

  def placeGameObject(gameObject: GameObject) = {
    board(gameObject.position.x)(gameObject.position.y) = gameObject
  }

  def gameObjectAt(position: Position): Option[GameObject] = {
    gameObjectAt(position.y, position.x)
  }

  def isInBound(position: Position): Boolean = {
    (position.x >= 0 && position.y >= 0) &&
      (position.x < columns && position.y < rows)
  }

  def gameObjectAt(rowIndex: Int, columnIndex: Int): Option[GameObject] = {
    val objectAt = board(columnIndex)(rowIndex)
    Option(objectAt)
  }

  def moveGameObject(gameObject: GameObject, newPosition: Position) = {
    board(gameObject.position.x)(gameObject.position.y) = null
    gameObject.position.x = newPosition.x
    gameObject.position.y = newPosition.y
    placeGameObject(gameObject)
  }

  def collisionObject(from: Position, to: Position): Option[GameObject] = {
    // TODO: refactor
    if (!isInBound(to) || !isInBound(from)) {
      return Option.empty
    }
    if (from.x < to.x && from.y < to.y) {
      // RIGHT_UP
      for (x <- from.x + 1 until to.x + 1) {
        for (y <- from.y + 1 until to.y + 1) {
          val gameObject = gameObjectAt(Position(x, y))
          if (gameObject.isDefined) {
            return Option(gameObject.get)
          }
        }
      }
    } else if (from.x < to.x && from.y > to.y) {
      // RIGHT_DOWN
      for (x <- from.x + 1 until to.x + 1) {
        for (y <- from.y - 1 until to.y - 1 by -1) {
          val gameObject = gameObjectAt(Position(x, y))
          if (gameObject.isDefined) {
            return Option(gameObject.get)
          }
        }
      }
    } else if (from.x > to.x && from.y < to.y) {
      // LEFT_UP
      for (x <- from.x - 1 until to.x - 1 by -1) {
        for (y <- from.y + 1 until to.y + 1) {
          val gameObject = gameObjectAt(Position(x, y))
          if (gameObject.isDefined) {
            return Option(gameObject.get)
          }
        }
      }
    } else if (from.x > to.x && from.y > to.y) {
      // LEFT_DOWN
      for (x <- from.x - 1 until to.x - 1 by -1) {
        for (y <- from.y - 1 until to.y - 1 by -1) {
          val gameObject = gameObjectAt(Position(x, y))
          if (gameObject.isDefined) {
            return Option(gameObject.get)
          }
        }
      }
    } else if (from.x < to.x) {
      // RIGHT
      for (x <- from.x + 1 until to.x + 1) {
        val gameObject = gameObjectAt(Position(x, from.y))
        if (gameObject.isDefined) {
          return Option(gameObject.get)
        }
      }
    } else if (from.x > to.x) {
      // LEFT
      for (x <- from.x - 1 until to.x - 1 by -1) {
        val gameObject = gameObjectAt(Position(x, from.y))
        if (gameObject.isDefined) {
          return Option(gameObject.get)
        }
      }
    } else if (from.y < to.y) {
      // UP
      for (y <- from.y + 1 until to.y + 1) {
        val gameObject = gameObjectAt(Position(from.x, y))
        if (gameObject.isDefined) {
          return Option(gameObject.get)
        }
      }
    } else if (from.y > to.y) {
      // DOWN
      for (y <- from.y - 1 until to.y - 1 by -1) {
        val gameObject = gameObjectAt(Position(from.x, y))
        if (gameObject.isDefined) {
          return Option(gameObject.get)
        }
      }
    }
    Option.empty
  }
}
