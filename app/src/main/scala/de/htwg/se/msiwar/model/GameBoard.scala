package de.htwg.se.msiwar.model

import de.htwg.se.msiwar.model.ActionType.ActionType
import de.htwg.se.msiwar.util.Direction
import de.htwg.se.msiwar.util.Direction.Direction
import de.htwg.se.msiwar.util.IterationFunction._

import scala.Option.empty

case class GameBoard(rows: Int, columns: Int, gameObjects: List[GameObject]) {
  private val board = Array.ofDim[GameObject](rows, columns)

  gameObjects.foreach(placeGameObject)

  def placeGameObject(gameObject: GameObject): Unit = {
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

  def moveGameObject(gameObject: GameObject, newPosition: Position): Unit = {
    removeGameObject(gameObject)
    gameObject.position.x = newPosition.x
    gameObject.position.y = newPosition.y
    placeGameObject(gameObject)
  }

  def removeGameObject(gameObject: GameObject): Unit = {
    board(gameObject.position.x)(gameObject.position.y) = null
  }

  def collisionObject(from: Position, to: Position, ignoreLastPosition: Boolean): Option[GameObject] = {
    var collisionObject: Option[GameObject] = empty
    if (isInBound(to) && isInBound(from)) {
      var countFunction: (Int, Int) => (Int, Int) = changeNothing
      var range = 0

      if (from.x != to.x) {
        range = math.abs(from.x - to.x)
      } else if (from.y != to.y) {
        range = math.abs(from.y - to.y)
      }

      if (from.x < to.x && from.y < to.y) {
        // RIGHT_UP
        countFunction = incXIncY
      } else if (from.x < to.x && from.y > to.y) {
        // RIGHT_DOWN
        countFunction = incXDecY
      } else if (from.x > to.x && from.y < to.y) {
        // LEFT_UP
        countFunction = decXIncY
      } else if (from.x > to.x && from.y > to.y) {
        // LEFT_DOWN
        countFunction = decXDecY
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
      if (ignoreLastPosition) {
        range -= 1
      }
      performOnPositionNTimes((from.x, from.y), range, countFunction, (x, y) => {
        val pos = Position(x, y)
        if (isInBound(pos) && !collisionObject.isDefined) {
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
        collisionObjectInBetween = collisionObject(basePosition, position, ignoreLastPosition = true).isDefined
      }

      actionType match {
        case _: ActionType.SHOOT.type =>
          if (position != basePosition && !collisionObjectInBetween) {
            addToList = true
          }
        case _: ActionType.MOVE.type =>
          if (position != basePosition && gameObjectOpt.isEmpty) {
            addToList = true
          }
      }
    }
    if (addToList) {
      (position.x, position.y) :: cellList
    } else {
      cellList
    }
  }

  def cellsInRange(position: Position, action: Action): List[(Int, Int)] = {
    var cellsInRangeList = List[(Int, Int)]()
    val range = action.range

    action.actionType match {
      case _: ActionType.WAIT.type => cellsInRangeList = (position.x, position.y) :: cellsInRangeList
      case _ =>
        val loopFunctions = incX _ :: incY _ :: decX _ :: decY _ :: incXIncY _ :: decXDecY _ :: incXDecY _ :: decXIncY _ :: Nil

        loopFunctions.foreach(f => {
          performOnPositionNTimes((position.x, position.y), range, f, (x, y) => {
            cellsInRangeList = addPosToListIfValid(Position(x, y), position, cellsInRangeList, action.actionType)
          })
        })
    }
    cellsInRangeList
  }


  def calculatePositionForDirection(oldPosition: Position, direction: Direction, range: Int): Position = {
    var newPosition: Option[Position] = empty
    var modifyPosition: (Int, Int) => (Int, Int) = changeNothing
    direction match {
      case Direction.UP => modifyPosition = decY
      case Direction.DOWN => modifyPosition = incY
      case Direction.LEFT => modifyPosition = decX
      case Direction.RIGHT => modifyPosition = incX
      case Direction.LEFT_UP => modifyPosition = decXDecY
      case Direction.LEFT_DOWN => modifyPosition = decXIncY
      case Direction.RIGHT_UP => modifyPosition = incXDecY
      case Direction.RIGHT_DOWN => modifyPosition = incXIncY
    }
    performOnPositionNTimes((oldPosition.x, oldPosition.y), range, modifyPosition, (x, y) => {
      val pos = Position(x, y)
      if (isInBound(pos)) {
        // The last position which is in bound
        newPosition = Option(pos)
      }
    })
    newPosition.get
  }
}
