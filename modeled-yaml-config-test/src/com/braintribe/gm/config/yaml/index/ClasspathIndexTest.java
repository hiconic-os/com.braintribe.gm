package com.braintribe.gm.config.yaml.index;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.braintribe.gm.config.yaml.index.ClasspathIndex.FilesystemMapping;

/**
 * Tests for {@link ClasspathIndex}
 *
 * @author peter.gazdik
 */
public class ClasspathIndexTest {

	private final ClasspathIndex classpathIndex = new ClasspathIndex();

	@Rule
	public TemporaryFolder temporaryFolder = new TemporaryFolder();

	/** The folder of the current test, created by {@link #newProject(String)}. Every {@link #writeFile(String, String)} is relative to it. */
	private Path project;

	@Test
	public void findAll() throws Exception {
		List<ClasspathEntry> entries = classpathIndex.all();

		assertContainsAll(entries);
	}

	private void assertContainsAll(List<ClasspathEntry> entries) {
		assertThat(pathsOf(entries)) //
				.containsExactlyInAnyOrder( //
						"simple-entity.1.yaml", //
						"simple-entity.3.yaml", //
						"simple-entity.disambig-2.yaml", //
						"simple-entity.yaml", //
						"simple-entity~use-case.disambig.yaml", //
						"simple-entity~use-case.yaml", //
						"loaded-entity.yaml" //
				);
	}

	@Test
	public void findNoneByInvalidPrefix() throws Exception {
		List<ClasspathEntry> entries = classpathIndex.forPrefix("bs-prefix");

		assertThat(entries).isEmpty();
	}

	@Test
	public void loadsArtifactScopedFilesystemMirror() throws Exception {
		newProject("classpath-resources");
		writeFile("example-configuration-1.0/HICONIC-CONF/example.yaml", "example: true\n");
		writeFile("example-configuration-1.0/META-INF/classpath-index.txt", "# preserved comment\nHICONIC-CONF/example.yaml\n");
		writeFile("example-configuration-1.0/META-INF/classpath-origin.properties", "artifactId=example-configuration\n");

		List<ClasspathEntry> entries = new ClasspathIndex(project).all();

		assertThat(entries).hasSize(1);
		assertThat(entries.get(0).path).isEqualTo("HICONIC-CONF/example.yaml");
		assertThat(entries.get(0).artifactId).isEqualTo("example-configuration");
		assertThat(entries.get(0).url.getProtocol()).isEqualTo("file");
	}

	@Test
	public void loadsLegacyWindowsClasspathIndex() throws Exception {
		Path archive = temporaryFolder.newFile("windows-index.jar").toPath();
		try (ZipOutputStream out = new ZipOutputStream(Files.newOutputStream(archive))) {
			writeZipEntry(out, "META-INF/classpath-index.txt", "HICONIC-CONF\\example.yaml\n");
			writeZipEntry(out, "HICONIC-CONF/example.yaml", "example: true\n");
		}

		try (URLClassLoader classLoader = new URLClassLoader(new URL[] { archive.toUri().toURL() }, null)) {
			List<ClasspathEntry> entries = new ClasspathIndex(classLoader).all();

			assertThat(entries).hasSize(1);
			assertThat(entries.get(0).path).isEqualTo("HICONIC-CONF/example.yaml");
		}
	}

	@Test
	public void infersArtifactIdFromEclipseProjectOutput() throws Exception {
		newProject("example-configuration");
		writeFile("classes/HICONIC-CONF/example-configuration.yaml", "example: true\n");
		writeFile("classes/META-INF/classpath-index.txt", "HICONIC-CONF/example-configuration.yaml\n");

		try (URLClassLoader classLoader = classLoaderFor("classes")) {
			List<ClasspathEntry> entries = new ClasspathIndex(classLoader).all();

			assertThat(entries).hasSize(1);
			assertThat(entries.get(0).artifactId).isEqualTo("example-configuration");
		}
	}

	@Test
	public void infersArtifactIdFromGradleResourcesOutput() throws Exception {
		newProject("gradle-configuration");
		writeFile("build/resources/main/HICONIC-CONF/gradle-configuration.yaml", "example: true\n");
		writeFile("build/resources/main/META-INF/classpath-index.txt", "HICONIC-CONF/gradle-configuration.yaml\n");

		try (URLClassLoader classLoader = classLoaderFor("build/resources/main")) {
			List<ClasspathEntry> entries = new ClasspathIndex(classLoader).all();

			assertThat(entries).hasSize(1);
			assertThat(entries.get(0).artifactId).isEqualTo("gradle-configuration");
		}
	}

