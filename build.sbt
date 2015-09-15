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
    "org.scala-lang"             % "scala-reflect"                        % scalaVersion.value,
    "org.scala-lang.modules"    %% "scala-xml"                            % "1.0.4",
    "io.reactivex"              %% "rxscala"                              % "0.25.0",
    "org.scalatest"             %% "scalatest"                            % "3.0.0-M7"       % "test",
    "org.scalamock"             %% "scalamock-scalatest-support"          % "3.2.2"          % "test",
    "com.mitchellbosecke"        % "pebble"                               % "1.5.1"          % "test",
    "org.freemarker"             % "freemarker"                           % "2.3.23"         % "test",
    "com.github.spullara.mustache.java" % "compiler"                      % "0.9.1"          % "test",
    "com.github.jknack"          % "handlebars"                           % "2.2.2"          % "test",
    "com.storm-enroute"         %% "scalameter"                           % "0.7"            % "test"
  )
}

testFrameworks += new TestFramework("org.scalameter.ScalaMeterFramework")