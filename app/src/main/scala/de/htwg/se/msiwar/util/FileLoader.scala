package de.htwg.se.msiwar.util

import java.nio.file.{FileSystems, Files, Path, Paths}
import java.util.Collections

case class FileRepresentation(fileName: String, filePath: String)

object FileLoader {

  def loadFilesFromDirPath(dirPath: String): List[FileRepresentation] = {
    var pathList = List[FileRepresentation]()
    val uri = classOf[Nothing].getResource(dirPath).toURI
    var myPath: Option[Path] = Option.empty

    if (uri.getScheme == "jar") {
      val fileSystem = FileSystems.newFileSystem(uri, Collections.emptyMap[String, Any])
      myPath = Option(fileSystem.getPath(dirPath))
    } else {
      myPath = Option(Paths.get(uri))
    }

    val walk = Files.walk(myPath.get, 1)
    val it = walk.iterator
    while (it.hasNext) {
      val path = it.next().toString
      val indexOfLastSlash = path.lastIndexOf("/")
      var fileName = path.substring(indexOfLastSlash, path.length)
      if (fileName != dirPath) {
        fileName = fileName.substring(1)
        pathList = pathList ::: List(FileRepresentation(fileName, path.toString))
      }
    }
    pathList
  }
}
