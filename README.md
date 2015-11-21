Templating Maven Plugin
=======================

[![Maven Central](https://img.shields.io/maven-central/v/org.codehaus.mojo/templating-maven-plugin.svg?label=Maven%20Central)](http://search.maven.org/#search%7Cga%7C1%7Ctemplating-maven-plugin) [![Build Status](https://travis-ci.org/mojohaus/templating-maven-plugin.svg)](https://travis-ci.org/mojohaus/templating-maven-plugin)

The [Templating Maven Plugin](http://www.mojohaus.org/templating-maven-plugin/)
handles copying files from a source to a given output directory, while
filtering them. This plugin is useful to filter Java Source Code if you need
for example to have things in that code replaced with some properties values.

## Releasing

* Make sure `gpg-agent` is running.
* Execute `mvn -B release:prepare release:perform`

For publishing the site do the following:

```
cd target/checkout
mvn verify site site:stage scm-publish:publish-scm
```
