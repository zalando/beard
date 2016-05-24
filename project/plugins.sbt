logLevel := Level.Warn

resolvers += "simplytyped" at "http://simplytyped.github.io/repo/releases"

addSbtPlugin("com.simplytyped" % "sbt-antlr4" % "0.7.4")

addSbtPlugin("net.virtual-void" % "sbt-dependency-graph" % "0.7.5")

addSbtPlugin("me.lessis" % "bintray-sbt" % "0.3.0")

addSbtPlugin("org.scalariform" % "sbt-scalariform" % "1.6.0")