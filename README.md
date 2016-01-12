[![Build Status](https://travis-ci.org/zalando/beard.svg)](https://travis-ci.org/zalando/beard)

# The Beard Template Engine

Blazing fast logicless template engine written in Scala used to parse simple templates like this:

    Hello {{person name='Dan' phone="123456576"}}

Uses ANTRL to compile the templates.

# Downloads
 
Binaries are available from [bintray](https://bintray.com/zalando-spearheads/java/beard/0.0.2/view). Or simply
add `resolvers += Resolver.bintrayRepo("zalando-spearheads", "java")` to your `build.sbt`.


Copyright 2015 Zalando SE

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.