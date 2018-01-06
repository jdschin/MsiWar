package de.htwg.se.msiwar

import de.htwg.se.msiwar.model.GameBoard
import org.scalatest.{FlatSpec, Matchers}

class GameBoardSpec extends FlatSpec with Matchers {

  GameBoard.getClass.getSimpleName should "throw IllegalArgumentException when column or row count is negative" in {
    a [IllegalArgumentException] should be thrownBy {
      GameBoard(-1, 1, List())
    }
    a [IllegalArgumentException] should be thrownBy {
      GameBoard(1, -1, List())
    }
    a [IllegalArgumentException] should be thrownBy {
      GameBoard(-1, -1, List())
    }
  }
}