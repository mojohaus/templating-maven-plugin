/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

def generated = new File(basedir, 'target/generated-sources/java-templates/pkg/SomeClass.java')
assert generated.isFile() : "the template was not copied to the generated sources"
assert generated.getText('UTF-8').contains('VERSION = "<1.0-specialversion>"') : generated.getText('UTF-8')

// the generated directory is a source root: the template and the class using it both compiled
assert new File(basedir, 'target/classes/pkg/SomeClass.class').isFile()
assert new File(basedir, 'target/classes/pkg/some/SomeOtherClass.class').isFile()
assert new File(basedir, 'target/classes/net/batmat/maven/App.class').isFile()

return true
