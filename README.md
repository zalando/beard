# The Beard Template Engine

Blazing fast template engine written in Scala used to parse simple templates like this:

    Hello {{person name='Dan' phone="123456576"}}

Uses ANTRL to compile the templates.

# Downloads
 
Binaries are available from [bintray](https://bintray.com/zalando-spearheads/java/beard/0.0.2/view). Or simply
add `resolvers += Resolver.bintrayRepo("zalando-spearheads", "java")` to your `build.sbt`.
