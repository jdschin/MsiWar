package de.htwg.se.msiwar.model

import de.htwg.se.msiwar.model.ActionType.ActionType
import de.htwg.se.msiwar.util.Direction
import de.htwg.se.msiwar.util.Direction._
import de.htwg.se.msiwar.util.IterationFunction._

import scala.Option.empty

case class GameBoard(rows: Int, columns: Int, gameObjects: List[GameObject]) {
  if (rows < 0 || columns < 0) {
    throw new IllegalArgumentException("rows and columns must be positive")
  }

  private val board = Array.ofDim[GameObject](rows, columns)

  gameObjects.foreach(placeGameObject)

  def placeGameObject(gameObject: GameObject): Unit = {
    board(gameObject.position.rowIdx)(gameObject.position.columnIdx) = gameObject
  }

  def gameObjectAt(position: Position): Option[GameObject] = {
    gameObjectAt(position.rowIdx, position.columnIdx)
  }

  def isInBound(position: Position): Boolean = {
    (position.rowIdx >= 0 && position.columnIdx >= 0) &&
      (position.rowIdx < rows && position.columnIdx < columns)
  }

  def gameObjectAt(rowIndex: Int, columnIndex: Int): Option[GameObject] = {
    val objectAt = board(rowIndex)(columnIndex)
    Option(objectAt)
  }

  def moveGameObject(gameObject: GameObject, newPosition: Position): Unit = {
    removeGameObject(gameObject)
    gameObject.position.rowIdx = newPosition.rowIdx
    gameObject.position.columnIdx = newPosition.columnIdx
    placeGameObject(gameObject)
  }

  def removeGameObject(gameObject: GameObject): Unit = {
    board(gameObject.position.rowIdx)(gameObject.position.columnIdx) = null
  }

  def collisionObject(from: Position, to: Position, ignoreLastPosition: Boolean): Option[GameObject] = {
    var collisionObject: Option[GameObject] = empty
    if (isInBound(to) && isInBound(from)) {
      var modifyPositionFunction: (Int, Int) => (Int, Int) = changeNothing
      var range = 0

      if (from.rowIdx != to.rowIdx) {
        range = math.abs(from.rowIdx - to.rowIdx)
      } else if (from.columnIdx != to.columnIdx) {
        range = math.abs(from.columnIdx - to.columnIdx)
      }
      modifyPositionFunction = modifyPositionFunctionForDirection(calculateDirection(from, to))

      if (ignoreLastPosition) {
        range -= 1
      }
      performOnPositionNTimes((from.rowIdx, from.columnIdx), range, modifyPositionFunction, (rowIdx, columnIdx) => {
        val pos = Position(rowIdx, columnIdx)
        if (isInBound(pos) && collisionObject.isEmpty) {
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

  private def modifyPositionFunctionForDirection(direction: Direction): (Int, Int) => (Int, Int) = {
    direction match {
      case RIGHT => incColumnIdx
      case RIGHT_UP => decRowIdxIncColumnIdx
      case RIGHT_DOWN => incRowIdxIncColumnIdx
      case LEFT => decColumnIdx
      case LEFT_UP => decRowIdxDecColumnIdx
      case LEFT_DOWN => incRowIdxDecColumnIdx
      case UP => decRowIdx
      case DOWN => incRowIdx
    }
  }

  private def addPosToListIfValid(position: Position, basePosition: Position, cellList: List[(Int, Int)], actionType: ActionType): List[(Int, Int)] = {
    var addToList = false
    if (isInBound(position)) {
      val gameObjectOpt = gameObjectAt(position)
      val rowOffSet = math.abs(position.rowIdx - basePosition.rowIdx)
      val columnOffSet = math.abs(position.columnIdx - basePosition.columnIdx)
      var collisionObjectInBetween = false
      if (rowOffSet > 1 || columnOffSet > 1) {
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
      (position.rowIdx, position.columnIdx) :: cellList
    } else {
      cellList
    }
  }

  def reachableCells(position: Position, action: Action): List[(Int, Int)] = {
    var reachableCellsList = List[(Int, Int)]()
    val range = action.range

    action.actionType match {
      case _: ActionType.WAIT.type => reachableCellsList = (position.rowIdx, position.columnIdx) :: reachableCellsList
      case _ =>
        cellsInRange(position, range).foreach(positionInRange => {
          reachableCellsList = addPosToListIfValid(positionInRange, position, reachableCellsList, action.actionType)
        })
    }
    reachableCellsList
  }

  def cellsInRange(position: Position, range: Int): List[Position] = {
    var cellsInRangeList = List[Position]()
    val loopFunctions = incRowIdx _ :: incColumnIdx _ :: decRowIdx _ :: decColumnIdx _ :: incRowIdxIncColumnIdx _ :: decRowIdxDecColumnIdx _ :: incRowIdxDecColumnIdx _ :: decRowIdxIncColumnIdx _ :: Nil

    loopFunctions.foreach(f => {
      performOnPositionNTimes((position.rowIdx, position.columnIdx), range, f, (rowIdx, columnIdx) => {
        cellsInRangeList = Position(rowIdx, columnIdx) :: cellsInRangeList
      })
    })
    cellsInRangeList
  }


  def calculateDirection(from: Position, to: Position): Direction = {
    if (from.columnIdx > to.columnIdx) {
      if (from.rowIdx < to.rowIdx) {
        Direction.LEFT_DOWN
      } else if (from.rowIdx > to.rowIdx) {
        Direction.LEFT_UP
      } else {
        Direction.LEFT
      }
    } else if (from.columnIdx < to.columnIdx) {
      if (from.rowIdx < to.rowIdx) {
        Direction.RIGHT_DOWN
      } else if (from.rowIdx > to.rowIdx) {
        Direction.RIGHT_UP
      } else {
        RIGHT
      }
    } else {
      if (from.rowIdx < to.rowIdx) {
        Direction.DOWN
      } else {
        Direction.UP
      }
    }
  }

  def calculatePositionForDirection(oldPosition: Position, direction: Direction, range: Int): Position = {
    var newPosition: Option[Position] = empty
    var modifyPositionFunction: (Int, Int) => (Int, Int) = changeNothing

    modifyPositionFunction = modifyPositionFunctionForDirection(direction)
    performOnPositionNTimes((oldPosition.rowIdx, oldPosition.columnIdx), range, modifyPositionFunction, (rowIdx, columnIdx) => {
      val pos = Position(rowIdx, columnIdx)
      if (isInBound(pos)) {
        // The last position which is in bound
        newPosition = Option(pos)
      }
    })
    newPosition.get
  }
}