	@Test
	public void infersArtifactIdOfDeclarationFromGradleResourcesOutput() throws Exception {
		newProject("gradle-declared-configuration");
		writeFile("build/resources/main/META-INF/classpath-resources.txt", "HICONIC-CONF\n");
		writeFile("build/resources/main/HICONIC-CONF/gradle-declared-configuration.yaml", "example: true\n");

		try (URLClassLoader classLoader = classLoaderFor("build/resources/main")) {
			List<ClasspathEntry> entries = new ClasspathIndex(classLoader).all();

			assertThat(entries).hasSize(1);
			assertThat(entries.get(0).artifactId).isEqualTo("gradle-declared-configuration");
		}
	}

	@Test
	public void takesArtifactIdOfJarFromArtifactDescriptor() throws Exception {
		newProject("described-jar");
		Path archive = project.resolve("renamed-1.0-pc.jar");
		try (ZipOutputStream out = new ZipOutputStream(Files.newOutputStream(archive))) {
			writeZipEntry(out, "META-INF/artifact-descriptor.properties", "groupId=example\nartifactId=described-configuration\nversion=1.0-pc\n");
			writeZipEntry(out, "META-INF/classpath-index.txt", "HICONIC-CONF/example.yaml\n");
			writeZipEntry(out, "HICONIC-CONF/example.yaml", "example: true\n");
		}

		try (URLClassLoader classLoader = new URLClassLoader(new URL[] { archive.toUri().toURL() }, null)) {
			List<ClasspathEntry> entries = new ClasspathIndex(classLoader).all();

			assertThat(entries).hasSize(1);
			assertThat(entries.get(0).artifactId).isEqualTo("described-configuration");
		}
	}

	@Test
	public void takesArtifactIdOfIndexedFolderFromArtifactDescriptor() throws Exception {
		newProject("renamed-folder");
		writeFile("classes/META-INF/artifact-descriptor.properties", "artifactId=described-configuration\n");
		writeFile("classes/META-INF/classpath-index.txt", "HICONIC-CONF/example.yaml\n");
		writeFile("classes/HICONIC-CONF/example.yaml", "example: true\n");

		try (URLClassLoader classLoader = classLoaderFor("classes")) {
			List<ClasspathEntry> entries = new ClasspathIndex(classLoader).all();

			assertThat(entries).hasSize(1);
			assertThat(entries.get(0).artifactId).isEqualTo("described-configuration");
		}
	}

	@Test
	public void takesArtifactIdOfDeclaredFolderFromArtifactDescriptor() throws Exception {
		newProject("renamed-folder");
		writeFile("classes/META-INF/artifact-descriptor.properties", "artifactId=described-configuration\n");
		writeFile("classes/META-INF/classpath-resources.txt", "HICONIC-CONF\n");
		writeFile("classes/HICONIC-CONF/example.yaml", "example: true\n");

		try (URLClassLoader classLoader = classLoaderFor("classes")) {
			List<ClasspathEntry> entries = new ClasspathIndex(classLoader).all();

			assertThat(entries).hasSize(1);
			assertThat(entries.get(0).artifactId).isEqualTo("described-configuration");
		}
	}

	@Test
	public void takesGroupIdOfJarFromArtifactDescriptor() throws Exception {
		newProject("described-jar");
		Path archive = project.resolve("renamed-1.0-pc.jar");
		try (ZipOutputStream out = new ZipOutputStream(Files.newOutputStream(archive))) {
			writeZipEntry(out, "META-INF/artifact-descriptor.properties", "groupId=example\nartifactId=described-configuration\nversion=1.0-pc\n");
			writeZipEntry(out, "META-INF/classpath-index.txt", "HICONIC-CONF/example.yaml\n");
			writeZipEntry(out, "HICONIC-CONF/example.yaml", "example: true\n");
		}

		try (URLClassLoader classLoader = new URLClassLoader(new URL[] { archive.toUri().toURL() }, null)) {
			List<ClasspathEntry> entries = new ClasspathIndex(classLoader).all();

			assertThat(entries).hasSize(1);
			assertThat(entries.get(0).groupId).isEqualTo("example");
		}
	}

