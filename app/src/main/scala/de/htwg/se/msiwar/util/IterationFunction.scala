package de.htwg.se.msiwar.util

object IterationFunction {
  def incXIncY(x: Int, y: Int): (Int, Int) = (x + 1, y + 1)

  def decXDecY(x: Int, y: Int): (Int, Int) = (x - 1, y - 1)

  def incXDecY(x: Int, y: Int): (Int, Int) = (x + 1, y - 1)

  def decXIncY(x: Int, y: Int): (Int, Int) = (x - 1, y + 1)

  def incX(x: Int, y: Int): (Int, Int) = (x + 1, y)

  def incY(x: Int, y: Int): (Int, Int) = (x, y + 1)

  def decX(x: Int, y: Int): (Int, Int) = (x - 1, y)

  def decY(x: Int, y: Int): (Int, Int) = (x, y - 1)

  def changeNothing(x: Int, y: Int): (Int, Int) = (x, y)

  def performOnPositionNTimes(basePosition: (Int, Int), n: Int, count: (Int, Int) => (Int, Int), f: (Int, Int) => Unit): Unit = {
    var x = basePosition._1
    var y = basePosition._2
    for (_ <- 0 until n) {
      val countResult = count(x, y)
      f(countResult._1, countResult._2)
      x = countResult._1
      y = countResult._2
    }
  }

}
