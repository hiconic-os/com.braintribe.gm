// ============================================================================
// Licensed under the Apache License, Version 2.0
// ============================================================================
package com.braintribe.gm.config.yaml;

import static com.braintribe.testing.junit.assertions.assertj.core.api.Assertions.assertThat;

import java.io.StringReader;
import java.io.StringWriter;

import org.junit.Test;

import com.braintribe.codec.marshaller.api.GmSerializationOptions;
import com.braintribe.codec.marshaller.api.PlaceholderSupport;
import com.braintribe.codec.marshaller.yaml.YamlMarshaller;
import com.braintribe.gm.config.yaml.expression.TestDecrypt;
import com.braintribe.gm.config.yaml.expression.TestImportText;
import com.braintribe.gm.config.yaml.model.MergedEntity;
import com.braintribe.gm.model.reason.Maybe;
import com.braintribe.gm.model.reason.essential.InvalidArgument;
import com.braintribe.model.bvd.string.Concatenation;
import com.braintribe.model.generic.reflection.VdHolder;
import com.braintribe.model.processing.vde.expression.ModelBasedValueDescriptorExpressionCodec;
import com.braintribe.model.processing.vde.expression.api.ValueDescriptorExpressionCodecOption;
import com.braintribe.model.processing.vde.expression.api.ValueDescriptorExpressionProjectionOption;

public class ValueDescriptorExpressionCodecTest {

	private final ModelBasedValueDescriptorExpressionCodec codec = new ModelBasedValueDescriptorExpressionCodec(
			TestDecrypt.T, TestImportText.T);

	@Test
	public void learnsNestedFunctionsOnlyFromModelTypes() {
		Maybe<Object> parsed = codec.parse("${testDecrypt(testImportText('./secret.txt'))}");

		assertThat(parsed.isSatisfied()).isTrue();
		TestDecrypt decrypt = (TestDecrypt) parsed.get();
		TestImportText importText = TestDecrypt.T.getProperty("cipherText").getVdDirect(decrypt);
		assertThat(importText.getPath()).isEqualTo("./secret.txt");
		assertThat(codec.render(decrypt).get()).isEqualTo("${testDecrypt(testImportText('./secret.txt'))}");
	}

	@Test
	public void preservesStaticTemplatePartsInRoundtrip() {
		Object parsed = codec.parse("prefix-${testImportText('./payload.txt')}-suffix").get();

		assertThat(parsed).isInstanceOf(Concatenation.class);
		Concatenation concatenation = (Concatenation) parsed;
		assertThat(concatenation.getOperands()).hasSize(3);
		assertThat(VdHolder.isVdHolder(concatenation.getOperands().get(1))).isFalse();
		assertThat(codec.render(parsed).get()).isEqualTo("prefix-${testImportText('./payload.txt')}-suffix");
	}

	@Test
	public void rejectsFunctionsOutsideSuppliedModelSpace() {
		Maybe<Object> parsed = codec.parse("${unknown('./payload.txt')}");

		assertThat(parsed.isUnsatisfied()).isTrue();
		assertThat((Object) parsed.whyUnsatisfied()).isInstanceOf(InvalidArgument.class);
		assertThat(parsed.whyUnsatisfied().getText()).contains("Unknown value descriptor function 'unknown'");
	}

	@Test
	public void yamlUsesConfiguredModelVocabularyBidirectionally() {
		String yaml = "string: ${testImportText('./payload.txt')}\n";
		MergedEntity parsed = YamlConfigurations.read(MergedEntity.T)
				.placeholders()
				.options(options -> options.set(ValueDescriptorExpressionCodecOption.class, codec))
				.from(new StringReader(yaml))
				.get();

		TestImportText descriptor = MergedEntity.T.getProperty("string").getVd(parsed);
		assertThat(descriptor.getPath()).isEqualTo("./payload.txt");

		GmSerializationOptions options = GmSerializationOptions.deriveDefaults()
				.inferredRootType(MergedEntity.T)
				.set(PlaceholderSupport.class, true)
				.set(ValueDescriptorExpressionCodecOption.class, codec)
				.build();
		StringWriter writer = new StringWriter();
		new YamlMarshaller().marshall(writer, parsed, options);
		assertThat(writer.toString()).contains("string: \"${testImportText('./payload.txt')}\"");
	}

	@Test
	public void modeledConfigurationLoaderContributesExpertsAndSourceAspects() {
		MergedEntity loaded = new ModeledYamlConfigurationLoader()
				.valueDescriptorExpressions(codec)
				.valueDescriptorExperts(registry -> registry.register(TestImportText.T,
						(context, descriptor) -> Maybe.complete(context.getAspect(String.class) + descriptor.getPath())))
				.valueDescriptorAspect(String.class, "artifact-a:")
				.loadConfig(MergedEntity.T,
						() -> new java.io.ByteArrayInputStream("string: ${testImportText('./payload.txt')}\n".getBytes()))
				.get();

		assertThat(loaded.getString()).isEqualTo("artifact-a:./payload.txt");
	}

	@Test
	public void yamlCanProjectConcreteValuesBackToModeledExpressions() {
		MergedEntity entity = MergedEntity.T.create();
		entity.setString("project-me");

		GmSerializationOptions options = GmSerializationOptions.deriveDefaults()
				.inferredRootType(MergedEntity.T)
				.set(PlaceholderSupport.class, true)
				.set(ValueDescriptorExpressionCodecOption.class, codec)
				.set(ValueDescriptorExpressionProjectionOption.class, (type, value) -> {
					if (!"project-me".equals(value))
						return null;
					TestImportText descriptor = TestImportText.T.create();
					descriptor.setPath("./payload.txt");
					return descriptor;
				})
				.build();
		StringWriter writer = new StringWriter();
		new YamlMarshaller().marshall(writer, entity, options);

		assertThat(writer.toString()).contains("string: \"${testImportText('./payload.txt')}\"");
	}
}
