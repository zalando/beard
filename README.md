# Beard Template Engine

[![Build Status](https://travis-ci.org/zalando/beard.svg)](https://travis-ci.org/zalando/beard)

Blazing fast, [open source](https://github.com/zalando/beard), logic-less template engine written in Scala, used to parse templates using a simple syntax:

```html
<html>
  <head>
	<title>{{ the.title }}</title>
  </head>
  <body>
	{{ the.content }}
  </body>
</html>
```

What makes Beard special:

  - simple syntax: inspired by mustache, we only use the `{` and `}` markers for tags
  - streaming: as soon as we have something to be rendered, we are able to stream it to the browser. This gives high user perceived performance.
  - fast: have benchmarked it against other jvm template engines, and we strive to keep it on top. Check out our [repository](https://github.com/zalando/beard) to run the benchmarks.
  - beautiful: only using brackets for delimiters keeps it beautiful

Uses ANTRL to compile the templates which makes the compilation really fast.

More details in our [documentation](https://danpersa.gitbooks.io/beard/content/)

# Gitbook

Contribute to our gitbook [here](https://github.com/danpersa/beard-book).

# Downloads
 
Binaries are available from [bintray](https://bintray.com/zalando-spearheads/java/beard/0.0.2/view). Or simply
add `resolvers += Resolver.bintrayRepo("zalando-spearheads", "java")` to your `build.sbt`.

# Publish

To login to bintray:

    sbt bintrayChangeCredentials

Then for cross publish

    sbt +publish

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