	@Test
	public void takesGroupIdOfDeclaredFolderFromArtifactDescriptor() throws Exception {
		newProject("renamed-folder");
		writeFile("classes/META-INF/artifact-descriptor.properties", "groupId=example\nartifactId=described-configuration\n");
		writeFile("classes/META-INF/classpath-resources.txt", "HICONIC-CONF\n");
		writeFile("classes/HICONIC-CONF/example.yaml", "example: true\n");

		try (URLClassLoader classLoader = classLoaderFor("classes")) {
			List<ClasspathEntry> entries = new ClasspathIndex(classLoader).all();

			assertThat(entries).hasSize(1);
			assertThat(entries.get(0).groupId).isEqualTo("example");
			assertThat(entries.get(0).artifactId).isEqualTo("described-configuration");
		}
	}

	/** Only the descriptor knows the groupId. The artifactId is still derived from the project folder. */
	@Test
	public void leavesGroupIdEmptyWithoutArtifactDescriptor() throws Exception {
		newProject("undescribed-configuration");
		writeFile("classes/META-INF/classpath-resources.txt", "HICONIC-CONF\n");
		writeFile("classes/HICONIC-CONF/example.yaml", "example: true\n");

		try (URLClassLoader classLoader = classLoaderFor("classes")) {
			List<ClasspathEntry> entries = new ClasspathIndex(classLoader).all();

			assertThat(entries).hasSize(1);
			assertThat(entries.get(0).groupId).isEmpty();
			assertThat(entries.get(0).artifactId).isEqualTo("undescribed-configuration");
		}
	}

	@Test
	public void expandsDeclaredClasspathResources() throws Exception {
		newProject("declared-configuration");
		writeFile("classes/META-INF/classpath-resources.txt", "# declared entries\n\nHICONIC-CONF\nnotes.txt\n");
		writeFile("classes/HICONIC-CONF/database-configuration.yaml", "name: auth\n");
		writeFile("classes/HICONIC-CONF/nested/extra.yaml", "extra: true\n");
		writeFile("classes/notes.txt", "hello\n");
		writeFile("classes/HICONIC-RESOURCES/hidden.txt", "hidden\n");
		writeFile("classes/META-INF/artifact-descriptor.properties", "artifactId=declared-configuration\n");
		writeFile("classes/.gitignore", "/classes\n");
		writeFile("classes/example/Generated.class", "\0");

		try (URLClassLoader classLoader = classLoaderFor("classes")) {
			List<ClasspathEntry> entries = new ClasspathIndex(classLoader).all();

			assertThat(pathsOf(entries)).containsExactlyInAnyOrder(
					"HICONIC-CONF/database-configuration.yaml",
					"HICONIC-CONF/nested/extra.yaml",
					"notes.txt");
			assertThat(entries).allMatch(entry -> entry.artifactId.equals("declared-configuration"));
		}
	}

	@Test
	public void skipsUndeclaredClasspathRoot() throws Exception {
		newProject("undeclared-library");
		writeFile("classes/HICONIC-CONF/stray.yaml", "stray: true\n");
		writeFile("classes/.gitignore", "/classes\n");

		try (URLClassLoader classLoader = classLoaderFor("classes")) {
			assertThat(new ClasspathIndex(classLoader).all()).isEmpty();
		}
	}

	@Test
	public void ignoresMissingDeclaredEntry() throws Exception {
		newProject("partly-declared-configuration");
		writeFile("classes/META-INF/classpath-resources.txt", "HICONIC-CONF\nHICONIC-RESOURCES\n");
		writeFile("classes/HICONIC-CONF/present.yaml", "present: true\n");

		try (URLClassLoader classLoader = classLoaderFor("classes")) {
			assertThat(pathsOf(new ClasspathIndex(classLoader).all())).containsExactly("HICONIC-CONF/present.yaml");
		}
	}

	@Test
	public void doesNotScanUnindexedFilesBesideExistingExplodedIndex() throws Exception {
		newProject("indexed-exploded-configuration");
		writeFile("classes/HICONIC-CONF/indexed.yaml", "indexed: true\n");
		writeFile("classes/HICONIC-CONF/unindexed.yaml", "indexed: false\n");
		writeFile("classes/META-INF/classpath-index.txt", "HICONIC-CONF/indexed.yaml\n");

		try (URLClassLoader classLoader = classLoaderFor("classes")) {
			assertThat(pathsOf(new ClasspathIndex(classLoader).all())).containsExactly("HICONIC-CONF/indexed.yaml");
		}
	}

