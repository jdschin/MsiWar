package de.htwg.se.msiwar.util

object IterationFunction {
  def incRowIdxIncColumnIdx(rowIdx: Int, columnIdx: Int): (Int, Int) = (rowIdx + 1, columnIdx + 1)

  def decRowIdxDecColumnIdx(rowIdx: Int, columnIdx: Int): (Int, Int) = (rowIdx - 1, columnIdx - 1)

  def incRowIdxDecColumnIdx(rowIdx: Int, columnIdx: Int): (Int, Int) = (rowIdx + 1, columnIdx - 1)

  def decRowIdxIncColumnIdx(rowIdx: Int, columnIdx: Int): (Int, Int) = (rowIdx - 1, columnIdx + 1)

  def incRowIdx(rowIdx: Int, columnIdx: Int): (Int, Int) = (rowIdx + 1, columnIdx)

  def incColumnIdx(rowIdx: Int, columnIdx: Int): (Int, Int) = (rowIdx, columnIdx + 1)

  def decRowIdx(rowIdx: Int, columnIdx: Int): (Int, Int) = (rowIdx - 1, columnIdx)

  def decColumnIdx(rowIdx: Int, columnIdx: Int): (Int, Int) = (rowIdx, columnIdx - 1)

  def changeNothing(rowIdx: Int, columnIdx: Int): (Int, Int) = (rowIdx, columnIdx)

  def performOnPositionNTimes(basePosition: (Int, Int), n: Int, count: (Int, Int) => (Int, Int), f: (Int, Int) => Unit): Unit = {
    var rowIdx = basePosition._1
    var columnIdx = basePosition._2
    for (_ <- 0 until n) {
      val countResult = count(rowIdx, columnIdx)
      f(countResult._1, countResult._2)
      rowIdx = countResult._1
      columnIdx = countResult._2
    }
  }

}
