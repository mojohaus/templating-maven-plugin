/*
 * Copyright MojoHaus and Contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.codehaus.mojo.templating;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.project.MavenProject;
import org.apache.maven.shared.filtering.DefaultMavenFileFilter;
import org.apache.maven.shared.filtering.DefaultMavenResourcesFiltering;
import org.junit.jupiter.api.Test;
import org.sonatype.plexus.build.incremental.DefaultBuildContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;

/**
 * Runs the goals with the real maven-filtering on the projects under {@code src/test/resources} and checks the
 * generated sources. That the generated directories become source roots and compile is covered by the
 * {@code filter-main} and {@code filter-tests} integration tests.
 */
class FilteringTest {

    @Test
    void buildFiltersFillTheTemplates() throws Exception {
        MavenProject project = project("build-filters");

        run(new FilterSourcesMojo(), project, true);

        String colors = generated(project, "generated-sources", "org/codehaus/mojo/templating/it/Colors.java");
        assertThat(colors)
                .contains("public static final String FIRST_COLOR = \"red\"")
                .contains("public static final String SECOND_COLOR = \"green\"")
                .contains("public static final String THIRD_COLOR = \"blue\"");
    }

    @Test
    void bothDelimitersFillConstantsAndAnnotations() throws Exception {
        MavenProject project = project("filter-annotations");

        run(new FilterSourcesMojo(), project, true);

        assertThat(generated(project, "generated-sources", "pkg/SomeClass.java"))
                .contains("VERSION = \"<1.0-specialversion>\"")
                .contains("@Disabled(\"coquinou\")");
        assertThat(generated(project, "generated-sources", "pkg/A.java"))
                .contains("@Disabled(\"coquinou\")")
                .contains("FILTER_A=\"coquinou\"")
                .contains("FILTER_B=\"coquinou\"");
        assertThat(generated(project, "generated-sources", "pkg/B.java"))
                .contains("FILTER_A=\"coquinou\"")
                .contains("FILTER_B=\"coquinou\"");
        // the annotation value concatenates to "coquinoucoquinoucoquinoucoquinou coquinoucoquinou"
        assertThat(generated(project, "generated-sources", "pkg/C.java"))
                .containsSubsequence("\"coquinou\" +", "\"coquinou\" +", "\"coquinoucoquinou coquinoucoquinou\"");
    }

    @Test
    void pluginFiltersAddToBuildFilters() throws Exception {
        MavenProject project = project("plugin-filters");

        AbstractFilterSourcesMojo mojo = new FilterSourcesMojo();
        mojo.filters = Arrays.asList("src/filters/template.properties");
        run(mojo, project, true);

        assertThat(generated(project, "generated-sources", "demo/MainValues.java"))
                .contains("NUMBER = \"4711\"")
                .contains("LABEL = \"from the plugin filter\"");
    }

    @Test
    void useBuildFiltersFalseLeavesOnlyPluginFilters() throws Exception {
        MavenProject project = project("plugin-filters");

        AbstractFilterSourcesMojo mojo = new FilterTestSourcesMojo();
        mojo.filters = Arrays.asList("src/filters/template.properties");
        run(mojo, project, false);

        assertThat(generated(project, "generated-test-sources", "demo/TestValues.java"))
                .contains("LABEL = \"from the plugin filter\"")
                .contains("NUMBER = \"${build.number}\"");
    }

    private static MavenProject project(String name) throws IOException {
        MavenProject project = MavenProjectStub.createProjectForITExample(name);
        FileUtils.deleteDirectory(new File(project.getBuild().getDirectory()));
        return project;
    }

    private static void run(AbstractFilterSourcesMojo mojo, MavenProject project, boolean useBuildFilters)
            throws Exception {
        MavenSession session = mock(MavenSession.class);
        lenient().when(session.getSystemProperties()).thenReturn(new Properties());
        lenient().when(session.getUserProperties()).thenReturn(new Properties());

        DefaultBuildContext buildContext = new DefaultBuildContext();
        mojo.mavenResourcesFiltering =
                new DefaultMavenResourcesFiltering(new DefaultMavenFileFilter(buildContext), buildContext);
        mojo.useBuildFilters = useBuildFilters;
        mojo.useDefaultDelimiters = true;
        set(mojo, "project", project);
        set(mojo, "session", session);
        set(mojo, "encoding", "UTF-8");

        boolean main = mojo instanceof FilterSourcesMojo;
        File basedir = project.getBasedir();
        String target = project.getBuild().getDirectory();
        set(
                mojo,
                main ? "sourceDirectory" : "testSourceDirectory",
                new File(basedir, main ? "src/main/java-templates" : "src/test/java-templates"));
        set(
                mojo,
                main ? "outputDirectory" : "testOutputDirectory",
                new File(target, main ? "generated-sources/java-templates" : "generated-test-sources/java-templates"));

        mojo.execute();
    }

    private static String generated(MavenProject project, String root, String path) throws IOException {
        File file = new File(project.getBuild().getDirectory(), root + "/java-templates/" + path);
        assertThat(file).isFile();
        return new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
    }

    private static void set(Object target, String name, Object value) throws ReflectiveOperationException {
        for (Class<?> type = target.getClass(); type != null; type = type.getSuperclass()) {
            try {
                Field field = type.getDeclaredField(name);
                field.setAccessible(true);
                field.set(target, value);
                return;
            } catch (NoSuchFieldException e) {
                // look in the superclass
            }
        }
        throw new NoSuchFieldException(name);
    }
}
