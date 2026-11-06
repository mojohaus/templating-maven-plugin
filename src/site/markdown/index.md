title: Introduction
author: Baptiste Mathus
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

# Templating Maven Plugin

The templating maven plugin handles copying files from a source to a given output directory, while filtering them.
This plugin is useful to filter Java Source Code if you need for example to have things in that code
replaced with some *properties* values.

## Goals Overview

- [templating:filter-sources](./filter-sources-mojo.html) lets you filter your sources in one go.
- [templating:filter-test-sources](./filter-test-sources-mojo.html) lets you filter your test sources in one go.

## Usage

General instructions on how to use the Templating Plugin can be found on the [usage page](./usage.html). Some more
specific use cases are described in the examples given below.

In case you still have questions regarding the plugin's usage, please have a look at the [FAQ](./faq.html) and feel
free to contact the [user mailing list](./mail-lists.html). The posts to the mailing list are archived and could
already contain the answer to your question as part of an older thread. Hence, it is also worth browsing/searching
the [mail archive](./mail-lists.html).

If you feel like the plugin is missing a feature or has a defect, you can fill a feature request or bug report in our
[issue tracker](./issue-tracking.html). When creating a new issue, please provide a comprehensive description of your
concern. Especially for fixing bugs it is crucial that the developers can reproduce your problem. For this reason,
entire debug logs, POMs or most preferably little demo projects attached to the issue are very much appreciated.
Of course, patches are welcome, too. Contributors can check out the project from our
[source repository](./source-repository.html) and will find supplementary information in the
[guide to helping with Maven](http://maven.apache.org/guides/development/guide-helping.html).

## Examples

The following examples show how to use the Templating Plugin in more advanced
usecases:

- [How to typically configure the filtering](./examples/source-filtering.html)
- [Specifying a character encoding scheme](./examples/encoding.html)

## Technical details

Currently, it uses the [Maven Filtering](http://maven.apache.org/shared/maven-filtering/)
shared component for filtering resources.
