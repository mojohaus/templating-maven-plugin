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

import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

/**
 * This mojo helps adding a filtered source folder in one go. This is typically useful if you want to use properties
 * coming from the POM inside parts of your test source code that requires real constants, like annotations for example.
 */
@Mojo( name = "filter-test-sources", defaultPhase = LifecyclePhase.GENERATE_TEST_SOURCES, threadSafe = true )
public class FilterTestSourcesMojo
    extends AbstractFilterSourcesMojo
{
    /**
     * Source directory that will be first filtered and then added as a classical source folder.
     */
    @Parameter( defaultValue = "${basedir}/src/test/java-templates" )
    private File testSourceDirectory;

    /**
     * Output folder where filtered test sources will land.
     */
    @Parameter( defaultValue = "${project.build.directory}/generated-test-sources/java-templates" )
    private File testOutputDirectory;

    @Override
    protected File getSourceDirectory()
    {
        return testSourceDirectory;
    }

    @Override
    protected File getOutputDirectory()
    {
        return testOutputDirectory;
    }

    @Override
    protected void addSourceFolderToProject( MavenProject mavenProject )
    {
        mavenProject.addTestCompileSourceRoot( getOutputDirectory().getAbsolutePath() );
    }
}
