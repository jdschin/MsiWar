package de.htwg.se.msiwar.util

import java.io.File
import java.nio.file.{FileSystems, Files, Path, Paths}
import java.util.Collections

object FileLoader {

  def loadFilesFromDirPath(dirPath: String): List[String] = {
    var pathList = List[String]()
    val uri = classOf[Nothing].getResource(dirPath).toURI
    var pathOpt: Option[Path] = Option.empty

    if (uri.getScheme == "jar") {
      val fileSystem = FileSystems.newFileSystem(uri, Collections.emptyMap[String, Any])
      pathOpt = Option(fileSystem.getPath(dirPath))
    } else {
      pathOpt = Option(Paths.get(uri))
    }

    val walk = Files.walk(pathOpt.get, 1)
    val it = walk.iterator
    while (it.hasNext) {
      val path = it.next().toString
      var indexOfLastSeparator = path.lastIndexOf(File.separator)
      if (indexOfLastSeparator < 0) {
        indexOfLastSeparator = path.lastIndexOf("/")
      }

      var fileName = path.substring(indexOfLastSeparator, path.length)
      if (!fileName.contains("scenario")) {
        fileName = fileName.substring(1)
        pathList = pathList ::: List(fileName)
      }
    }
    pathList
  }
}
