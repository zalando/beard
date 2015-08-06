name          := "beard"
organization  := "de.zalando.spearheads"
version       := "0.0.3-SNAPSHOT"

scalaVersion := "2.11.7"
scalacOptions := Seq("-unchecked", "-feature", "-deprecation", "-encoding", "utf8")

antlr4Settings

antlr4GenListener in Antlr4 := true

antlr4GenVisitor in Antlr4 := true

antlr4Dependency in Antlr4 := "org.antlr" % "antlr4" % "4.5"

antlr4PackageName in Antlr4 := Some("de.zalando.beard")

libraryDependencies ++= {
  Seq(
    "org.scala-lang"            % "scala-reflect"                         % scalaVersion.value,
    "org.scala-lang.modules"    %% "scala-xml"                            % "1.0.4",
    "org.scalatest"             %% "scalatest"                            % "3.0.0-M7"       % "test",
    "org.scalamock"             %% "scalamock-scalatest-support"          % "3.2.2"          % "test"
  )
}