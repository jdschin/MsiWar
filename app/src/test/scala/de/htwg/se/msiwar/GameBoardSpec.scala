package de.htwg.se.msiwar

import de.htwg.se.msiwar.model.GameBoard
import org.scalatest.{FlatSpec, Matchers}

class GameBoardSpec extends FlatSpec with Matchers {

  "GameBoard" should "" in {
    val gameBoard = GameBoard(1, 1, List())
  }
}