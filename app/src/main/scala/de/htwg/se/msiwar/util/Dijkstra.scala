package de.htwg.se.msiwar.util

object Dijkstra {

  type Path[Key] = (Int, List[Key])

  def findPath[Key](lookup: Map[Key, List[(Int, Key)]], fringe: List[Path[Key]], dest: Key, visited: Set[Key]): Option[Path[Key]] = fringe match {
    case (dist, path) :: fringe_rest => path match {
      case key :: _ =>
        if (key == dest) Option((dist, path.reverse))
        else {
          val paths = lookup(key).flatMap { case (d, key) => if (!visited.contains(key)) List((dist + d, key :: path)) else Nil }
          val sorted_fringe = (paths ++ fringe_rest).sortWith { case ((d1, _), (d2, _)) => d1 < d2 }
          findPath(lookup, sorted_fringe, dest, visited + key)
        }
    }
    case Nil => Option.empty
  }
}