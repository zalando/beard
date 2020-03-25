import scalariform.formatter.preferences._

name          := "beard"
organization  := "de.zalando"
version       := "0.3.1"
licenses      += ("Apache-2.0", url("http://www.apache.org/licenses/"))

scalaVersion := "2.13.1"
scalacOptions := Seq("-unchecked", "-feature", "-deprecation", "-encoding", "utf8")

val antlrVersion = "4.8-1"

crossScalaVersions := Seq(scalaVersion.value, "2.12.11")

enablePlugins(Antlr4Plugin)

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
    "org.scala-lang.modules"      %% "scala-xml"                            % "1.3.0",
    "org.slf4j"                    % "slf4j-api"                            % "1.7.30",
    "ch.qos.logback"               % "logback-classic"                      % "1.1.11",
    "org.scalatest"               %% "scalatest"                            % "3.1.1"          % "test",
    "io.pebbletemplates"           % "pebble"                               % "3.0.10"         % "test",
    "org.freemarker"               % "freemarker"                           % "2.3.28"         % "test",
    "com.github.spullara.mustache.java"   % "compiler"                      % "0.9.6"          % "test",
    "com.github.jknack"            % "handlebars"                           % "4.1.2"          % "test",
    "de.neuland-bfi"               % "jade4j"                               % "1.2.7"          % "test",
    "com.storm-enroute"           %% "scalameter"                           % "0.19"           % "test"
  )
}

scalariformPreferences := scalariformPreferences.value
  .setPreference(AlignSingleLineCaseStatements, true)
  .setPreference(AlignSingleLineCaseStatements.MaxArrowIndent, 100)
  .setPreference(PreserveSpaceBeforeArguments, true)
  .setPreference(DanglingCloseParenthesis, Preserve)
  .setPreference(DoubleIndentConstructorArguments, true)
  .setPreference(SpacesAroundMultiImports, false)

logBuffered := false
parallelExecution in Test := false
// testFrameworks += new TestFramework("org.scalameter.ScalaMeterFramework")

publishMavenStyle       := true
bintrayOrganization     := Some("zalando")
