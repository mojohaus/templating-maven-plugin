package org.codehaus.mojo.templating;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;

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

/**
 * This mojo helps adding a filtered source folder in one go. This is typically useful if you want to use properties
 * coming from the POM inside parts of your source code that requires real constants, like annotations for example.
 */
public abstract class AbstractFilterSourcesMojo
    extends AbstractMojo
{
    protected abstract File getSourceDirectory();
    protected abstract File getOutputDirectory();

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

    @Component( hint = "default" )
    private MavenResourcesFiltering mavenResourcesFiltering;

    public void execute()
        throws MojoExecutionException
    {
        getLog().debug( "source=" + getSourceDirectory() + " target=" + getOutputDirectory() );

        // 1 Copy with filtering the given source to target dir
        List<Resource> resources = new ArrayList<Resource>();
        Resource resource = new Resource();
        resource.setFiltering( true );
        getLog().debug( getSourceDirectory().getAbsolutePath() );
        resource.setDirectory( getSourceDirectory().getAbsolutePath() );
        resources.add( resource );

        MavenResourcesExecution mavenResourcesExecution =
            new MavenResourcesExecution( resources, getOutputDirectory(), project, encoding, Collections.emptyList(),
                                         Collections.<String> emptyList(), session );
        mavenResourcesExecution.setInjectProjectBuildFilters( false );
        mavenResourcesExecution.setEscapeString( escapeString );
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

        try
        {
            mavenResourcesFiltering.filterResources( mavenResourcesExecution );
        }
        catch ( MavenFilteringException e )
        {
            throw new MojoExecutionException( e.getMessage(), e );
        }

        // 2 Add that dir to sources
        addSourceFolderToProject(this.project);
        
        if ( getLog().isInfoEnabled() )
        {
            getLog().info( "Source directory: " + getOutputDirectory() + " added." );
        }
    }
    protected abstract void addSourceFolderToProject( MavenProject mavenProject );
}
