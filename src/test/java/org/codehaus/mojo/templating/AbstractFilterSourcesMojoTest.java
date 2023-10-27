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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.shared.filtering.MavenFilteringException;
import org.apache.maven.shared.filtering.MavenResourcesExecution;
import org.apache.maven.shared.filtering.MavenResourcesFiltering;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;

/**
 * @author Krzysztof Suszyński <krzysztof.suszynski@wavesoftware.pl>
 * @since 2015-11-17
 */
@ExtendWith(MockitoExtension.class)
class AbstractFilterSourcesMojoTest
{
    @Spy
    private MavenProject project = MavenProjectStub.createProjectForITExample( "sample-simple" );

    @Mock
    private MavenSession session;

    @Mock
    private MavenResourcesFiltering mavenResourcesFiltering;

    @Spy
    private File sourceDirectory = resolve( project.getBasedir(), "src", "main", "java-templates" );

    @Spy
    private File outputDirectory = resolve( new File( "target" ), "generated-sources", "java-templates" );

    @InjectMocks
    private AbstractFilterSourcesMojo mojo = new FilterSourcesMojo();

    @BeforeEach
    void before() {
        File target = resolve( project.getBasedir(), outputDirectory.getPath() );
        FileUtils.deleteQuietly( target );
    }

    @Test
    void testGetOutputDirectory()
    {
        // when
        File file = mojo.getOutputDirectory();

        // then
        assertThat( file ).isNotNull();
        assertThat( file.getPath() ).contains( "generated-sources" );
    }

    @Test
    void testGetSourceDirectory()
    {
        // when
        File file = mojo.getSourceDirectory();

        // then
        assertThat( file ).isNotNull();
        assertThat( file ).exists();
        assertThat( file ).isDirectory();
        assertThat( file.getPath() ).contains( "src" );
        assertThat( file.getPath() ).contains( "main" );
    }

    @Test
    void testExecute() throws MojoExecutionException, MavenFilteringException
    {
        // given
        doAnswer( new MockCopyAnswer() ).when( mavenResourcesFiltering ).filterResources( any( MavenResourcesExecution.class ) );

        // when
        mojo.execute();
        assertThat( mojo.countCopiedFiles() ).isEqualTo( 1 );
        mojo.execute();
        assertThat( mojo.countCopiedFiles() ).isEqualTo( 0 );

        // then
        verify( mavenResourcesFiltering, times( 2 ) ).filterResources( any( MavenResourcesExecution.class ) );
        verify( project, times( 2 ) ).addCompileSourceRoot( outputDirectory.getAbsolutePath() );
    }

    private static class MockCopyAnswer
        implements Answer<Void>
    {

        public Void answer( InvocationOnMock invocation )
            throws Throwable
        {
            MavenResourcesExecution arg = (MavenResourcesExecution) invocation.getArguments()[0];
            File source = new File( arg.getResources().iterator().next().getDirectory() );
            assertThat( source ).exists();
            assertThat( source ).isDirectory();
            File destination = arg.getOutputDirectory();
            assertThat( destination ).doesNotExist();
            FileUtils.copyDirectory( source, destination );
            return null;
        }
    }

    private static File resolve( File file, String... paths )
    {
        StringBuilder sb = new StringBuilder( file.getPath() );
        for ( String path : paths )
        {
            sb.append( File.separator ).append( path );
        }
        return new File( sb.toString() );
    }
}
