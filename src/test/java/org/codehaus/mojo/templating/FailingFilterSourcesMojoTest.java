package org.codehaus.mojo.templating;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyZeroInteractions;

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
