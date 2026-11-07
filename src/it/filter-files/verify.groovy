/*
 * Copyright MojoHaus and Contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
def main = new File(basedir, 'target/generated-sources/java-templates/demo/MainValues.java').getText('UTF-8')
def test = new File(basedir, 'target/generated-test-sources/java-templates/demo/TestValues.java').getText('UTF-8')

// main templates take values from both the plugin's filters and the POM's build filters
assert main.contains('NUMBER = "4711"') : main
assert main.contains('LABEL = "from the plugin filter"') : main

// with useBuildFilters=false only the plugin's filters apply
assert test.contains('LABEL = "from the plugin filter"') : test
assert test.contains('NUMBER = "${build.number}"') : test

// both generated classes compiled
assert new File(basedir, 'target/classes/demo/MainValues.class').isFile()
assert new File(basedir, 'target/test-classes/demo/TestValues.class').isFile()

return true
