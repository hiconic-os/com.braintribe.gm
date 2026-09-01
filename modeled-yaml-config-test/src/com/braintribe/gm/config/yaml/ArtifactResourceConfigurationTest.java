// ============================================================================
// Copyright BRAINTRIBE TECHNOLOGY GMBH, Austria, 2002-2026
// Licensed under the Apache License, Version 2.0
// ============================================================================
package com.braintribe.gm.config.yaml;

import static com.braintribe.testing.junit.assertions.assertj.core.api.Assertions.assertThat;

import java.io.StringWriter;

import org.junit.Test;

import com.braintribe.codec.marshaller.api.GmSerializationOptions;
import com.braintribe.codec.marshaller.api.PlaceholderSupport;
import com.braintribe.codec.marshaller.yaml.YamlMarshaller;
import com.braintribe.gm.config.yaml.model.MergedEntity;
import com.braintribe.gm.model.reason.Maybe;
import com.braintribe.model.processing.resource.artifact.ArtifactResourceValueDescriptorExperts;
import com.braintribe.model.processing.resource.artifact.api.ArtifactResourceResolver;
import com.braintribe.model.processing.vde.expression.api.ValueDescriptorExpressionCodec;
import com.braintribe.model.processing.vde.expression.api.ValueDescriptorExpressionCodecOption;
import com.braintribe.model.processing.vde.expression.api.ValueDescriptorExpressionProjectionOption;
import com.braintribe.model.processing.vde.reasoned.api.ValueDescriptorSourceContext;
import com.braintribe.model.resource.Resource;
import com.braintribe.model.resource.source.ArtifactResourceSource;

public class ArtifactResourceConfigurationTest {

	private static final ValueDescriptorSourceContext CONFIG_SOURCE =
			new ValueDescriptorSourceContext("configuration-artifact", "HICONIC-CONF/config.yaml");

	@Test
	public void resolvesRelativeArtifactResourceWithoutRxTypes() {
		ArtifactResourceResolver resolver = new TestResolver();
		ValueDescriptorExpressionCodec codec = ArtifactResourceValueDescriptorExperts.expressionCodec();
		String yaml = "resource: ${artifactResource('../assets/logo.svg')}\n";

		MergedEntity loaded = new ModeledYamlConfigurationLoader()
				.valueDescriptorExpressions(codec)
				.valueDescriptorExperts(registry -> ArtifactResourceValueDescriptorExperts.register(registry, resolver))
				.valueDescriptorAspect(ValueDescriptorSourceContext.class, CONFIG_SOURCE)
				.loadConfig(MergedEntity.T, () -> new java.io.ByteArrayInputStream(yaml.getBytes(java.nio.charset.StandardCharsets.UTF_8)))
				.get();

		assertThat(loaded.getResource().getName()).isEqualTo("resolved-logo");
		ArtifactResourceSource source = (ArtifactResourceSource) loaded.getResource().getResourceSource();
		assertThat(source.getArtifact()).isEqualTo("configuration-artifact");
		assertThat(source.getPath()).isEqualTo("assets/logo.svg");
	}

	@Test
	public void resolvesUnprefixedPathFromArtifactRoot() {
		ArtifactResourceResolver resolver = new TestResolver();
		String yaml = "resource: ${artifactResource('assets/logo.svg')}\n";

		MergedEntity loaded = new ModeledYamlConfigurationLoader()
				.valueDescriptorExpressions(ArtifactResourceValueDescriptorExperts.expressionCodec())
				.valueDescriptorExperts(registry -> ArtifactResourceValueDescriptorExperts.register(registry, resolver))
				.valueDescriptorAspect(ValueDescriptorSourceContext.class, CONFIG_SOURCE)
				.loadConfig(MergedEntity.T, () -> new java.io.ByteArrayInputStream(yaml.getBytes(java.nio.charset.StandardCharsets.UTF_8)))
				.get();

		ArtifactResourceSource source = (ArtifactResourceSource) loaded.getResource().getResourceSource();
		assertThat(source.getArtifact()).isEqualTo("configuration-artifact");
		assertThat(source.getPath()).isEqualTo("assets/logo.svg");
	}

	@Test
	public void projectsOnlySourceAndPreservesResourceMetadata() {
		MergedEntity entity = MergedEntity.T.create();
		Resource resource = Resource.T.create();
		resource.setName("Proventem logo");
		resource.setMimeType("image/svg+xml");
		ArtifactResourceSource source = ArtifactResourceSource.T.create();
		source.setArtifact("configuration-artifact");
		source.setPath("assets/logo.svg");
		resource.setResourceSource(source);
		entity.setResource(resource);

		String yaml = write(entity);

		assertThat(yaml).contains("name: \"Proventem logo\"");
		assertThat(yaml).contains("mimeType: \"image/svg+xml\"");
		assertThat(yaml).contains("resourceSource: \"${artifactResourceSource('../assets/logo.svg')}\"");
		assertThat(yaml).doesNotContain("artifact: configuration-artifact");
	}

	private static String write(MergedEntity entity) {
		GmSerializationOptions options = GmSerializationOptions.deriveDefaults()
				.inferredRootType(MergedEntity.T)
				.set(PlaceholderSupport.class, true)
				.set(ValueDescriptorExpressionCodecOption.class, ArtifactResourceValueDescriptorExperts.expressionCodec())
				.set(ValueDescriptorExpressionProjectionOption.class,
						ArtifactResourceValueDescriptorExperts.projection(CONFIG_SOURCE))
				.build();
		StringWriter writer = new StringWriter();
		new YamlMarshaller().marshall(writer, entity, options);
		return writer.toString();
	}

	private static class TestResolver implements ArtifactResourceResolver {
		@Override
		public Maybe<Resource> resolveResource(String artifact, String path) {
			Resource resource = Resource.T.create();
			resource.setName("resolved-logo");
			resource.setResourceSource(resolveSource(artifact, path).get());
			return Maybe.complete(resource);
		}

		@Override
		public Maybe<ArtifactResourceSource> resolveSource(String artifact, String path) {
			ArtifactResourceSource source = ArtifactResourceSource.T.create();
			source.setArtifact(artifact);
			source.setPath(path);
			return Maybe.complete(source);
		}
	}
}