	@Test
	public void loadsLegacyWindowsFilesystemIndex() throws Exception {
		newProject("windows-filesystem-index");
		writeFilesystemArtifact("example-configuration-1.0", "example-configuration", "HICONIC-CONF\\example.yaml\n",
				Map.of("HICONIC-CONF/example.yaml", "example: true\n"));

		List<ClasspathEntry> entries = new ClasspathIndex(project).all();

		assertThat(entries).hasSize(1);
		assertThat(entries.get(0).path).isEqualTo("HICONIC-CONF/example.yaml");
	}

	@Test
	public void loadsMappedFilesystemSourceAndReplacesCanonicalDuplicate() throws Exception {
		newProject("mapped-classpath-resources");
		writeFilesystemArtifact("classpath-resources/example-configuration-1.0", "example-configuration",
				"HICONIC-CONF/example.yaml\nHICONIC-RESOURCES/logo.svg\n",
				Map.of("HICONIC-CONF/example.yaml", "source: canonical\n", "HICONIC-RESOURCES/logo.svg", "<svg/>"));

		writeFilesystemArtifact("packaged-conf/example-configuration-1.0", "example-configuration", "example.yaml\n",
				Map.of("example.yaml", "source: projected\n"));
		writeFilesystemArtifact("packaged-conf/other-configuration-1.0", "other-configuration", "example.yaml\n",
				Map.of("example.yaml", "source: other\n"));

		ClasspathIndex index = new ClasspathIndex(List.of(
				ClasspathIndex.filesystemSource(project.resolve("classpath-resources"), ""),
				ClasspathIndex.filesystemSource(project.resolve("packaged-conf"), "HICONIC-CONF")));

		List<ClasspathEntry> entries = index.all();

		assertThat(entries).hasSize(3);
		assertThat(pathsOf(entries)).containsExactlyInAnyOrder("HICONIC-CONF/example.yaml", "HICONIC-RESOURCES/logo.svg");
		assertThat(entries.stream().filter(e -> e.path.equals("HICONIC-CONF/example.yaml")).map(e -> e.artifactId))
				.containsExactlyInAnyOrder("example-configuration", "other-configuration");
		ClasspathEntry configEntry = entries.stream()
				.filter(e -> e.path.equals("HICONIC-CONF/example.yaml") && e.artifactId.equals("example-configuration"))
				.findFirst()
				.orElseThrow();
		assertThat(Path.of(configEntry.url.toURI())).hasContent("source: projected");
		assertThat(configEntry.artifactId).isEqualTo("example-configuration");
	}

	@Test
	public void takesGroupIdFromCentralPackagedResourceIndex() throws Exception {
		newProject("packaged-resources");
		writeFile("example-configuration-1.0/HICONIC-CONF/example.yaml", "example: true\n");
		writeFile("index.properties", """
				formatVersion=1
				artifact.count=1
				artifact.0.folder=example-configuration-1.0
				artifact.0.groupId=example
				artifact.0.artifactId=example-configuration
				artifact.0.resource.count=1
				artifact.0.resource.0.path=HICONIC-CONF/example.yaml
				""");

		List<ClasspathEntry> entries = new ClasspathIndex(project).all();

		assertThat(entries).hasSize(1);
		assertThat(entries.get(0).groupId).isEqualTo("example");
		assertThat(entries.get(0).artifactId).isEqualTo("example-configuration");
	}

	/** A mirror written before the groupId was recorded is still valid. */
	@Test
	public void leavesGroupIdEmptyWhenCentralPackagedResourceIndexHasNone() throws Exception {
		newProject("packaged-resources");
		writeFile("example-configuration-1.0/HICONIC-CONF/example.yaml", "example: true\n");
		writeFile("index.properties", """
				formatVersion=1
				artifact.count=1
				artifact.0.folder=example-configuration-1.0
				artifact.0.artifactId=example-configuration
				artifact.0.resource.count=1
				artifact.0.resource.0.path=HICONIC-CONF/example.yaml
				""");

		List<ClasspathEntry> entries = new ClasspathIndex(project).all();

		assertThat(entries).hasSize(1);
		assertThat(entries.get(0).groupId).isEmpty();
	}

