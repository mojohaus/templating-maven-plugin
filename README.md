Templating Maven Plugin
=======================

[![Apache License, Version 2.0, January 2004](https://img.shields.io/github/license/mojohaus/templating-maven-plugin.svg?label=License)](http://www.apache.org/licenses/)
[![Maven Central](https://img.shields.io/maven-central/v/org.codehaus.mojo/templating-maven-plugin.svg?label=Maven%20Central)](http://search.maven.org/#search%7Cga%7C1%7Ctemplating-maven-plugin)
[![Build Status](https://travis-ci.org/mojohaus/templating-maven-plugin.svg)](https://travis-ci.org/mojohaus/templating-maven-plugin)

The templating maven plugin handles copying files from a source to a given output directory, while filtering them. This plugin is useful to filter Java Source Code if you need for example to have things in that code replaced with some properties values.

Goals Overview
--------------
* `templating:filter-sources` lets you filter your sources in one go.
* `templating:filter-test-sources` lets you filter your test sources in one go.

Usage
-----

```xml
<plugin>
    <groupId>org.codehaus.mojo</groupId>
    <artifactId>templating-maven-plugin</artifactId>
    <version>1.0.0</version>
    <executions>
        <execution>
            <goals>
                <goal>filter-sources</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

General instructions on how to use the Templating Plugin can be found on the usage page. Some more specific use cases are described in the examples given below.

In case you still have questions regarding the plugin's usage, please have a look at the FAQ and feel free to contact the user mailing list. The posts to the mailing list are archived and could already contain the answer to your question as part of an older thread. Hence, it is also worth browsing/searching the mail archive.

If you feel like the plugin is missing a feature or has a defect, you can fill a feature request or bug report in our issue tracker. When creating a new issue, please provide a comprehensive description of your concern. Especially for fixing bugs it is crucial that the developers can reproduce your problem. For this reason, entire debug logs, POMs or most preferably little demo projects attached to the issue are very much appreciated. Of course, patches are welcome, too. Contributors can check out the project from our source repository and will find supplementary information in the guide to helping with Maven.

Examples
--------

The following examples show how to use the Templating Plugin in more advanced usecases:

### How to typically configure the filtering ###

The Templating Maven Plugin **filter-sources** goal lets you filter a dedicated source folder. In one pass, it filters that folder and adds the resulting filtered folder to the POM model as a source folder.

If, for example, you have some of your code where you would like to reference properties coming from the POM, you want to put those classes inside the `sourceDirectory` tag.

> Please note that filtering the standard `src/main/java` folder is not supported.

```xml
<project>
...
  <build>
    <plugins>
      <plugin>
        <groupId>org.codehaus.mojo</groupId>
        <artifactId>templating-maven-plugin</artifactId>
        <version>1.0.0</version>
        <executions>
          <execution>
            <id>filter-src</id>
            <goals>
              <goal>filter-sources</goal>
            </goals>
                <configuration>              
                  <!-- 
                    Note the two following parameters are the default one. 
                    These are specified here just as a reminder. 
                    But as the Maven philosophy is strongly about conventions, 
                    it's better to just not specify them.
                  -->
                  <sourceDirectory>${basedir}/src/main/java-templates</sourceDirectory>
                  <outputDirectory>${project.build.directory}/generated-sources/java-templates</outputDirectory>
                </configuration>
          </execution>
        </executions>
      </plugin>
    </plugins>
  </build>

</project>
```

### Specifying a character encoding scheme ###

A character encoding scheme such as `ASCII`, `UTF-8` or `UTF-16` can be chosen to be used for the reading and writing of files.

For example, if we want to specify that the character encoding scheme be `UTF-8`, we would simply have to configure that as in the following example:

```xml
<project>
  ...
  <properties>
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
  </properties>

  <build>
    <plugins>
      <plugin>
        <groupId>org.codehaus.mojo</groupId>
        <artifactId>templating-maven-plugin</artifactId>
        <version>1.0.0</version>
      </plugin>
    </plugins>
    ...
  </build>
  ...
</project>
```

Goals Description
-----------------

### templating:filter-sources ###

#### Full name: ####

`org.codehaus.mojo:templating-maven-plugin:1.0.0:filter-sources`

#### Description: ####

This mojo helps adding a filtered source folder in one go. This is typically useful if you want to use properties coming from the POM inside parts of your source code that requires real constants, like annotations for example.

#### Attributes: ####

* Requires a Maven project to be executed.
* The goal is thread-safe and supports parallel builds.
* Binds by default to the *lifecycle* phase: `generate-sources`.

#### Optional Parameters ####

 Name       |  Type       | Description
:-----------|:------------|:------------
 delimiters | `List`      | Set of delimiters for expressions to filter within the resources. These delimiters are specified in the form 'beginToken\*endToken'. If no '\*' is given, the delimiter is assumed to be the same for start and end. So, the default filtering delimiters might be specified as: ```<delimiters> <delimiter>${*}</delimiter>  <delimiter>@</delimiter> </delimiters>```
 | |Since the '@' delimiter is the same on both ends, we don't need to specify '@*@' (though we can).
 encoding     | `String`      |    The character encoding scheme to be applied when filtering resources. 
 ||**Default value is:** `${project.build.sourceEncoding}`. 
escapeString  |  `String`    | Expression preceded with the String won't be interpolated `\${foo}` will be replaced with `${foo}`
||**User property is:** `maven.resources.escapeString`.
outputDirectory  |  `File` | Output folder where filtered sources will land.
|| **Default value is:** `${project.build.directory}/generated-sources/java-templates`.
sourceDirectory	| `File` | Source directory that will be first filtered and then added as a classical source folder.
|| **Default value is:** `${basedir}/src/main/java-templates`.
useDefaultDelimiters | `boolean`	| Controls whether the default delimiters are included in addition to those configured delimiters. Does not have any effect if delimiters is empty when the defaults will be included anyway.
|| **Default value is:** `true`.

### templating:filter-test-sources ###

#### Full name: ####

`org.codehaus.mojo:templating-maven-plugin:1.0.0:filter-test-sources`

#### Description: ####

This mojo helps adding a filtered source folder in one go. This is typically useful if you want to use properties coming from the POM inside parts of your test source code that requires real constants, like annotations for example.

#### Attributes: ####

* Requires a Maven project to be executed.
* The goal is thread-safe and supports parallel builds.
* Binds by default to the *lifecycle* phase: `generate-test-sources`.


#### Optional Parameters ####

 Name       |  Type       | Description
:-----------|:------------|:------------
 delimiters | `List`      | Set of delimiters for expressions to filter within the resources. These delimiters are specified in the form 'beginToken\*endToken'. If no '\*' is given, the delimiter is assumed to be the same for start and end. So, the default filtering delimiters might be specified as: ```<delimiters> <delimiter>${*}</delimiter>  <delimiter>@</delimiter> </delimiters>```
 | |Since the '@' delimiter is the same on both ends, we don't need to specify '@*@' (though we can).
 encoding     | `String`      |    The character encoding scheme to be applied when filtering resources. 
 ||**Default value is:** `${project.build.sourceEncoding}`. 
escapeString  |  `String`    | Expression preceded with the String won't be interpolated `\${foo}` will be replaced with `${foo}`
||**User property is:** `maven.resources.escapeString`.
testOutputDirectory  |  `File` | Output folder where filtered sources will land.
|| **Default value is:** `${project.build.directory}/generated-sources/java-templates`.
testSourceDirectory    | `File` | Source directory that will be first filtered and then added as a classical source folder.
|| **Default value is:** `${basedir}/src/main/java-templates`.
useDefaultDelimiters | `boolean`	| Controls whether the default delimiters are included in addition to those configured delimiters. Does not have any effect if delimiters is empty when the defaults will be included anyway.
|| **Default value is:** `true`.


Technical details
-----------------
Currently, it uses the Maven Filtering shared component for filtering resources and uses up-to-date mechanism.
