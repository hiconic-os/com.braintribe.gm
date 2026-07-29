package com.braintribe.gm.config.yaml.index;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * Tests for {@link ClasspathIndex}
 * 
 * @author peter.gazdik
 */
public class ClasspathIndexTest {

	private final ClasspathIndex classpathIndex = new ClasspathIndex();

	@Rule
	public TemporaryFolder temporaryFolder = new TemporaryFolder();

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
		Path root = temporaryFolder.newFolder("classpath-resources").toPath();
		Path artifact = root.resolve("example-configuration-1.0");
		Path config = artifact.resolve("HICONIC-CONF/example.yaml");
		Path index = artifact.resolve("META-INF/classpath-index.txt");
		Path origin = artifact.resolve("META-INF/classpath-origin.properties");
		Files.createDirectories(config.getParent());
		Files.createDirectories(index.getParent());
		Files.writeString(config, "example: true\n", StandardCharsets.UTF_8);
		Files.writeString(index, "# preserved comment\nHICONIC-CONF/example.yaml\n", StandardCharsets.UTF_8);
		Files.writeString(origin, "artifactId=example-configuration\n", StandardCharsets.UTF_8);

		List<ClasspathEntry> entries = new ClasspathIndex(root).all();

		assertThat(entries).hasSize(1);
		assertThat(entries.get(0).path).isEqualTo("HICONIC-CONF/example.yaml");
		assertThat(entries.get(0).origin).isEqualTo("example-configuration");
		assertThat(entries.get(0).url.getProtocol()).isEqualTo("file");
	}

	@Test
	public void loadsMappedFilesystemSourceAndReplacesCanonicalDuplicate() throws Exception {
		Path root = temporaryFolder.newFolder("mapped-classpath-resources").toPath();
		Path canonicalArtifact = root.resolve("classpath-resources/example-configuration-1.0");
		Path canonicalConfig = canonicalArtifact.resolve("HICONIC-CONF/example.yaml");
		Path canonicalResource = canonicalArtifact.resolve("HICONIC-RESOURCES/logo.svg");
		writeFilesystemArtifact(canonicalArtifact, "example-configuration",
				"HICONIC-CONF/example.yaml\nHICONIC-RESOURCES/logo.svg\n",
				Map.of(canonicalConfig, "source: canonical\n", canonicalResource, "<svg/>"));

		Path projectedArtifact = root.resolve("packaged-conf/example-configuration-1.0");
		Path projectedConfig = projectedArtifact.resolve("example.yaml");
		writeFilesystemArtifact(projectedArtifact, "example-configuration", "example.yaml\n", Map.of(projectedConfig, "source: projected\n"));
		Path otherProjectedArtifact = root.resolve("packaged-conf/other-configuration-1.0");
		Path otherProjectedConfig = otherProjectedArtifact.resolve("example.yaml");
		writeFilesystemArtifact(otherProjectedArtifact, "other-configuration", "example.yaml\n",
				Map.of(otherProjectedConfig, "source: other\n"));

		ClasspathIndex index = new ClasspathIndex(List.of(
				ClasspathIndex.filesystemSource(root.resolve("classpath-resources"), ""),
				ClasspathIndex.filesystemSource(root.resolve("packaged-conf"), "HICONIC-CONF")));

		List<ClasspathEntry> entries = index.all();

		assertThat(entries).hasSize(3);
		assertThat(pathsOf(entries)).containsExactlyInAnyOrder("HICONIC-CONF/example.yaml", "HICONIC-RESOURCES/logo.svg");
		assertThat(entries.stream().filter(e -> e.path.equals("HICONIC-CONF/example.yaml")).map(e -> e.origin))
				.containsExactlyInAnyOrder("example-configuration", "other-configuration");
		ClasspathEntry configEntry = entries.stream()
				.filter(e -> e.path.equals("HICONIC-CONF/example.yaml") && e.origin.equals("example-configuration"))
				.findFirst()
				.orElseThrow();
		assertThat(Path.of(configEntry.url.toURI())).hasContent("source: projected");
		assertThat(configEntry.origin).isEqualTo("example-configuration");
	}

	@Test
	public void loadsCentralPackagedResourceIndexAndCanExcludeConfiguration() throws Exception {
		Path root = temporaryFolder.newFolder("packaged-resources").toPath();
		Path artifact = root.resolve("example-configuration-1.0");
		Path config = artifact.resolve("HICONIC-CONF/example.yaml");
		Path logo = artifact.resolve("icons/logo.svg");
		Files.createDirectories(config.getParent());
		Files.createDirectories(logo.getParent());
		Files.writeString(config, "example: true\n", StandardCharsets.UTF_8);
		Files.writeString(logo, "<svg/>", StandardCharsets.UTF_8);
		Files.writeString(root.resolve("index.properties"), """
				formatVersion=1
				artifact.count=1
				artifact.0.folder=example-configuration-1.0
				artifact.0.origin=example-configuration
				artifact.0.resource.count=2
				artifact.0.resource.0.path=HICONIC-CONF/example.yaml
				artifact.0.resource.1.path=icons/logo.svg
				""", StandardCharsets.UTF_8);

		ClasspathIndex index = new ClasspathIndex(List.of(
				ClasspathIndex.filesystemSource(root, "", List.of("HICONIC-CONF/"))));

		assertThat(pathsOf(index.all())).containsExactly("icons/logo.svg");
		assertThat(index.all().get(0).origin).isEqualTo("example-configuration");
	}

	@Test
	public void loadsDirectEffectiveConfigurationSlots() throws Exception {
		Path root = temporaryFolder.newFolder("effective-conf").toPath();
		Path compiled = root.resolve("compiled/database-configuration.yaml");
		Path residual = root.resolve("example-configuration/custom.xml");
		Files.createDirectories(compiled.getParent());
		Files.createDirectories(residual.getParent());
		Files.writeString(compiled, "databases: []\n", StandardCharsets.UTF_8);
		Files.writeString(residual, "<custom/>", StandardCharsets.UTF_8);

		ClasspathIndex index = new ClasspathIndex(List.of(ClasspathIndex.filesystemSlots(root, "HICONIC-CONF")));

		assertThat(pathsOf(index.all())).containsExactlyInAnyOrder(
				"HICONIC-CONF/database-configuration.yaml",
				"HICONIC-CONF/custom.xml");
		assertThat(index.all().stream().map(e -> e.origin)).containsExactlyInAnyOrder("compiled", "example-configuration");
	}

	@Test
	public void combinesGeneralPackagedResourcesWithEffectiveConfiguration() throws Exception {
		Path root = temporaryFolder.newFolder("assembled-application").toPath();
		Path packagedResources = root.resolve("packaged-resources");
		Path artifact = packagedResources.resolve("example-configuration-1.0");
		Path rawConfig = artifact.resolve("HICONIC-CONF/example.yaml");
		Path logo = artifact.resolve("icons/logo.svg");
		Files.createDirectories(rawConfig.getParent());
		Files.createDirectories(logo.getParent());
		Files.writeString(rawConfig, "source: raw\n", StandardCharsets.UTF_8);
		Files.writeString(logo, "<svg/>", StandardCharsets.UTF_8);
		Files.writeString(packagedResources.resolve("index.properties"), """
				formatVersion=1
				artifact.count=1
				artifact.0.folder=example-configuration-1.0
				artifact.0.origin=example-configuration
				artifact.0.resource.count=2
				artifact.0.resource.0.path=HICONIC-CONF/example.yaml
				artifact.0.resource.1.path=icons/logo.svg
				""", StandardCharsets.UTF_8);

		Path effectiveConfig = root.resolve("effective-conf/compiled/example.yaml");
		Files.createDirectories(effectiveConfig.getParent());
		Files.writeString(effectiveConfig, "source: compiled\n", StandardCharsets.UTF_8);

		ClasspathIndex index = new ClasspathIndex(List.of(
				ClasspathIndex.filesystemSource(packagedResources, "", List.of("HICONIC-CONF/")),
				ClasspathIndex.filesystemSlots(root.resolve("effective-conf"), "HICONIC-CONF")));

		assertThat(pathsOf(index.all())).containsExactlyInAnyOrder("HICONIC-CONF/example.yaml", "icons/logo.svg");
		ClasspathEntry configEntry = index.all().stream()
				.filter(e -> e.path.equals("HICONIC-CONF/example.yaml"))
				.findFirst()
				.orElseThrow();
		assertThat(configEntry.origin).isEqualTo("compiled");
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

	private void writeFilesystemArtifact(Path artifact, String artifactId, String indexContent, Map<Path, String> resources) throws Exception {
		Path index = artifact.resolve("META-INF/classpath-index.txt");
		Path origin = artifact.resolve("META-INF/classpath-origin.properties");
		Files.createDirectories(index.getParent());
		Files.writeString(index, indexContent, StandardCharsets.UTF_8);
		Files.writeString(origin, "artifactId=" + artifactId + "\n", StandardCharsets.UTF_8);
		for (var resource : resources.entrySet()) {
			Files.createDirectories(resource.getKey().getParent());
			Files.writeString(resource.getKey(), resource.getValue(), StandardCharsets.UTF_8);
		}
	}

}
