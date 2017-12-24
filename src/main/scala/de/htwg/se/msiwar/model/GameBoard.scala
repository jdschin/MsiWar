package de.htwg.se.msiwar.model

import de.htwg.se.msiwar.model.ActionType.ActionType

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

  private def addPosToListIfValid(position: Position, basePosition: Position, cellList: List[(Int, Int)], actionType: ActionType): List[(Int, Int)] = {
    var addToList = false
    if (isInBound(position)) {
      val gameObjectOpt = gameObjectAt(position)
      actionType match {
        case t: ActionType.SHOOT.type => {
          var occupiedByPlayer = false
          if (gameObjectOpt.isDefined && gameObjectOpt.get.isInstanceOf[PlayerObject]) {
            occupiedByPlayer = true
          }
          if (position != basePosition && (gameObjectOpt.isEmpty || occupiedByPlayer)) {
            addToList = true
          }
        }
        case t: ActionType.MOVE.type => {
          if (position != basePosition && gameObjectOpt.isEmpty) {
            addToList = true
          }
        }
      }
    }
    if (addToList) {
      ((position.x, position.y)) :: cellList
    } else {
      cellList
    }
  }

  def cellsInRange(position: Position, action: Action): List[(Int, Int)] = {
    var cellsInRangeList = List[(Int, Int)]()
    val range = action.range

    action.actionType match {
      case a: ActionType.WAIT.type => cellsInRangeList = ((position.x, position.y)) :: cellsInRangeList
      case _ => {
        // TODO: check if there is somwthing in the way
        loopForwards(position.x, position.x + range + 1, x => {
          val pos = Position(x, position.y)
          cellsInRangeList = addPosToListIfValid(pos, position, cellsInRangeList, action.actionType)
        })
        loopForwards(position.y, position.y + range + 1, y => {
          val pos = Position(position.x, y)
          cellsInRangeList = addPosToListIfValid(pos, position, cellsInRangeList, action.actionType)
        })
        loopBackwards(position.y, position.y - range - 1, y => {
          val pos = Position(position.x, y)
          cellsInRangeList = addPosToListIfValid(pos, position, cellsInRangeList, action.actionType)
        })
        loopBackwards(position.x, position.x - range - 1, x => {
          val pos = Position(x, position.y)
          cellsInRangeList = addPosToListIfValid(pos, position, cellsInRangeList, action.actionType)
        })
        loopForwards(position.x, position.x + range + 1, x => {
          loopBackwards(position.y, position.y - range - 1, y => {
            val pos = Position(x, y)
            cellsInRangeList = addPosToListIfValid(pos, position, cellsInRangeList, action.actionType)
          })
        })
        loopForwards(position.y, position.y + range + 1, y => {
          loopBackwards(position.x, position.x - range - 1, x => {
            val pos = Position(x, y)
            cellsInRangeList = addPosToListIfValid(pos, position, cellsInRangeList, action.actionType)
          })
        })
        loopForwards(position.x, position.x + range + 1, x => {
          loopForwards(position.y, position.y + range + 1, y => {
            val pos = Position(x, y)
            cellsInRangeList = addPosToListIfValid(pos, position, cellsInRangeList, action.actionType)
          })
        })
        loopBackwards(position.x, position.x - range - 1, x => {
          loopBackwards(position.y, position.y - range - 1, y => {
            val pos = Position(x, y)
            cellsInRangeList = addPosToListIfValid(pos, position, cellsInRangeList, action.actionType)
          })
        })
      }
    }
    cellsInRangeList
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
