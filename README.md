Templating Maven Plugin
=======================

[![Apache License, Version 2.0, January 2004](https://img.shields.io/github/license/mojohaus/templating-maven-plugin.svg?label=License)](https://www.apache.org/licenses/)
[![Maven Central](https://img.shields.io/maven-central/v/org.codehaus.mojo/templating-maven-plugin.svg?label=Maven%20Central)](https://central.sonatype.com/artifact/org.codehaus.mojo/templating-maven-plugin)
[![GitHub CI](https://github.com/mojohaus/templating-maven-plugin/actions/workflows/maven.yml/badge.svg?branch=master)](https://github.com/mojohaus/templating-maven-plugin/actions/workflows/maven.yml)

The [Templating Maven Plugin](https://www.mojohaus.org/templating-maven-plugin/)
copies the files of a template directory to an output directory while filtering them, and adds the output
directory as a source root. Use it to put values such as POM properties into Java source code, for example into
annotation constants. It requires Maven 3.9.0 or later and Java 8 or later.

Put the templates in `src/main/java-templates` (or `src/test/java-templates` for test sources) and bind the goals:

```xml
<plugin>
  <groupId>org.codehaus.mojo</groupId>
  <artifactId>templating-maven-plugin</artifactId>
  <version><!-- latest release --></version>
  <executions>
    <execution>
      <id>filter-src</id>
      <goals>
        <goal>filter-sources</goal>
      </goals>
    </execution>
  </executions>
</plugin>
```

The [usage guide](https://www.mojohaus.org/templating-maven-plugin/usage.html) and the
[goal parameters](https://www.mojohaus.org/templating-maven-plugin/filter-sources-mojo.html) are on the plugin site.
See [AGENTS.md](AGENTS.md) for build, test and pull request conventions.

Releasing
---------

* Make sure `gpg-agent` is running.
* Execute `mvn -B release:prepare release:perform`

For publishing the site do the following:

```
cd target/checkout
mvn verify site site:stage scm-publish:publish-scm
```

