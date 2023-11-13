# [DEPRECATED] Beard: A Blazing-Fast Template Engine

[![Build Status](https://travis-ci.org/zalando/beard.svg)](https://travis-ci.org/zalando/beard)

Beard is a logic-less templating engine written in Scala and inspired by [Mustache](https://mustache.github.io/). You can use it out-of-the-box; see the [Requirements list](#requirements) below.

What makes Beard powerful:

  - **Streaming**. As soon as you need to render something, you can stream it to the browser. This provides high user-perceived performance.
  - **Speed**. We've benchmarked it against other template engines for the JVM, and Beard performed much faster in terms of rendering time. (We invite you to run your own benchmarks to see if you get the same results.) It also uses [ANTLR](http://www.antlr.org/) to make template compilation fast.
  - It offers **template inheritance**.
  - Its **simple, beautiful syntax**. A la Mustache, it uses only the `{` and `}` markers for tags and delimiters.

Here's a code snippet to show you how simply Beard can parse templates:

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

## Requirements

- Scala 2.12 or 2.13
- a package manager like sbt or Maven

## Installing

If you're using sbt, add this line to your build.sbt file:

    libraryDependencies += "de.zalando" %% "beard" % "0.3.1"

    resolvers ++= Seq(
      "zalando-maven" at "https://dl.bintray.com/zalando/maven"
    )

If you're using Maven, run this:

    <repositories>
        <repository>
            <snapshots>
                <enabled>false</enabled>
            </snapshots>
            <id>bintray-maven</id>
            <name>bintray</name>
            <url>https://dl.bintray.com/zalando/maven</url>
        </repository>
    </repositories>

    <dependency>
        <groupId>de.zalando</groupId>
        <artifactId>beard_2.13</artifactId>
        <version>0.3.1</version>
    </dependency>

Binaries are available from [bintray](https://dl.bintray.com/zalando/maven/de/zalando/beard_2.13/0.3.1/)

## Additional Documentation

We've started a [Gitbook](https://danpersa.gitbooks.io/beard/content/) for additional docs. There, you'll find more information on:

- [Basic Usage](https://danpersa.gitbooks.io/beard/content/chapter-1-basic-usage.html): rendering an `index` template
- [Control Flow](https://danpersa.gitbooks.io/beard/content/chapter-2-control-flow.html): If statements and For statements
- [Template Inheritance](https://danpersa.gitbooks.io/beard/content/chapter-3-template-inheritance.html): layout templates and templates-to-be-rendered
- [Filters](https://danpersa.gitbooks.io/beard/content/chapter-4-filters.html): describes the filters feature
- [Quick Reference](https://danpersa.gitbooks.io/beard/content/chapter-5-quick-reference.html): with details and code on interpolation, comment statements, block statements, yield statements, and more

You can contribute to this documentation [here](https://github.com/danpersa/beard-book).

## Performance Tests

Here is how to run them:
    sbt "testOnly de.zalando.beard.performance.JadeBenchmark"

## Contributing/TODO List

We gladly welcome contributions—just submit a pull request with a short note summarizing briefly (1-2 sentences) what you've done. If you'd like to make a substantial contribution to Beard, we could use your help with these items:
- adding filters: ind Handlebars, Angular, Twig, etc.
- providing more meaningful error messages

## License
Copyright 2015 Zalando SE

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
