package org.codehaus.mojo.templating;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.io.File;

import org.apache.maven.project.MavenProject;
import org.junit.Test;
import org.mockito.Mockito;

public class OkFilterSourcesMojoTest
{
    @Test
    public void testExistingDirectoryDoesNotAddSourceFolder()
    {
        final StringBuilder placeholder = new StringBuilder();
        FilterSourcesMojo filterSourcesMojo = new FilterSourcesMojo()
        {
            @Override
            protected void addSourceFolderToProject( MavenProject mavenProject )
            {
                placeholder.append( "called" );
            }
        };
        filterSourcesMojo.sourceDirectory = new File( "." );

        MavenProject mock = mock( MavenProject.class );
        Mockito.doThrow( IllegalArgumentException.class ).when( mock ).addCompileSourceRoot( anyString() );
        Mockito.doThrow( IllegalArgumentException.class ).when( mock ).addTestCompileSourceRoot( anyString() );

        filterSourcesMojo.addSourceFolderToProject( mock );

        assertThat( placeholder.toString() ).isEqualTo( "called" );
        verify( mock, never() ).addTestCompileSourceRoot( anyString() );
    }
}
