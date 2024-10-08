package org.codehaus.mojo.templating;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import java.io.File;

import org.apache.maven.project.MavenProject;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

class OkFilterSourcesMojoTest {
    @Test
    void testExistingDirectoryDoesNotAddSourceFolder() {
        final StringBuilder placeholder = new StringBuilder();
        FilterSourcesMojo filterSourcesMojo = new FilterSourcesMojo() {
            @Override
            protected void addSourceFolderToProject(MavenProject mavenProject) {
                placeholder.append("called");
            }
        };
        filterSourcesMojo.sourceDirectory = new File(".");

        MavenProject mock = mock(MavenProject.class);
        Mockito.doThrow(IllegalArgumentException.class).when(mock).addCompileSourceRoot(anyString());
        Mockito.doThrow(IllegalArgumentException.class).when(mock).addTestCompileSourceRoot(anyString());

        filterSourcesMojo.addSourceFolderToProject(mock);
        assertEquals("called", placeholder.toString());
        assertThat(placeholder.toString()).isEqualTo("called");
        verify(mock, never()).addTestCompileSourceRoot(anyString());
    }
}
