# Agent guide for templating-maven-plugin

This file is for automated agents and human contributors working on this repository. It records
what the code and the POMs do not make obvious.

## Layout

- One module with two goals in `org.codehaus.mojo.templating`: `filter-sources`
  (`FilterSourcesMojo`, bound to `generate-sources`) and `filter-test-sources`
  (`FilterTestSourcesMojo`, bound to `generate-test-sources`). Both extend
  `AbstractFilterSourcesMojo`, which filters the template directory with maven-filtering and adds
  the result as a source root. The defaults are `src/main/java-templates` to
  `target/generated-sources/java-templates`, and `src/test/java-templates` to
  `target/generated-test-sources/java-templates`.
- The site is built from `src/site` (`apt` pages, `apt/examples` and the FAQ in `fml`).

## Build gates

- On a local build Spotless applies the formatting; it also reformats Markdown, so run
  `mvn spotless:apply` after editing the README or this file.
- The parent, `mojo-parent`, sets `maven.compiler.release` to 8, so main code must use the Java 8
  API.
- The plugin compiles against Maven 3.9.16 (`mavenVersion`) and declares a literal
  `<prerequisites><maven>3.9.0</maven>`; keep the two apart, so raising `mavenVersion` does not
  raise the prerequisite.

## Unit tests

- JUnit 5 with Mockito (`MockitoExtension`) and AssertJ. `MavenProjectStub.createProjectForITExample`
  builds a project from a POM under `src/test/resources`, such as `sample-simple`; the mojo is
  wired with `@InjectMocks` and a mocked `MavenResourcesFiltering`, so no Maven build runs.

## Integration tests

- They live in `src/it`, one directory per test, and run with maven-invoker-plugin. The `run-its`
  profile is active unless `-DskipTests=true` is set, so a plain `mvn verify` runs all of them. The
  plugin is installed into `target/local-repo` first, and `src/it/settings.xml` points the forked
  builds at it.
- Run a subset:

  ```shell
  mvn -B -P run-its verify -Dinvoker.test=MOJO-2014,filter-main
  ```

  The tests have no `setup-*` projects.

- Write `verify.groovy` as `assert` statements. Invoker fails a script whose last expression is
  anything other than `true` or `null`, so a trailing `list.each { }` fails the test; use a `for`
  loop.

## Dependencies

- Deliberate pins in `.github/dependabot.yml`: `maven-core` and `maven-plugin-api` stay below
  3.10.0, because the plugin baseline is the Maven 3.9.x line.

## Continuous integration and pull requests

- `.github/workflows/maven.yml` calls the shared `maven-verify.yml@v5` workflow from
  `apache/maven-gh-actions-shared`. Branches in this repository build under the push event, so look
  at the push run for a pull request from a same-repository branch.
- Pull requests are squash-, merge- or rebase-merged. Release Drafter builds the release notes from
  the titles and labels, so the title should describe the change for a user.

