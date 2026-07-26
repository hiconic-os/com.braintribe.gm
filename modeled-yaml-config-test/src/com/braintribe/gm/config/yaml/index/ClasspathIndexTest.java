package com.braintribe.gm.config.yaml.index;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
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

}
