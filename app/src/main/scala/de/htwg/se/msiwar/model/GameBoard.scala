package de.htwg.se.msiwar.model

import de.htwg.se.msiwar.model.ActionType.ActionType
import de.htwg.se.msiwar.util.Direction
import de.htwg.se.msiwar.util.Direction.Direction
import de.htwg.se.msiwar.util.IterationFunction._

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

      performOnPositionNTimes((from.x, from.y), range, countFunction, (x, y) => {
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
          performOnPositionNTimes((position.x, position.y), range, f, (x, y) => {
            cellsInRangeList = addPosToListIfValid(Position(x, y), position, cellsInRangeList, action.actionType)
          })
        })
      }
    }
    cellsInRangeList
  }


  def calculatePositionForDirection(oldPosition: Position, direction: Direction, range: Int): Position = {
    var newPosition: Option[Position] = None
    var modifyPosition = changeNothing
    direction match {
      case Direction.UP => modifyPosition = decY
      case Direction.DOWN => modifyPosition = incY
      case Direction.LEFT => modifyPosition = decX
      case Direction.RIGHT => modifyPosition = incX
      case Direction.LEFT_UP => modifyPosition = decXandDecY
      case Direction.LEFT_DOWN => modifyPosition = decXandIncY
      case Direction.RIGHT_UP => modifyPosition = incXandDecY
      case Direction.RIGHT_DOWN => modifyPosition = incXandIncY
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
