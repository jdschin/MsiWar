package de.htwg.se.msiwar.util

object Direction extends Enumeration {
  type Direction = Value
  val UP, DOWN, LEFT, RIGHT, LEFT_UP, LEFT_DOWN, RIGHT_UP, RIGHT_DOWN = Value

  def toDegree(direction: Direction) : Int = {
    direction match  {
      case Direction.UP => 0
      case Direction.RIGHT_UP => 45
      case Direction.RIGHT => 90
      case Direction.RIGHT_DOWN => 135
      case Direction.DOWN => 180
      case Direction.LEFT_DOWN => 225
      case Direction.LEFT => 270
      case Direction.LEFT_UP => 315
    }
  }
}
