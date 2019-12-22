name := "scala-anyval-mocking"

version := "0.1"

scalaVersion := "2.13.1"
// Recommended by mockito-scala, but this is enabled by default in Scala 2.13
// scalacOptions += "-Ypartial-unification"

// Compiled `.scala` files are put into:
// `./target/scala-2.13/classes/` directory.


resolvers ++= Seq(
  "BinTray" at "https://dl.bintray.com/mockito/maven/"
)

libraryDependencies += "org.scalactic" %% "scalactic" % "3.1.0"
libraryDependencies += "org.scalatest" %% "scalatest" % "3.1.0" % "test"

libraryDependencies += "org.mockito" %% "mockito-scala" % "1.8.0" % "test"
// vs
// libraryDependencies += "org.mockito" % "mockito-core" % "2.7.19" % Test