	/** Two artifacts with the same artifactId but different groups are two artifacts, so neither replaces the other's resource. */
	@Test
	public void keepsSamePathOfSameArtifactIdInDifferentGroups() throws Exception {
		newProject("packaged-resources");
		writeFile("a/HICONIC-CONF/example.yaml", "group: a\n");
		writeFile("b/HICONIC-CONF/example.yaml", "group: b\n");
		writeFile("index.properties", """
				formatVersion=1
				artifact.count=2
				artifact.0.folder=a
				artifact.0.groupId=group.a
				artifact.0.artifactId=example-configuration
				artifact.0.resource.count=1
				artifact.0.resource.0.path=HICONIC-CONF/example.yaml
				artifact.1.folder=b
				artifact.1.groupId=group.b
				artifact.1.artifactId=example-configuration
				artifact.1.resource.count=1
				artifact.1.resource.0.path=HICONIC-CONF/example.yaml
				""");

		List<ClasspathEntry> entries = new ClasspathIndex(project).all();

		assertThat(entries).extracting(entry -> entry.groupId).containsExactlyInAnyOrder("group.a", "group.b");
	}

	/**
	 * A later source replaces the entry of the same artifact, even if only one of the sources knows the groupId. The groupId is kept, so it is not lost
	 * by the replacement.
	 */
	@Test
	public void replacesEntryOfSameArtifactWhenOnlyOneSourceKnowsTheGroupId() throws Exception {
		newProject("mixed-group-knowledge");
		writeFile("packaged-resources/example-configuration-1.0/HICONIC-CONF/example.yaml", "source: raw");
		writeFile("packaged-resources/index.properties", """
				formatVersion=1
				artifact.count=1
				artifact.0.folder=example-configuration-1.0
				artifact.0.groupId=example
				artifact.0.artifactId=example-configuration
				artifact.0.resource.count=1
				artifact.0.resource.0.path=HICONIC-CONF/example.yaml
				""");
		writeFilesystemArtifact("packaged-conf/example-configuration-1.0", "example-configuration", "example.yaml",
				Map.of("example.yaml", "source: projected"));

		ClasspathIndex index = new ClasspathIndex(List.of(
				ClasspathIndex.filesystemSource(project.resolve("packaged-resources"), ""),
				ClasspathIndex.filesystemSource(project.resolve("packaged-conf"), "HICONIC-CONF")));

		List<ClasspathEntry> entries = index.forPrefix("HICONIC-CONF/example.yaml");

		assertThat(entries).hasSize(1);
		assertThat(Path.of(entries.get(0).url.toURI())).hasContent("source: projected");
		assertThat(entries.get(0).groupId).isEqualTo("example");
	}

	@Test
	public void loadsCentralPackagedResourceIndexAndCanExcludeConfiguration() throws Exception {
		newProject("packaged-resources");
		writeFile("example-configuration-1.0/HICONIC-CONF/example.yaml", "example: true\n");
		writeFile("example-configuration-1.0/icons/logo.svg", "<svg/>");
		writeFile("index.properties", """
				formatVersion=1
				artifact.count=1
				artifact.0.folder=example-configuration-1.0
				artifact.0.artifactId=example-configuration
				artifact.0.resource.count=2
				artifact.0.resource.0.path=HICONIC-CONF/example.yaml
				artifact.0.resource.1.path=icons/logo.svg
				""");

		ClasspathIndex index = new ClasspathIndex(List.of(
				ClasspathIndex.filesystemSource(project, "", List.of("HICONIC-CONF/"))));

		assertThat(pathsOf(index.all())).containsExactly("icons/logo.svg");
		assertThat(index.all().get(0).artifactId).isEqualTo("example-configuration");
	}

	@Test
	public void readsArtifactIdOfCentralPackagedResourceIndexWrittenWithOriginKey() throws Exception {
		newProject("legacy-packaged-resources");
		writeFile("example-configuration-1.0/icons/logo.svg", "<svg/>");
		writeFile("index.properties", """
				formatVersion=1
				artifact.count=1
				artifact.0.folder=example-configuration-1.0
				artifact.0.origin=example-configuration
				artifact.0.resource.count=1
				artifact.0.resource.0.path=icons/logo.svg
				""");

		ClasspathIndex index = new ClasspathIndex(project);

		assertThat(pathsOf(index.all())).containsExactly("icons/logo.svg");
		assertThat(index.all().get(0).artifactId).isEqualTo("example-configuration");
	}

