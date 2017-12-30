name := """Pixel Tank War Play"""
version := "1.0-SNAPSHOT"
lazy val root = (project in file(".")).enablePlugins(PlayScala)
resolvers += Resolver.sonatypeRepo("snapshots")
scalaVersion := "2.12.3"
libraryDependencies += guice
libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.4" % "test"
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "3.1.2" % Test