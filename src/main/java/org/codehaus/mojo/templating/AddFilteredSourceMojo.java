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
 */
@Mojo(name = "add-filtered-source", defaultPhase = LifecyclePhase.GENERATE_SOURCES)
public class AddFilteredSourceMojo extends AbstractMojo
{
	@Parameter(defaultValue = "${basedir}/src/main/java-templates")
	private File templateSourceDirectory;

	@Parameter(defaultValue = "${project.build.directory}/javagenerated")
	private File targetGenerated;

	/**
	 * The character encoding scheme to be applied when filtering resources.
	 */
	@Parameter(defaultValue = "${project.build.sourceEncoding}")
	protected String encoding;

	@Parameter(defaultValue = "${session}", required = true, readonly = true)
	protected MavenSession session;

	@Parameter
	protected List<String> filters;

	@Parameter(defaultValue = "${project}", required = true, readonly = true)
	protected MavenProject project;

	@Parameter(defaultValue = "${project.build.filters}", readonly = true)
	protected List<String> buildFilters;

	@Component(hint = "default")
	protected MavenResourcesFiltering mavenResourcesFiltering;

	@Parameter(defaultValue = "true")
	protected boolean useBuildFilters;

	protected List<String> getCombinedFiltersList()
	{
		if (filters == null || filters.isEmpty())
		{
			return useBuildFilters ? buildFilters : null;
		}
		else
		{
			List<String> result = new ArrayList<String>();

			if (useBuildFilters && buildFilters != null && !buildFilters.isEmpty())
			{
				result.addAll(buildFilters);
			}

			result.addAll(filters);

			return result;
		}
	}

	public void execute() throws MojoExecutionException
	{
		System.out.println("source=" + templateSourceDirectory + " target=" + targetGenerated);
		// System.out.println("Nombre de filtres = " + getCombinedFiltersList().size());

		// 1 Copy with filtering the given source (default java-templates) to target dir
		List<Resource> resources = new ArrayList<Resource>();
		Resource resource = new Resource();
		resource.setFiltering(true);
		System.err.println(templateSourceDirectory);
		resource.setDirectory(templateSourceDirectory.getAbsolutePath());
		resources.add(resource);

		MavenResourcesExecution mavenResourcesExecution = new MavenResourcesExecution(resources,
			targetGenerated, project, encoding, Collections.emptyList(), Collections.EMPTY_LIST,
			session);
		mavenResourcesExecution.setInjectProjectBuildFilters(false);
		// mavenResourcesExecution.setUseDefaultFilterWrappers(true);

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
			System.err.println("XXXXXX Source directory: " + targetGenerated + " added.");
		}

	}
}
