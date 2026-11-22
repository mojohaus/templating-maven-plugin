title: Usage
author:
- Baptiste Mathus
date: 2013-01-27

<!--
Licensed to the Apache Software Foundation (ASF) under one
or more contributor license agreements.  See the NOTICE file
distributed with this work for additional information
regarding copyright ownership.  The ASF licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License.  You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.
-->

Usage
=====

The Templating Maven Plugin handles the copying and filtering of project sources to the output
directory. The following examples describe the basic usage of the plugin.

To handle filtering this plugin uses the
[Maven Filtering](https://maven.apache.org/shared/maven-filtering/index.html) shared component.

Bind the goals to your build
----------------------------

Declare the plugin with an execution for each goal you need:

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
        <goal>filter-test-sources</goal>
      </goals>
    </execution>
  </executions>
</plugin>
```

`filter-sources` runs in the `generate-sources` phase. It filters `src/main/java-templates` into
`target/generated-sources/java-templates` and adds that directory as a source root. `filter-test-sources` runs in
the `generate-test-sources` phase, filters `src/test/java-templates` into
`target/generated-test-sources/java-templates` and adds that directory as a test source root. Without an execution,
the goals run only when you call them from the command line, as shown below.

Copy and filters your source files to the specified output directory
--------------------------------------------------------------------

```sh
mvn templating:filter-sources
```

Upon executing this command, your source files inside this directory will be copied and filtered to the output
directory you specified (or the default one).
Note: if you only want to filter resources files (not source code), you should use the standard src/main/resources
directory and the associated Maven Resources Plugin.

Moreover, when you declare an execution of this goal, it is bound to the `generate-sources` phase by default.

Copy and filters your test source files to the specified output directory
-------------------------------------------------------------------------

```sh
mvn templating:filter-test-sources
```

Upon executing this command, your source files inside this directory will be copied and filtered to the output
directory you specified (or the default one).
Note: if you only want to filter resources files (not source code), you should use the standard src/test/resources
directory and the associated Maven Resources Plugin.

Moreover, when you declare an execution of this goal, it is bound to the `generate-test-sources` phase by default.

Fill templates from property files
----------------------------------

Values that are awkward to write as POM properties, or that an earlier build step generates, can come from
property files. List them in the plugin's `filters`; relative paths are resolved against the project base
directory:

```xml
<plugin>
  <groupId>org.codehaus.mojo</groupId>
  <artifactId>templating-maven-plugin</artifactId>
  <configuration>
    <filters>
      <filter>src/filters/version.properties</filter>
    </filters>
  </configuration>
  <executions>
    <execution>
      <goals>
        <goal>filter-sources</goal>
      </goals>
    </execution>
  </executions>
</plugin>
```

With `build.number=4711` in that file, `"${build.number}"` in a template becomes `"4711"`.

The files in the POM's `<build><filters>` are applied as well, as they are for filtered resources. Set
`useBuildFilters` to `false` to fill the templates from the plugin's `filters` only.

Other options
-------------

These parameters apply to both goals; the [goal documentation](./filter-sources-mojo.html) describes each one in
full.

* `encoding`: the encoding used to read and write the files; it defaults to `project.build.sourceEncoding`.
* `delimiters` and `useDefaultDelimiters`: the expression delimiters, in the form `beginToken*endToken`. The defaults
  are `${*}` and `@`, which stay active unless `useDefaultDelimiters` is `false`.
* `escapeString` (property `maven.resources.escapeString`): the prefix that keeps an expression from being filtered.
* `overwrite`: copy files to the output directory even when they did not change; the default is `false`.
* `skipPoms`: the goals do nothing in a project with `pom` packaging; the default is `true`.

The directories are the `sourceDirectory` and `outputDirectory` parameters of `filter-sources`, and
`testSourceDirectory` and `testOutputDirectory` of `filter-test-sources`.
