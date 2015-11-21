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

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.zip.CRC32;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.Resource;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apache.maven.shared.filtering.MavenFilteringException;
import org.apache.maven.shared.filtering.MavenResourcesExecution;
import org.apache.maven.shared.filtering.MavenResourcesFiltering;
import org.codehaus.plexus.util.FileUtils;
import org.sonatype.plexus.build.incremental.BuildContext;

/**
 * The base class for {@link FilterSourcesMojo} and {@link FilterTestSourcesMojo}
 */
public abstract class AbstractFilterSourcesMojo
    extends AbstractMojo
{
    private static final int CHECKSUM_BUFFER = 4096;

    private int copied = 0;

    /**
     * @return The location of the source directory.
     */
    protected abstract File getSourceDirectory();

    /**
     * @return The location of the output directory.
     */
    protected abstract File getOutputDirectory();

    @Component
    private BuildContext buildContext;

    /**
     * The character encoding scheme to be applied when filtering resources.
     */
    @Parameter( defaultValue = "${project.build.sourceEncoding}" )
    private String encoding;

    /**
     * Expression preceded with the String won't be interpolated \${foo} will be replaced with ${foo}
     */
    @Parameter( property = "maven.resources.escapeString" )
    protected String escapeString;

    /**
     * Set of delimiters for expressions to filter within the resources. These delimiters are specified in the form
     * 'beginToken*endToken'. If no '*' is given, the delimiter is assumed to be the same for start and end. So, the
     * default filtering delimiters might be specified as:
     * 
     * <pre>
     * &lt;delimiters&gt;
     *   &lt;delimiter&gt;${*}&lt;/delimiter&gt;
     *   &lt;delimiter&gt;@&lt;/delimiter&gt;
     * &lt;/delimiters&gt;
     * </pre>
     * 
     * Since the '@' delimiter is the same on both ends, we don't need to specify '@*@' (though we can).
     */
    @Parameter
    protected List<String> delimiters;

    /**
     * Controls whether the default delimiters are included in addition to those configured {@link #delimiters}. Does
     * not have any effect if {@link #delimiters} is empty when the defaults will be included anyway.
     */
    @Parameter( defaultValue = "true" )
    protected boolean useDefaultDelimiters;

    @Parameter( defaultValue = "${session}", required = true, readonly = true )
    private MavenSession session;

    @Parameter( defaultValue = "${project}", required = true, readonly = true )
    private MavenProject project;

    /**
     * Controls whether to overwrite files that are not changed, by default files will not be overwritten
     */
    @Parameter( defaultValue = "false" )
    protected boolean overwrite;

    /**
     * Skips POM projects if set to true, which is the default option.
     */
    @Parameter( defaultValue = "true" )
    protected boolean skipPoms;

    /**
     * The resources filtering which is used.
     */
    @Component( hint = "default" )
    protected MavenResourcesFiltering mavenResourcesFiltering;

    /** {@inheritDoc} */
    public void execute()
        throws MojoExecutionException
    {
        File sourceDirectory = getSourceDirectory();
        if ( !preconditionsFulfilled( sourceDirectory ) )
        {
            return;
        }
        buildContext.removeMessages( sourceDirectory );

        // 1 Copy with filtering the given source to temporary dir
        copied = 0;
        File temporaryDirectory = getTemporaryDirectory( sourceDirectory );
        logInfo( "Coping files with filtering to temporary directory." );
        logDebug( "Temporary director for filtering is: %s", temporaryDirectory );
        filterSourceToTemporaryDir( sourceDirectory, temporaryDirectory );
        // 2 Copy if needed
        copyDirectoryStructure( temporaryDirectory, getOutputDirectory() );
        cleanupTemporaryDirectory( temporaryDirectory );
        if ( isSomethingBeenUpdated() )
        {
            buildContext.refresh( getOutputDirectory() );
            logInfo( "Copied %d files to output directory: %s", copied, getOutputDirectory() );
        }
        else
        {
            logInfo( "No files needs to be copied to output directory. Up to date: %s", getOutputDirectory() );
        }

        // 3 Add that dir to sources
        addSourceFolderToProject( this.project );
        logInfo( "Source directory: %s added.", getOutputDirectory() );
    }

    /**
     * @return number of copied files.
     */
    protected int countCopiedFiles()
    {
        return copied;
    }

    private void logInfo( String format, Object... args )
    {
        if ( getLog().isInfoEnabled() )
        {
            getLog().info( String.format( format, args ) );
        }
    }

    private void logDebug( String format, Object... args )
    {
        if ( getLog().isDebugEnabled() )
        {
            getLog().debug( String.format( format, args ) );
        }
    }

    private boolean isSomethingBeenUpdated()
    {
        return copied > 0;
    }

    private void cleanupTemporaryDirectory( File temporaryDirectory )
        throws MojoExecutionException
    {
        try
        {
            FileUtils.forceDelete( temporaryDirectory );
        }
        catch ( IOException ex )
        {
            throw new MojoExecutionException( ex.getMessage(), ex );
        }
    }

    private void filterSourceToTemporaryDir( final File sourceDirectory, final File temporaryDirectory )
        throws MojoExecutionException
    {
        List<Resource> resources = new ArrayList<Resource>();
        Resource resource = new Resource();
        resource.setFiltering( true );
        logDebug( "Source absolute path: %s", sourceDirectory.getAbsolutePath() );
        resource.setDirectory( sourceDirectory.getAbsolutePath() );
        resources.add( resource );

        MavenResourcesExecution mavenResourcesExecution =
            new MavenResourcesExecution( resources, temporaryDirectory, project, encoding,
                                         Collections.<String>emptyList(), Collections.<String>emptyList(), session );
        mavenResourcesExecution.setInjectProjectBuildFilters( true );
        mavenResourcesExecution.setEscapeString( escapeString );
        mavenResourcesExecution.setOverwrite( overwrite );
        setDelimitersForExecution( mavenResourcesExecution );
        try
        {
            mavenResourcesFiltering.filterResources( mavenResourcesExecution );
        }
        catch ( MavenFilteringException e )
        {
            buildContext.addMessage( getSourceDirectory(), 1, 1, "Filtering Exception", BuildContext.SEVERITY_ERROR,
                                     e );
            throw new MojoExecutionException( e.getMessage(), e );
        }
    }

    private void setDelimitersForExecution( MavenResourcesExecution mavenResourcesExecution )
    {
        // if these are NOT set, just use the defaults, which are '${*}' and '@'.
        if ( delimiters != null && !delimiters.isEmpty() )
        {
            LinkedHashSet<String> delims = new LinkedHashSet<String>();
            if ( useDefaultDelimiters )
            {
                delims.addAll( mavenResourcesExecution.getDelimiters() );
            }

            for ( String delim : delimiters )
            {
                if ( delim == null )
                {
                    // FIXME: ${filter:*} could also trigger this condition. Need a better long-term solution.
                    delims.add( "${*}" );
                }
                else
                {
                    delims.add( delim );
                }
            }

            mavenResourcesExecution.setDelimiters( delims );
        }
    }

    private void preconditionsCopyDirectoryStructure( final File sourceDirectory, final File destinationDirectory,
                                                      final File rootDestinationDirectory )
                                                          throws IOException
    {
        if ( sourceDirectory == null )
        {
            throw new IOException( "source directory can't be null." );
        }

        if ( destinationDirectory == null )
        {
            throw new IOException( "destination directory can't be null." );
        }

        if ( sourceDirectory.equals( destinationDirectory ) )
        {
            throw new IOException( "source and destination are the same directory." );
        }

        if ( !sourceDirectory.exists() )
        {
            throw new IOException( "Source directory doesn't exists (" + sourceDirectory.getAbsolutePath() + ")." );
        }
    }

    private void copyDirectoryStructure( final File sourceDirectory, final File destinationDirectory )
        throws MojoExecutionException
    {
        try
        {
            File target = destinationDirectory;
            if ( !target.isAbsolute() )
            {
                target = resolve( project.getBasedir(), destinationDirectory.getPath() );
            }
            copyDirectoryStructureWithIO( sourceDirectory, target, target );
        }
        catch ( IOException ex )
        {
            throw new MojoExecutionException( ex.getMessage(), ex );
        }
    }

    private void copyDirectoryStructureWithIO( final File sourceDirectory, final File destinationDirectory,
                                               final File rootDestinationDirectory )
                                                   throws IOException
    {
        preconditionsCopyDirectoryStructure( sourceDirectory, destinationDirectory, rootDestinationDirectory );
        File[] files = sourceDirectory.listFiles();
        if ( files == null )
        {
            return;
        }
        String sourcePath = sourceDirectory.getAbsolutePath();

        for ( File file : files )
        {
            if ( file.equals( rootDestinationDirectory ) )
            {
                // We don't copy the destination directory in itself
                continue;
            }

            String dest = file.getAbsolutePath();

            dest = dest.substring( sourcePath.length() + 1 );

            File destination = new File( destinationDirectory, dest );

            if ( file.isFile() )
            {
                destination = destination.getParentFile();

                if ( isFileDifferent( file, destination ) )
                {
                    copied++;
                    FileUtils.copyFileToDirectory( file, destination );
                }
            }
            else if ( file.isDirectory() )
            {
                if ( !destination.exists() && !destination.mkdirs() )
                {
                    throw new IOException( "Could not create destination directory '" + destination.getAbsolutePath()
                        + "'." );
                }

                copyDirectoryStructureWithIO( file, destination, rootDestinationDirectory );
            }
            else
            {
                throw new IOException( "Unknown file type: " + file.getAbsolutePath() );
            }
        }
    }

    private File resolve( final File file, final String... subfile )
    {
        StringBuilder path = new StringBuilder();
        path.append( file.getPath() );
        for ( String fi : subfile )
        {
            path.append( File.separator );
            path.append( fi );
        }
        return new File( path.toString() );
    }

    private boolean isFileDifferent( final File file, final File directory )
        throws IOException
    {
        File targetFile = resolve( directory, file.getName() ).getAbsoluteFile();
        return !targetFile.canRead() || getCrc32OfFile( file ) != getCrc32OfFile( targetFile );
    }

    private long getCrc32OfFile( final File target )
        throws IOException
    {
        FileInputStream fis = null;
        try
        {
            fis = new FileInputStream( target );
            CRC32 crcMaker = new CRC32();
            byte[] buffer = new byte[CHECKSUM_BUFFER];
            int bytesRead;
            while ( ( bytesRead = fis.read( buffer ) ) != -1 )
            {
                crcMaker.update( buffer, 0, bytesRead );
            }
            return crcMaker.getValue();
        }
        catch ( FileNotFoundException ex )
        {
            close( fis );
            throw new IOException( ex.getLocalizedMessage(), ex );
        }
        finally
        {
            close( fis );
        }
    }

    private void close( Closeable is )
        throws IOException
    {
        if ( is != null )
        {
            is.close();
        }
    }

    private File getTemporaryDirectory( File sourceDirectory )
        throws MojoExecutionException
    {
        File basedir = project.getBasedir();
        File target = new File( project.getBuild().getDirectory() );
        StringBuilder label = new StringBuilder( "templates-tmp" );
        CRC32 crcMaker = new CRC32();
        crcMaker.update( sourceDirectory.getPath().getBytes() );
        label.append( crcMaker.getValue() );
        String subfile = label.toString();
        return target.isAbsolute() ? resolve( target, subfile ) : resolve( basedir, target.getPath(), subfile );
    }

    private boolean preconditionsFulfilled( File sourceDirectory )
    {
        if ( skipPoms && "pom".equals( project.getPackaging() ) )
        {
            logInfo( "Skipping a POM project type. Change a `skipPoms` to false to run anyway." );
            return false;
        }
        logDebug( "source=%s target=%s", sourceDirectory, getOutputDirectory() );
        if ( !( sourceDirectory != null && sourceDirectory.exists() ) )
        {
            logInfo( "Request to add '%s' folder. Not added since it does not exist.", sourceDirectory );
            return false;
        }
        return true;
    }

    /**
     * @param mavenProject {@link MavenProject}
     */
    protected abstract void addSourceFolderToProject( MavenProject mavenProject );
}
