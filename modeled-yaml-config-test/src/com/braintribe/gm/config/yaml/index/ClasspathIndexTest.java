package com.braintribe.gm.config.yaml.index;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.Test;

/**
 * Tests for {@link ClasspathIndex}
 * 
 * @author peter.gazdik
 */
public class ClasspathIndexTest {

	private final ClasspathIndex classpathIndex = new ClasspathIndex();

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
