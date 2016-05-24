# Beard: A Blazing-Fast Template Engine

[![Build Status](https://travis-ci.org/zalando/beard.svg)](https://travis-ci.org/zalando/beard)

Beard is an open-source, logic-less templating engine, written in Scala and inspired by [Mustache](https://mustache.github.io/). You can use it out-of-the-box; see the Requirements list below. 

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
- Scala 2.10 or 2.11
- a package manager like sbt or Maven

## Installing
If you're using sbt, add this line to your build.sbt file:

    libraryDependencies += "de.zalando" %% "beard" % "0.1.1"

    resolvers ++= Seq(
      "zalando-maven" at "https://dl.bintray.com/zalando/maven"
    )

If you're using Maven, run this:

    <dependency>
    	<groupId>de.zalando</groupId>
    	<artifactId>beard</artifactId>
    	<version>0.1.1</version>
    </dependency>
 
Binaries are available from [bintray](https://bintray.com/zalando/maven/beard/0.1.1/view).
   
## Publishing

To login to bintray:

    sbt bintrayChangeCredentials

Then, for cross-publish:

    sbt +publish

##Additional Documentation
We've started a [Gitbook](https://danpersa.gitbooks.io/beard/content/) for additional docs. There, you'll find more information on:

- [Basic Usage](https://danpersa.gitbooks.io/beard/content/chapter-1-basic-usage.html): rendering an `index` template
- [Control Flow](https://danpersa.gitbooks.io/beard/content/chapter-2-control-flow.html): If statements and For statements
- [Template Inheritance](https://danpersa.gitbooks.io/beard/content/chapter-3-template-inheritance.html): layout templates and templates-to-be-rendered
- [Filters](https://danpersa.gitbooks.io/beard/content/chapter-4-filters.html): describes the filters feature
- [Quick Reference](https://danpersa.gitbooks.io/beard/content/chapter-5-quick-reference.html): with details and code on interpolation, comment statements, block statements, yield statements, and more

You can contribute to this documentation [here](https://github.com/danpersa/beard-book).

##Contributing/TODO List
We gladly welcome contributions—just submit a pull request with a short note summarizing briefly (1-2 sentences) what you've done. If you'd like to make a substantial contribution to Beard, we could use your help with these items:
- adding filters: ind Handlebars, Angular, Twig, etc.
- providing more meaningful error messages
- XSS prevention; [see related issue](https://github.com/zalando/beard/issues/11)
