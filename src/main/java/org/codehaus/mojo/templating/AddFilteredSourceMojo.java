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
import java.util.List;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.Resource;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apache.maven.shared.filtering.MavenFilteringException;
import org.apache.maven.shared.filtering.MavenResourcesExecution;
import org.apache.maven.shared.filtering.MavenResourcesFiltering;

/**
 * This mojo helps adding a filtered source folder in one go. This is typically useful if you want
 * to use properties coming from the POM inside parts of your source code that requires real
 * constants, like annotations for example.
 */
@Mojo(name = "add-filtered-source", defaultPhase = LifecyclePhase.GENERATE_SOURCES)
public class AddFilteredSourceMojo extends AbstractMojo
{
	/**
	 * Source directory that will be first filtered and then added as a classical source folder.
	 */
	@Parameter(defaultValue = "${basedir}/src/main/java-templates")
	private File templateSourceDirectory;

	/**
	 * Target folder where filtered sources will land.
	 */
	@Parameter(defaultValue = "${project.build.directory}/generated-sources/java-templates")
	private File targetGenerated;

	/**
	 * The character encoding scheme to be applied when filtering resources.
	 */
	@Parameter(defaultValue = "${project.build.sourceEncoding}")
	private String encoding;

	@Parameter(defaultValue = "${session}", required = true, readonly = true)
	private MavenSession session;

	@Parameter(defaultValue = "${project}", required = true, readonly = true)
	private MavenProject project;

	@Component(hint = "default")
	private MavenResourcesFiltering mavenResourcesFiltering;

	public void execute() throws MojoExecutionException
	{
		getLog().debug("source=" + templateSourceDirectory + " target=" + targetGenerated);

		// 1 Copy with filtering the given source to target dir
		List<Resource> resources = new ArrayList<Resource>();
		Resource resource = new Resource();
		resource.setFiltering(true);
		getLog().debug(templateSourceDirectory.getAbsolutePath());
		resource.setDirectory(templateSourceDirectory.getAbsolutePath());
		resources.add(resource);

		MavenResourcesExecution mavenResourcesExecution = new MavenResourcesExecution(resources,
			targetGenerated, project, encoding, Collections.emptyList(),
			Collections.<String> emptyList(), session);
		mavenResourcesExecution.setInjectProjectBuildFilters(false);

		try
		{
			mavenResourcesFiltering.filterResources(mavenResourcesExecution);
		}
		catch (MavenFilteringException e)
		{
			throw new MojoExecutionException(e.getMessage(), e);
		}

		// 2 Add that dir to sources
		this.project.addCompileSourceRoot(targetGenerated.getAbsolutePath());
		if (getLog().isInfoEnabled())
		{
			getLog().info("Source directory: " + targetGenerated + " added.");
		}
	}
}
