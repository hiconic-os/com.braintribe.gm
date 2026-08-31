// ============================================================================
// Copyright BRAINTRIBE TECHNOLOGY GMBH, Austria, 2002-2026
// Licensed under the Apache License, Version 2.0
// ============================================================================
package com.braintribe.gm.config.yaml;

import static com.braintribe.testing.junit.assertions.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import com.braintribe.gm.config.yaml.model.LoadedEntity;
import com.braintribe.gm.model.reason.Maybe;
import com.braintribe.gm.model.reason.config.PropertyNotFound;
import com.braintribe.gm.model.reason.essential.NotFound;
import com.braintribe.model.generic.reflection.VdHolder;
import com.braintribe.model.bvd.convert.ToInteger;
import com.braintribe.model.bvd.convert.ToString;
import com.braintribe.model.bvd.string.Concatenation;
import com.braintribe.model.generic.value.Variable;
import com.braintribe.model.processing.vde.reasoned.api.ResidualValuePolicy;
import com.braintribe.model.processing.vde.reasoned.impl.ReasonedValueDescriptorMaterializer;
import com.braintribe.model.processing.vde.reasoned.impl.StandardValueDescriptorEvaluationContext;
import com.braintribe.model.processing.vde.reasoned.impl.ValueDescriptorExpertRegistry;
import com.braintribe.testing.model.test.technical.features.CollectionEntity;
import com.braintribe.testing.model.test.technical.features.SimpleEntity;

public class ReasonedValueDescriptorMaterializerTest {

	@Test
	public void reasonedConfigurationPathMatchesEstablishedEvaluation() {
		LoadedEntity oldSource = placeholderConfiguration();
		LoadedEntity newSource = placeholderConfiguration();

		LoadedEntity oldResult = YamlConfigurations.resolvePlaceholders(oldSource, variable -> configuredValue(variable.getName())).get();
		LoadedEntity newResult = YamlConfigurations.resolvePlaceholdersReasoned(newSource,
				variable -> Maybe.complete(configuredValue(variable.getName()))).get();

		assertThat(newResult.getCpValue()).isEqualTo(oldResult.getCpValue()).isEqualTo("http://localhost:54320/api");
		assertThat(newResult.getIntegerValue()).isEqualTo(oldResult.getIntegerValue()).isEqualTo(54320);
		assertThat(newResult).isNotSameAs(newSource);
	}

    @Test
    public void materializesPropertyWithoutChangingSource() {
        LoadedEntity source = LoadedEntity.T.createRaw();
        Variable variable = variable("answer");
        LoadedEntity.T.getProperty("cpValue").setVdDirect(source, variable);

        LoadedEntity result = materializer(nameContext(name -> Maybe.complete("resolved-" + name))).materialize(source).get();

        assertThat(result).isNotSameAs(source);
        assertThat(result.getCpValue()).isEqualTo("resolved-answer");
        assertThat((Object) LoadedEntity.T.getProperty("cpValue").getVdDirect(result)).isNull();
        assertThat((Object) LoadedEntity.T.getProperty("cpValue").getVdDirect(source)).isSameAs(variable);
    }

    @Test
    public void preservesUnresolvedDescriptorWhenPolicyAcceptsReason() {
        LoadedEntity source = LoadedEntity.T.createRaw();
        Variable variable = variable("missing");
        LoadedEntity.T.getProperty("cpValue").setVdDirect(source, variable);

        StandardValueDescriptorEvaluationContext context = nameContext(name -> PropertyNotFound.create(name).asMaybe());
        ResidualValuePolicy policy = ResidualValuePolicy.preserving(reason -> NotFound.T.isInstance(reason));
        LoadedEntity result = new ReasonedValueDescriptorMaterializer(context, policy).materialize(source).get();

        Variable residual = LoadedEntity.T.getProperty("cpValue").getVdDirect(result);
        assertThat(residual).isNotSameAs(variable);
        assertThat(residual.getName()).isEqualTo("missing");
    }

