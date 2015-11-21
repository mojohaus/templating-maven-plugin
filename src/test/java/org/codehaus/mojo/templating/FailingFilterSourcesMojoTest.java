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
import java.util.Arrays;
import java.util.Collection;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.shared.filtering.MavenFilteringException;
import org.apache.maven.shared.filtering.MavenResourcesFiltering;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyZeroInteractions;

@RunWith( value = Parameterized.class )
// Let's play with Parameterized, I've been wanted to do that for quite a long time :-).
public class FailingFilterSourcesMojoTest
{
    @Parameter( 0 )
    public File failingParam;

    @Parameters
    public static Collection<File[]> getFailingParameters()
    {
        return Arrays.asList( new File[] { null }, new File[] { new File( "/non/existing/path/yodleyyyyeee" ) } );
    }

    @Test
    public void testBadDirectoryDoesNotAddSourceFolder()
        throws MojoExecutionException, MavenFilteringException
    {
        FilterSourcesMojo filterSourcesMojo = new FilterSourcesMojo()
        {
            @Override
            protected void addSourceFolderToProject( MavenProject mavenProject )
            {
                throw new IllegalArgumentException();
            }
        };
        filterSourcesMojo.sourceDirectory = failingParam;

        MavenResourcesFiltering mock = mock( MavenResourcesFiltering.class );
        filterSourcesMojo.mavenResourcesFiltering = mock;

        filterSourcesMojo.execute();
        verifyZeroInteractions( mock );
    }
}