	@Test
	public void loadsDirectEffectiveConfigurationSlots() throws Exception {
		newProject("effective-conf");
		writeFile("compiled/database-configuration.yaml", "databases: []\n");
		writeFile("example-configuration/custom.xml", "<custom/>");

		ClasspathIndex index = new ClasspathIndex(List.of(ClasspathIndex.filesystemSlots(project, "HICONIC-CONF")));

		assertThat(pathsOf(index.all())).containsExactlyInAnyOrder(
				"HICONIC-CONF/database-configuration.yaml",
				"HICONIC-CONF/custom.xml");
		assertThat(index.all().stream().map(e -> e.artifactId)).containsExactlyInAnyOrder("compiled", "example-configuration");
	}

	@Test
	public void loadsIntegralConfigurationTreeAsOneSyntheticArtifact() throws Exception {
		newProject("integral-application");
		writeFile("conf/database-configuration.yaml", "databases: []\n");
		writeFile("conf/certificates/client.pem", "certificate");

		ClasspathIndex index = new ClasspathIndex(List.of(
				ClasspathIndex.filesystemTree(project.resolve("conf"), "HICONIC-CONF", "compiled")));

		assertThat(pathsOf(index.all())).containsExactlyInAnyOrder(
				"HICONIC-CONF/database-configuration.yaml",
				"HICONIC-CONF/certificates/client.pem");
		assertThat(index.all()).allSatisfy(entry -> assertThat(entry.artifactId).isEqualTo("compiled"));
	}

	@Test
	public void mapsMaterializedResourcesBackToTheirLogicalArtifactPaths() throws Exception {
		newProject("mapped-integral-application");
		writeFile("conf/sample-configuration.yaml", "label: compiled\n");
		writeFile("conf/log-levels.properties", "a=INFO\n");
		writeFile("conf/log-levels--artifact-b.properties", "b=DEBUG\n");

		var mappings = List.of(
				new ClasspathIndex.FilesystemMapping("HICONIC-CONF/log-levels.properties", "log-levels.properties", "group", "artifact-a"),
				new ClasspathIndex.FilesystemMapping("HICONIC-CONF/log-levels.properties", "log-levels--artifact-b.properties", "group", "artifact-b"));
		ClasspathIndex index = new ClasspathIndex(List.of(
				ClasspathIndex.filesystemTree(project.resolve("conf"), "HICONIC-CONF", "compiled",
						mappings.stream().map(ClasspathIndex.FilesystemMapping::materializedPath).toList()),
				ClasspathIndex.filesystemMappings(project.resolve("conf"), mappings)));

		assertThat(index.forPrefix("HICONIC-CONF/log-levels.properties"))
				.extracting(entry -> entry.groupId + ":" + entry.artifactId)
				.containsExactlyInAnyOrder("group:artifact-a", "group:artifact-b");
		assertThat(index.forPrefix("HICONIC-CONF/sample-configuration.yaml"))
				.extracting(entry -> entry.artifactId)
				.containsExactly("compiled");
	}

	/** The groupId has its own component, so an artifactId never carries a coordinate. */
	@Test(expected = IllegalArgumentException.class)
	public void rejectsCoordinateAsArtifactIdOfMapping() {
		FilesystemMapping _ = new ClasspathIndex.FilesystemMapping("HICONIC-CONF/log-levels.properties", "log-levels.properties", "", "group:artifact-a");
	}

