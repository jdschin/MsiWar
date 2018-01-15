package de.htwg.se.msiwar

import de.htwg.se.msiwar.model._
import de.htwg.se.msiwar.util.Direction
import de.htwg.se.msiwar.util.Direction._
import org.scalatest.{FlatSpec, Matchers}


class GameBoardSpec extends FlatSpec with Matchers {

  GameBoard.getClass.getSimpleName should "throw IllegalArgumentException when column or row count is negative" in {
    a[IllegalArgumentException] should be thrownBy {
      GameBoard(-1, 1, List())
    }
    a[IllegalArgumentException] should be thrownBy {
      GameBoard(1, -1, List())
    }
    a[IllegalArgumentException] should be thrownBy {
      GameBoard(-1, -1, List())
    }

  }

  it should "return LEFT_UP for the given positions" in {
    val direction = GameBoard(10, 10, List()).calculateDirection(Position(1, 1), Position(0, 0))

    direction should be(LEFT_UP)
  }

  it should "return LEFT_DOWN for the given positions" in {
    val direction = GameBoard(10, 10, List()).calculateDirection(Position(1, 1), Position(2, 0))

    direction should be(LEFT_DOWN)
  }

  it should "return LEFT for the given positions" in {
    val direction = GameBoard(10, 10, List()).calculateDirection(Position(1, 1), Position(1, 0))

    direction should be(LEFT)
  }

  it should "return RIGHT for the given positions" in {
    val direction = GameBoard(10, 10, List()).calculateDirection(Position(1, 1), Position(1, 2))

    direction should be(RIGHT)
  }

  it should "return RIGHT_UP for the given positions" in {
    val direction = GameBoard(10, 10, List()).calculateDirection(Position(1, 1), Position(0, 2))

    direction should be(RIGHT_UP)
  }

  it should "return RIGHT_DOWN for the given positions" in {
    val direction = GameBoard(10, 10, List()).calculateDirection(Position(1, 1), Position(2, 2))

    direction should be(RIGHT_DOWN)
  }

  it should "return DOWN for the given positions" in {
    val direction = GameBoard(10, 10, List()).calculateDirection(Position(1, 1), Position(2, 1))

    direction should be(DOWN)
  }

  it should "return UP for the given positions" in {
    val direction = GameBoard(10, 10, List()).calculateDirection(Position(1, 1), Position(0, 1))

    direction should be(UP)
  }

  it should "return a collision object when searching for it in the same column" in {
    val wood = BlockObject("B", "images/block_wood.png", Position(0, 5))

    val gameBoard = GameBoard(10, 10, List(wood))
    val collisionObject = gameBoard.collisionObject(Position(0, 0), Position(0, 7), false)
    collisionObject.get should be(wood)
  }

  it should "return a collision object when searching for it in the same row" in {
    val wood = BlockObject("B", "images/block_wood.png", Position(5, 0))

    val gameBoard = GameBoard(10, 10, List(wood))
    val collisionObject = gameBoard.collisionObject(Position(0, 0), Position(7, 0), false)
    collisionObject.get should be(wood)
  }

  it should "return no collision object when last position is ignored" in {
    val wood = BlockObject("B", "images/block_wood.png", Position(7, 0))

    val gameBoard = GameBoard(10, 10, List(wood))
    val collisionObject = gameBoard.collisionObject(Position(0, 0), Position(7, 0), true)
    collisionObject.isEmpty should be(true)
  }

  it should "return the moved GameObject from the new position" in {
    val oldPosition = Position(7, 6)
    val newPosition = Position(7, 7)

    val player = PlayerObject("Player1", "images/medium_tank_blue.png", oldPosition.copy(), Direction.LEFT,
      playerNumber = 1, "images/background_won_blue.png", maxActionPoints = 3, maxHealthPoints = 3, List())

    val gameBoard = GameBoard(10, 10, List(player))

    gameBoard.moveGameObject(player, newPosition)
    gameBoard.gameObjectAt(newPosition).get should be(player)
    gameBoard.gameObjectAt(oldPosition).isEmpty should be(true)
    player.position.columnIdx should be(7)
    player.position.rowIdx should be(7)
  }

  it should "return no GameObject when it is removed" in {
    val position = Position(7, 6)
    val wood = BlockObject("B", "images/block_wood.png", position)

    val gameBoard = GameBoard(10, 10, List(wood))
    gameBoard.removeGameObject(wood)

    gameBoard.gameObjectAt(position).isEmpty should be(true)
  }

  it should "return the cell 0,0 because it is reachable for shooting" in {
    val shootAction = Action(id = 1, "Move", "", "",
      actionPoints = 3, range = 1, ActionType.SHOOT, damage = 2)

    val wood = BlockObject("B", "images/block_wood.png", Position(0, 0))

    val gameBoard = GameBoard(10, 10, List(wood))
    gameBoard.reachableCells(Position(1, 1), shootAction)
      .exists(p => p._1 == 0 && p._2 == 0) should be(true)
  }

  it should "not return the cell 0,0 because it is not reachable for moving" in {
    val shootAction = Action(id = 1, "Move", "", "",
      actionPoints = 1, range = 2, ActionType.MOVE, damage = 2)

    val wood = BlockObject("B", "images/block_wood.png", Position(0, 0))

    val gameBoard = GameBoard(10, 10, List(wood))
    !gameBoard.reachableCells(Position(2, 2), shootAction)
      .exists(p => p._1 == 0 && p._2 == 0) should be(true)
  }
}
