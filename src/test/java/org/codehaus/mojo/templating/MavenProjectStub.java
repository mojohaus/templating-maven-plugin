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

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.model.Build;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.project.MavenProject;
import org.apache.maven.shared.utils.ReaderFactory;

/**
 * @author Krzysztof Suszyński <krzysztof.suszynski@wavesoftware.pl>
 * @since 2015-11-17
 */
public class MavenProjectStub
    extends MavenProject
{
    private final File basedir;

    public MavenProjectStub( File basedir )
    {
        this.basedir = basedir;
        initiate();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public File getBasedir()
    {
        return basedir;
    }

    private void initiate()
    {
        MavenXpp3Reader pomReader = new MavenXpp3Reader();
        Model model;
        try
        {
            model = pomReader.read( ReaderFactory.newXmlReader( new File( getBasedir(), "pom.xml" ) ) );
            setModel( model );
        }
        catch ( Exception e )
        {
            throw new RuntimeException( e );
        }

        setGroupId( model.getGroupId() );
        setArtifactId( model.getArtifactId() );
        setVersion( model.getVersion() );
        setName( model.getName() );
        setUrl( model.getUrl() );
        setPackaging( model.getPackaging() );

        Build build = new Build();
        build.setFinalName( model.getArtifactId() );
        build.setDirectory( getBasedir() + "/target" );
        build.setSourceDirectory( getBasedir() + "/src/main/java" );
        build.setOutputDirectory( getBasedir() + "/target/classes" );
        build.setTestSourceDirectory( getBasedir() + "/src/test/java" );
        build.setTestOutputDirectory( getBasedir() + "/target/test-classes" );
        setBuild( build );

        List<String> compileSourceRoots = new ArrayList<String>();
        compileSourceRoots.add( getBasedir() + "/src/main/java" );
        setCompileSourceRoots( compileSourceRoots );

        List<String> testCompileSourceRoots = new ArrayList<String>();
        testCompileSourceRoots.add( getBasedir() + "/src/test/java" );
        setTestCompileSourceRoots( testCompileSourceRoots );
    }

    public static MavenProject createProjectForITExample( String exampleName )
    {
        String load = exampleName + File.separator + "pom.xml";
        URL pomUrl = MavenProjectStub.class.getClassLoader().getResource( load );
        assert pomUrl != null : "Could not load: " + load;
        String pomPath = pomUrl.getPath();
        File pomFile = new File( pomPath );
        assertThat( pomFile ).exists().isFile();

        File baseDir = pomFile.getParentFile();
        assertThat( baseDir ).exists().isDirectory();
        return new MavenProjectStub( baseDir );
    }

}