    @Test
    public void materializesVdHolderInsideTypedCollection() {
        CollectionEntity source = CollectionEntity.T.createRaw();
        List<Object> rawValues = new ArrayList<>();
        rawValues.add("static");
        rawValues.add(VdHolder.newInstance(variable("dynamic")));
        CollectionEntity.T.getProperty("stringList").setDirectUnsafe(source, rawValues);

        CollectionEntity result = materializer(nameContext(name -> Maybe.complete("resolved-" + name))).materialize(source).get();

        assertThat(result.getStringList()).containsExactly("static", "resolved-dynamic");
    }

    @Test
    public void clonesOriginalEntityReturnedByExpert() {
        SimpleEntity borrowed = SimpleEntity.T.createRaw();
        borrowed.setStringProperty("borrowed");
        CollectionEntity source = CollectionEntity.T.createRaw();
        List<Object> rawValues = new ArrayList<>();
        Variable entityVariable = variable("entity");
        entityVariable.setTypeSignature(SimpleEntity.T.getTypeSignature());
        rawValues.add(VdHolder.newInstance(entityVariable));
        CollectionEntity.T.getProperty("simpleEntityList").setDirectUnsafe(source, rawValues);

        StandardValueDescriptorEvaluationContext context = nameContext(name -> Maybe.complete(borrowed));
        CollectionEntity result = materializer(context).materialize(source).get();

        SimpleEntity value = result.getSimpleEntityList().get(0);
        assertThat(value).isNotSameAs(borrowed);
        assertThat(value.getStringProperty()).isEqualTo("borrowed");
    }

    @Test
    public void typedExpertGetterUsesReasonedPai() {
        Variable inner = variable("inner");
        Variable outer = variable("unused");
        Variable.T.getProperty("name").setVdDirect(outer, inner);
        StandardValueDescriptorEvaluationContext context = nameContext(name -> Maybe.complete("value-of-" + name));

        assertThat(context.evaluate(outer).get()).isEqualTo("value-of-value-of-inner");
    }

	@Test
	public void nestedUnsatisfiedMaybeIsTunneledAndRestoredWithoutReasonLoss() {
		Variable inner = variable("missing");
		Variable outer = variable("unused");
		Variable.T.getProperty("name").setVdDirect(outer, inner);

		StandardValueDescriptorEvaluationContext context = nameContext(name -> PropertyNotFound.create(name).asMaybe());
		Maybe<Object> result = context.evaluate(outer);

		assertThat(result.isUnsatisfied()).isTrue();
		assertThat((Object) result.whyUnsatisfied()).isInstanceOf(PropertyNotFound.class);
		assertThat(result.whyUnsatisfied().getText()).contains("missing");
	}

    private static ReasonedValueDescriptorMaterializer materializer(StandardValueDescriptorEvaluationContext context) {
        return new ReasonedValueDescriptorMaterializer(context);
    }

    private static StandardValueDescriptorEvaluationContext nameContext(NameResolution resolution) {
        ValueDescriptorExpertRegistry registry = new ValueDescriptorExpertRegistry();
        registry.register(Variable.T, (context, descriptor) -> resolution.resolve(descriptor.getName()));
        return new StandardValueDescriptorEvaluationContext(registry);
    }

    private static Variable variable(String name) {
        Variable variable = Variable.T.create();
        variable.setName(name);
        return variable;
    }

	private static LoadedEntity placeholderConfiguration() {
		Concatenation concatenation = Concatenation.T.create();
		concatenation.setOperands(Arrays.asList("http://", variable("HOST"), ":", variable("PORT"), "/api"));

		ToString stringValue = ToString.T.create();
		stringValue.setOperand(concatenation);

		ToInteger integerValue = ToInteger.T.create();
		integerValue.setOperand(variable("PORT"));

		LoadedEntity result = LoadedEntity.T.createRaw();
		LoadedEntity.T.getProperty("cpValue").setVdDirect(result, stringValue);
		LoadedEntity.T.getProperty("integerValue").setVdDirect(result, integerValue);
		return result;
	}

	private static String configuredValue(String name) {
		if ("HOST".equals(name))
			return "localhost";
		if ("PORT".equals(name))
			return "54320";
		throw new IllegalArgumentException("Unexpected variable: " + name);
	}

    @FunctionalInterface
    private interface NameResolution {
        Maybe<?> resolve(String name);
    }
}
