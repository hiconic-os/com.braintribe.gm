package com.braintribe.gm.config.yaml;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import com.braintribe.gm.config.yaml.index.ClasspathEntry;

/**
 * Tests for {@link ConfigurationEntrySorter} based on examples from {@link ModeledYamlConfiguration} Javadoc.
 * 
 * @author peter.gazdik
 */
public class ConfigurationEntrySorterTest {

	@Test
	public void sortByPriority() {
		ClasspathEntry low = cpEntry("artifact", "1.0", "my-entity~uc.1.yaml");
		ClasspathEntry high = cpEntry("artifact", "1.0", "my-entity~uc.5.yaml");

		List<ClasspathEntry> sorted = sortEntries(high, low);

		assertThat(sorted).containsExactly(low, high);
	}

	@Test
	public void sortByDisambiguator_samePriority() {
		ClasspathEntry abc = cpEntry("artifact", "1.0", "my-entity~uc.abc-3.yaml");
		ClasspathEntry xyz = cpEntry("artifact", "1.0", "my-entity~uc.xyz-3.yaml");

		List<ClasspathEntry> sorted = sortEntries(xyz, abc);

		assertThat(sorted).containsExactly(abc, xyz);
	}

	@Test
	public void sortByArtifactId_samePriority_NodDisambiguator() {
		ClasspathEntry aArt = cpEntry("aaa-artifact", "1.0", "my-entity~uc.yaml");
		ClasspathEntry zArt = cpEntry("zzz-artifact", "1.0", "my-entity~uc.yaml");

		List<ClasspathEntry> sorted = sortEntries(zArt, aArt);

		assertThat(sorted).containsExactly(aArt, zArt);
	}

	@Test
	public void noPriority_noDisambiguator_defaultsToMinusOneAndEmpty() {
		ClasspathEntry noPriority = cpEntry("artifact", "1.0", "my-entity~uc.yaml");
		ClasspathEntry yesPriority = cpEntry("artifact", "1.0", "my-entity~uc.1.yaml");

		List<ClasspathEntry> sorted = sortEntries(yesPriority, noPriority);

		// -1 < 1, so entry without priority/disambiguator comes first
		assertThat(sorted).containsExactly(noPriority, yesPriority);
	}

	@Test
	public void disambiguatorOnly_noPriority() {
		ClasspathEntry withDisamb = cpEntry("artifact", "1.0", "my-entity.mydisamb.yaml");
		ClasspathEntry plain = cpEntry("artifact", "1.0", "my-entity.yaml");

		List<ClasspathEntry> sorted = sortEntries(withDisamb, plain);

		// Both priority -1; "" < "mydisamb"
		assertThat(sorted).containsExactly(plain, withDisamb);
	}

	@Test
	public void nonYamlFilesAreIgnored() {
		ClasspathEntry yamlEntry = cpEntry("artifact", "1.0", "my-entity~uc.yaml");
		ClasspathEntry txtEntry = cpEntry("artifact", "1.0", "my-entity~uc.txt");

		List<ClasspathEntry> sorted = sortEntries(txtEntry, yamlEntry);

		assertThat(sorted).containsExactly(yamlEntry);
	}

	@Test
	public void complexExample() {
		// Create entries in REVERSE order to prove sorting works
		ClasspathEntry e6 = cpEntry("artifactIdZ", "1.0", "my-entity~use-case.xyz-2.yaml");
		ClasspathEntry e5 = cpEntry("artifactIdZ", "1.0", "my-entity~use-case.abc-2.yaml");
		ClasspathEntry e4 = cpEntry("artifactIdA", "1.0", "my-entity~use-case.1.yaml");
		ClasspathEntry e3 = cpEntry("artifactIdA", "1.0", "my-entity~use-case.xyz-0.yaml");
		ClasspathEntry e2 = cpEntry("artifactIdZ", "1.0", "my-entity~use-case.yaml");
		ClasspathEntry e1 = cpEntry("artifactIdA", "1.0", "my-entity~use-case.yaml");

		List<ClasspathEntry> sorted = sortEntries(e6, e5, e4, e3, e2, e1);

		assertThat(sorted).containsExactly(e1, e2, e3, e4, e5, e6);
	}

	private List<ClasspathEntry> sortEntries(ClasspathEntry... entries) {
		return ConfigurationEntrySorter.sortClasspathEntries(Arrays.asList(entries));
	}

	// ###########################################
	// ## . . . . . . . Helpers . . . . . . . . ##
	// ###########################################

	/**
	 * For the YAML filename format see {@link ModeledYamlConfiguration}
	 */
	private static ClasspathEntry cpEntry(String artifactId, String version, String yamlFileName) {
		String pathInsideJar = "config/" + yamlFileName;

		String urlStr = "jar:file:/C:/maven-repo/" + artifactId + "/" + version + "/" + artifactId + "-" + version + ".jar!/" + pathInsideJar;
		return new ClasspathEntry(pathInsideJar, asUrl(urlStr));
	}

	private static URL asUrl(String urlStr) {
		try {
			return new URI(urlStr).toURL();
		} catch (URISyntaxException | MalformedURLException e) {
			throw new RuntimeException("Bad test URL: " + urlStr, e);
		}
	}

}