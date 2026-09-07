// ============================================================================
// Licensed under the Apache License, Version 2.0
// ============================================================================
package com.braintribe.gm.config.yaml;

import static com.braintribe.testing.junit.assertions.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;

import org.junit.Test;

import com.braintribe.codec.marshaller.api.GmSerializationOptions;
import com.braintribe.codec.marshaller.api.PlaceholderSupport;
import com.braintribe.codec.marshaller.yaml.YamlMarshaller;
import com.braintribe.gm.config.yaml.model.MergedEntity;
import com.braintribe.gm.model.reason.Maybe;
import com.braintribe.model.processing.resource.packaged.PackagedResourceValueDescriptorExperts;
import com.braintribe.model.processing.resource.packaged.api.PackagedResourceResolver;
import com.braintribe.model.processing.vde.expression.api.ValueDescriptorExpressionCodec;
import com.braintribe.model.processing.vde.expression.api.ValueDescriptorExpressionCodecOption;
import com.braintribe.model.processing.vde.expression.api.ValueDescriptorExpressionProjectionOption;
import com.braintribe.model.processing.vde.reasoned.api.ValueDescriptorSourceContext;
import com.braintribe.model.resource.Resource;
import com.braintribe.model.resource.source.PackagedSource;

public class PackagedResourceConfigurationTest {

	private static final ValueDescriptorSourceContext CONFIG_SOURCE =
			new ValueDescriptorSourceContext("configuration-artifact", "HICONIC-CONF/config.yaml");

	@Test
	public void resolvesRelativePackagedResourceWithoutRxTypes() {
		PackagedResourceResolver resolver = new TestResolver();
		ValueDescriptorExpressionCodec codec = PackagedResourceValueDescriptorExperts.expressionCodec();
		String yaml = "resource: ${packagedResource('../assets/logo.svg')}\n";

		MergedEntity loaded = new ModeledYamlConfigurationLoader()
				.valueDescriptorExpressions(codec)
				.valueDescriptorExperts(registry -> PackagedResourceValueDescriptorExperts.register(registry, resolver))
				.valueDescriptorAspect(ValueDescriptorSourceContext.class, CONFIG_SOURCE)
				.loadConfig(MergedEntity.T, () -> new java.io.ByteArrayInputStream(yaml.getBytes(java.nio.charset.StandardCharsets.UTF_8)))
				.get();

		assertThat(loaded.getResource().getName()).isEqualTo("resolved-logo");
		PackagedSource source = (PackagedSource) loaded.getResource().getResourceSource();
		assertThat(source.getArtifact()).isEqualTo("configuration-artifact");
		assertThat(source.getPath()).isEqualTo("assets/logo.svg");
	}

	@Test
	public void resolvesUnprefixedPathFromArtifactRoot() {
		PackagedResourceResolver resolver = new TestResolver();
		String yaml = "resource: ${packagedResource('assets/logo.svg')}\n";

		MergedEntity loaded = new ModeledYamlConfigurationLoader()
				.valueDescriptorExpressions(PackagedResourceValueDescriptorExperts.expressionCodec())
				.valueDescriptorExperts(registry -> PackagedResourceValueDescriptorExperts.register(registry, resolver))
				.valueDescriptorAspect(ValueDescriptorSourceContext.class, CONFIG_SOURCE)
				.loadConfig(MergedEntity.T, () -> new java.io.ByteArrayInputStream(yaml.getBytes(java.nio.charset.StandardCharsets.UTF_8)))
				.get();

		PackagedSource source = (PackagedSource) loaded.getResource().getResourceSource();
		assertThat(source.getArtifact()).isEqualTo("configuration-artifact");
		assertThat(source.getPath()).isEqualTo("assets/logo.svg");
	}

	@Test
	public void projectsOnlySourceAndPreservesResourceMetadata() {
		MergedEntity entity = MergedEntity.T.create();
		Resource resource = Resource.T.create();
		resource.setName("Proventem logo");
		resource.setMimeType("image/svg+xml");
		PackagedSource source = PackagedSource.T.create();
		source.setArtifact("configuration-artifact");
		source.setPath("assets/logo.svg");
		resource.setResourceSource(source);
		entity.setResource(resource);

		String yaml = write(entity);

		assertThat(yaml).contains("name: \"Proventem logo\"");
		assertThat(yaml).contains("mimeType: \"image/svg+xml\"");
		assertThat(yaml).contains("resourceSource: \"${packagedSource('../assets/logo.svg')}\"");
		assertThat(yaml).doesNotContain("artifact: configuration-artifact");
	}

	private static String write(MergedEntity entity) {
		GmSerializationOptions options = GmSerializationOptions.deriveDefaults()
				.inferredRootType(MergedEntity.T)
				.set(PlaceholderSupport.class, true)
				.set(ValueDescriptorExpressionCodecOption.class, PackagedResourceValueDescriptorExperts.expressionCodec())
				.set(ValueDescriptorExpressionProjectionOption.class,
						PackagedResourceValueDescriptorExperts.projection(CONFIG_SOURCE))
				.build();
		StringWriter writer = new StringWriter();
		new YamlMarshaller().marshall(writer, entity, options);
		return writer.toString();
	}

	private static class TestResolver implements PackagedResourceResolver {
		@Override
		public Maybe<Resource> resolveResource(String artifact, String path) {
			Resource resource = Resource.T.create();
			resource.setName("resolved-logo");
			resource.setResourceSource(resolveSource(artifact, path).get());
			return Maybe.complete(resource);
		}

		@Override
		public Maybe<PackagedSource> resolveSource(String artifact, String path) {
			PackagedSource source = PackagedSource.T.create();
			source.setArtifact(artifact);
			source.setPath(path);
			return Maybe.complete(source);
		}

		/** The tests here are about addressing, not about payload, so the stream is the address itself. */
		@Override
		public Maybe<InputStream> openStream(String artifact, String path) {
			return Maybe.complete(new ByteArrayInputStream((artifact + ":" + path).getBytes(StandardCharsets.UTF_8)));
		}
	}
}
