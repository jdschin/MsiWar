package de.htwg.se.msiwar.model

import scala.collection.mutable.Buffer

case class GameBoard(rows: Int, columns: Int, gameObjects: List[GameObject]) {
  private val board = Array.ofDim[GameObject](rows, columns)

  gameObjects.foreach(placeGameObject(_))

  def placeGameObject(gameObject: GameObject) = {
    board(gameObject.position.x)(gameObject.position.y) = gameObject
  }

  def gameObjectAt(position: Position): Option[GameObject] = {
    gameObjectAt(position.x, position.y)
  }

  def isInBound(position: Position): Boolean = {
    (position.x >= 0 && position.y >= 0) &&
      (position.x < columns && position.y < rows)
  }

  def gameObjectAt(x: Int, y: Int): Option[GameObject] = {
    val objectAt = board(x)(y)
    Option(objectAt)
  }

  def moveGameObject(gameObject: GameObject, newPosition: Position) = {
    removeGameObject(gameObject)
    gameObject.position.x = newPosition.x
    gameObject.position.y = newPosition.y
    placeGameObject(gameObject)
  }

  def removeGameObject(gameObject: GameObject) = {
    board(gameObject.position.x)(gameObject.position.y) = null
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

  def cellsInRange(position: Position, action: Action): List[(Int, Int)] = {
    var cellsInRangeList = Buffer[(Int, Int)]()
    val range = action.range

    action.actionType match {
      case a: ActionType.WAIT.type => cellsInRangeList += ((position.x, position.y))
      case _ => {
        loopForwards(position.x, position.x + range + 1, x => {
          val pos = Position(x, position.y)
          if (isInBound(pos) && pos != position && gameObjectAt(pos).isEmpty) {
            cellsInRangeList += ((pos.x, pos.y))
          }
        })
        loopForwards(position.y, position.y + range + 1, y => {
          val pos = Position(position.x, y)
          if (isInBound(pos) && pos != position && gameObjectAt(pos).isEmpty) {
            cellsInRangeList += ((pos.x, pos.y))
          }
        })
        loopBackwards(position.y, position.y - range - 1, y => {
          val pos = Position(position.x, y)
          if (isInBound(pos) && pos != position && gameObjectAt(pos).isEmpty) {
            cellsInRangeList += ((pos.x, pos.y))
          }
        })
        loopBackwards(position.x, position.x - range - 1, x => {
          val pos = Position(x, position.y)
          if (isInBound(pos) && pos != position && gameObjectAt(pos).isEmpty) {
            cellsInRangeList += ((pos.x, pos.y))
          }
        })
        loopForwards(position.x, position.x + range + 1, x => {
          loopBackwards(position.y, position.y - range - 1, y => {
            val pos = Position(x, y)
            if (isInBound(pos) && pos != position && gameObjectAt(pos).isEmpty) {
              cellsInRangeList += ((pos.x, pos.y))
            }
          })
        })
        loopForwards(position.y, position.y + range + 1, y => {
          loopBackwards(position.x, position.x - range - 1, x => {
            val pos = Position(x, y)
            if (isInBound(pos) && pos != position && gameObjectAt(pos).isEmpty) {
              cellsInRangeList += ((pos.x, pos.y))
            }
          })
        })
        loopForwards(position.x, position.x + range + 1, x => {
          loopForwards(position.y, position.y + range + 1, y => {
            val pos = Position(x, y)
            if (isInBound(pos) && pos != position && gameObjectAt(pos).isEmpty) {
              cellsInRangeList += ((pos.x, pos.y))
            }
          })
        })
        loopBackwards(position.x, position.x - range - 1, x => {
          loopBackwards(position.y, position.y - range - 1, y => {
            val pos = Position(x, y)
            if (isInBound(pos) && pos != position && gameObjectAt(pos).isEmpty) {
              cellsInRangeList += ((pos.x, pos.y))
            }
          })
        })
      }
    }
    cellsInRangeList.toList
  }

  private def loopBackwards(high: Int, low: Int, f: Int => Unit): Unit = {
    for (x <- high until low by -1) {
      f(x)
    }
  }

  private def loopForwards(low: Int, high: Int, f: Int => Unit): Unit = {
    for (x <- low until high) {
      f(x)
    }
  }
}
