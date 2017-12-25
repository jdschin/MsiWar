package de.htwg.se.msiwar.model

import de.htwg.se.msiwar.model.ActionType.ActionType

case class GameBoard(rows: Int, columns: Int, gameObjects: List[GameObject]) {
  private val board = Array.ofDim[GameObject](rows, columns)

  private val incXandIncY = (x: Int, y: Int) => (x + 1, y + 1)
  private val decXandDecY = (x: Int, y: Int) => (x - 1, y - 1)
  private val incXandDecY = (x: Int, y: Int) => (x + 1, y - 1)
  private val decXandIncY = (x: Int, y: Int) => (x - 1, y + 1)
  private val incX = (x: Int, y: Int) => (x + 1, y)
  private val incY = (x: Int, y: Int) => (x, y + 1)
  private val decX = (x: Int, y: Int) => (x - 1, y)
  private val decY = (x: Int, y: Int) => (x, y - 1)
  private val changeNothing = (x: Int, y: Int) => (x, y)

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
    var collisionObject: Option[GameObject] = Option.empty
    if (isInBound(to) && isInBound(from)) {
      var countFunction = changeNothing
      var range = 0

      if (from.x != to.x) {
        range = math.abs(from.x - to.x)
      } else if (from.y != to.y) {
        range = math.abs(from.y - to.y)
      }

      if (from.x < to.x && from.y < to.y) {
        // RIGHT_UP
        countFunction = incXandIncY
      } else if (from.x < to.x && from.y > to.y) {
        // RIGHT_DOWN
        countFunction = incXandDecY
      } else if (from.x > to.x && from.y < to.y) {
        // LEFT_UP
        countFunction = decXandIncY
      } else if (from.x > to.x && from.y > to.y) {
        // LEFT_DOWN
        countFunction = decXandDecY
      } else if (from.x < to.x) {
        // RIGHT
        countFunction = incX
      } else if (from.x > to.x) {
        // LEFT
        countFunction = decX
      } else if (from.y < to.y) {
        // UP
        countFunction = incY
      } else if (from.y > to.y) {
        // DOWN
        countFunction = decY
      }

      loopRange(from, range, countFunction, (x, y) => {
        val pos = Position(x, y)
        if (isInBound(pos)) {
          val gameObject = gameObjectAt(pos)
          if (gameObject.isDefined) {
            collisionObject = Option(gameObject.get)
          }
        }
      }
      )
    }

    collisionObject
  }

  private def addPosToListIfValid(position: Position, basePosition: Position, cellList: List[(Int, Int)], actionType: ActionType): List[(Int, Int)] = {
    var addToList = false
    if (isInBound(position)) {
      val gameObjectOpt = gameObjectAt(position)
      val xOffSet = math.abs(position.x - basePosition.x)
      val yOffSet = math.abs(position.y - basePosition.y)
      var collisionObjectInBetween = false
      if (xOffSet > 1 || yOffSet > 1) {
        collisionObjectInBetween = collisionObject(basePosition, position).isDefined
      }

      actionType match {
        case t: ActionType.SHOOT.type => {
          var occupiedByPlayer = false
          if (gameObjectOpt.isDefined && gameObjectOpt.get.isInstanceOf[PlayerObject]) {
            occupiedByPlayer = true
          }
          if (position != basePosition && (gameObjectOpt.isEmpty || occupiedByPlayer) && !collisionObjectInBetween) {
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
        val loopFunctions = incX :: incY :: decX :: decY :: incXandIncY :: decXandDecY :: incXandDecY :: decXandIncY :: Nil

        loopFunctions.foreach(f => {
          loopRange(position, range, f, (x, y) => {
            cellsInRangeList = addPosToListIfValid(Position(x, y), position, cellsInRangeList, action.actionType)
          })
        })
      }
    }
    cellsInRangeList
  }

  private def loopRange(basePosition: Position, range: Int, count: (Int, Int) => (Int, Int), f: (Int, Int) => Unit): Unit = {
    var x = basePosition.x
    var y = basePosition.y
    for (_ <- 0 until range) {
      val countResult = count(x, y)
      f(countResult._1, countResult._2)
      x = countResult._1
      y = countResult._2
    }
  }
}
