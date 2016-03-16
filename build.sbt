name          := "beard"
organization  := "de.zalando"
version       := "0.0.7-SNAPSHOT"
licenses      += ("Apache-2.0", url("http://www.apache.org/licenses/"))

scalaVersion := "2.11.7"
scalacOptions := Seq("-unchecked", "-feature", "-deprecation", "-encoding", "utf8")

val antlrVersion = "4.5.2"

crossScalaVersions := Seq(scalaVersion.value, "2.10.5")

antlr4Settings

antlr4GenListener in Antlr4 := true

antlr4GenVisitor in Antlr4 := true

antlr4Dependency in Antlr4 := "org.antlr" % "antlr4" % antlrVersion

antlr4PackageName in Antlr4 := Some("de.zalando.beard")

libraryDependencies ++= {
  Seq(
    "org.antlr"                    % "antlr4"                               % antlrVersion
                                                                              exclude("org.antlr", "ST4")
                                                                              exclude("org.antlr", "antlr-runtime"),
    "org.scala-lang"               % "scala-reflect"                        % scalaVersion.value,
    "org.scala-lang.modules"       % "scala-xml_2.11"                       % "1.0.4",
    "org.monifu"                  %% "monifu"                               % "1.0-RC3",
    "org.slf4j"                    % "slf4j-api"                            % "1.7.7",
    "ch.qos.logback"               % "logback-classic"                      % "1.0.13",
    "org.scalaz"                  %% "scalaz-core"                          % "7.2.1",
    "org.scalaz"                  %% "scalaz-effect"                        % "7.2.1",
    "org.scalatest"               %% "scalatest"                            % "3.0.0-M7"       % "test",
    "org.scalamock"               %% "scalamock-scalatest-support"          % "3.2.2"          % "test",
    "com.mitchellbosecke"          % "pebble"                               % "1.6.0"          % "test",
    "org.freemarker"               % "freemarker"                           % "2.3.23"         % "test",
    "com.github.spullara.mustache.java"   % "compiler"                      % "0.9.1"          % "test",
    "com.github.jknack"            % "handlebars"                           % "2.2.2"          % "test",
    "de.neuland-bfi"               % "jade4j"                               % "0.4.0"          % "test",
    "com.storm-enroute"           %% "scalameter"                           % "0.7"            % "test"
  )
}


net.virtualvoid.sbt.graph.Plugin.graphSettings
// testFrameworks += new TestFramework("org.scalameter.ScalaMeterFramework")

publishMavenStyle       := true
bintrayOrganization     := Some("zalando")