	@Test
	public void combinesGeneralPackagedResourcesWithEffectiveConfiguration() throws Exception {
		newProject("assembled-application");
		writeFile("packaged-resources/example-configuration-1.0/HICONIC-CONF/example.yaml", "source: raw\n");
		writeFile("packaged-resources/example-configuration-1.0/icons/logo.svg", "<svg/>");
		writeFile("packaged-resources/index.properties", """
				formatVersion=1
				artifact.count=1
				artifact.0.folder=example-configuration-1.0
				artifact.0.artifactId=example-configuration
				artifact.0.resource.count=2
				artifact.0.resource.0.path=HICONIC-CONF/example.yaml
				artifact.0.resource.1.path=icons/logo.svg
				""");

		writeFile("effective-conf/compiled/example.yaml", "source: compiled\n");

		ClasspathIndex index = new ClasspathIndex(List.of(
				ClasspathIndex.filesystemSource(project.resolve("packaged-resources"), "", List.of("HICONIC-CONF/")),
				ClasspathIndex.filesystemSlots(project.resolve("effective-conf"), "HICONIC-CONF")));

		assertThat(pathsOf(index.all())).containsExactlyInAnyOrder("HICONIC-CONF/example.yaml", "icons/logo.svg");
		ClasspathEntry configEntry = index.all().stream()
				.filter(e -> e.path.equals("HICONIC-CONF/example.yaml"))
				.findFirst()
				.orElseThrow();
		assertThat(configEntry.artifactId).isEqualTo("compiled");
		assertThat(Path.of(configEntry.url.toURI())).hasContent("source: compiled");
	}

	@Test
	public void findByPrefix_All() throws Exception {
		List<ClasspathEntry> entries = classpathIndex.forPrefix("simple-");

		assertContainsAllSimple(entries);
	}

	@Test
	public void findByPrefix_ExactlyOne() throws Exception {
		List<ClasspathEntry> entries = classpathIndex.forPrefix("simple-entity.yaml");

		assertThat(pathsOf(entries)).containsExactly("simple-entity.yaml");
	}

	@Test
	public void findByPrefix_NoUseCase() throws Exception {
		List<ClasspathEntry> entries = classpathIndex.forPrefix("simple-entity.");

		assertThat(pathsOf(entries)) //
				.containsExactlyInAnyOrder( //
						"simple-entity.1.yaml", //
						"simple-entity.3.yaml", //
						"simple-entity.disambig-2.yaml", //
						"simple-entity.yaml" //
				);
	}

	@Test
	public void findByPrefix_WithUseCase() throws Exception {
		List<ClasspathEntry> entries = classpathIndex.forPrefix("simple-entity~use-case");

		assertThat(pathsOf(entries)) //
				.containsExactlyInAnyOrder( //
						"simple-entity~use-case.disambig.yaml", //
						"simple-entity~use-case.yaml" //
				);
	}

	private void assertContainsAllSimple(List<ClasspathEntry> entries) {
		assertThat(pathsOf(entries)) //
				.containsExactlyInAnyOrder( //
						"simple-entity.1.yaml", //
						"simple-entity.3.yaml", //
						"simple-entity.disambig-2.yaml", //
						"simple-entity.yaml", //
						"simple-entity~use-case.disambig.yaml", //
						"simple-entity~use-case.yaml" //
				);
	}

	private Set<String> pathsOf(List<ClasspathEntry> entries) {
		return entries.stream().map(e -> e.path).collect(Collectors.toSet());
	}

	private void newProject(String name) throws Exception {
		project = temporaryFolder.newFolder(name).toPath();
	}

	/** Writes the file relative to the {@link #project}, creating its parent folders. */
	private void writeFile(String fileName, String content) throws Exception {
		Path file = project.resolve(fileName);
		Files.createDirectories(file.getParent());
		Files.writeString(file, content, StandardCharsets.UTF_8);
	}

	/** A class loader with the given folder of the {@link #project} as its only classpath root, and no parent. */
	private URLClassLoader classLoaderFor(String outputFolder) throws Exception {
		return new URLClassLoader(new URL[] { project.resolve(outputFolder).toUri().toURL() }, null);
	}

	/** Writes an artifact folder of an assembled filesystem mirror, with its index, its artifactId and the given resources relative to it. */
	private void writeFilesystemArtifact(String artifactFolder, String artifactId, String indexContent, Map<String, String> resources)
			throws Exception {
		writeFile(artifactFolder + "/META-INF/classpath-index.txt", indexContent);
		writeFile(artifactFolder + "/META-INF/classpath-origin.properties", "artifactId=" + artifactId + "\n");
		for (var resource : resources.entrySet())
			writeFile(artifactFolder + "/" + resource.getKey(), resource.getValue());
	}

	private void writeZipEntry(ZipOutputStream out, String name, String content) throws Exception {
		out.putNextEntry(new ZipEntry(name));
		out.write(content.getBytes(StandardCharsets.UTF_8));
		out.closeEntry();
	}

}
