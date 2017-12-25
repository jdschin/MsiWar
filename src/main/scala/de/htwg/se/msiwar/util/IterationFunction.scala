package de.htwg.se.msiwar.util

object IterationFunction {
  val incXandIncY = (x: Int, y: Int) => (x + 1, y + 1)
  val decXandDecY = (x: Int, y: Int) => (x - 1, y - 1)
  val incXandDecY = (x: Int, y: Int) => (x + 1, y - 1)
  val decXandIncY = (x: Int, y: Int) => (x - 1, y + 1)
  val incX = (x: Int, y: Int) => (x + 1, y)
  val incY = (x: Int, y: Int) => (x, y + 1)
  val decX = (x: Int, y: Int) => (x - 1, y)
  val decY = (x: Int, y: Int) => (x, y - 1)
  val changeNothing = (x: Int, y: Int) => (x, y)

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
