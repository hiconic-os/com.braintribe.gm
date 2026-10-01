// ============================================================================
// Licensed under the Apache License, Version 2.0
// ============================================================================
package com.braintribe.gm.config.yaml;

import static com.braintribe.testing.junit.assertions.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.Test;

import com.braintribe.gm.config.yaml.expression.TestDecrypt;
import com.braintribe.gm.config.yaml.expression.TestImportText;
import com.braintribe.gm.config.yaml.model.LoadedEntity;
import com.braintribe.gm.config.ReasonedConfigPlaceholders;
import com.braintribe.gm.model.reason.Maybe;
import com.braintribe.gm.model.reason.config.PropertyNotFound;
import com.braintribe.gm.model.reason.essential.NotFound;
import com.braintribe.model.generic.reflection.VdHolder;
import com.braintribe.model.bvd.convert.ToInteger;
import com.braintribe.model.bvd.convert.ToString;
import com.braintribe.model.bvd.string.Concatenation;
import com.braintribe.model.generic.value.Variable;
import com.braintribe.model.processing.vde.reasoned.api.ResidualValuePolicy;
import com.braintribe.model.processing.vde.reasoned.api.ValueDescriptorEvaluationPolicy;
import com.braintribe.model.resource.source.PackagedSource;
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
    public void preservesResidualDescriptorAsDirectTypedCollectionElement() {
        CollectionEntity source = CollectionEntity.T.createRaw();
        List<Object> rawValues = new ArrayList<>();
        rawValues.add(VdHolder.newInstance(variable("missing")));
        CollectionEntity.T.getProperty("stringList").setDirectUnsafe(source, rawValues);

        StandardValueDescriptorEvaluationContext context = nameContext(name -> PropertyNotFound.create(name).asMaybe());
        CollectionEntity result = new ReasonedValueDescriptorMaterializer(context,
                ResidualValuePolicy.preserving(reason -> NotFound.T.isInstance(reason))).materialize(source).get();

        Object residual = result.getStringList().get(0);
        assertThat(VdHolder.isVdHolder(residual)).isFalse();
        assertVariable(residual, "missing");
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

    /**
     * A transient attribute is a plain Java field rather than a property, so a clone only keeps it because the cloning transfers it explicitly. Without
     * that, a resolved {@link PackagedSource} keeps its address but loses the reader that makes it streamable.
     */
    @Test
    public void carriesTransientDataOntoTheClone() throws Exception {
        PackagedSource borrowed = PackagedSource.T.createRaw();
        borrowed.setArtifact("my-artifact");
        borrowed.setPath("HICONIC-CONF/logo.svg");
        borrowed.setInputStreamProvider(() -> new ByteArrayInputStream("payload".getBytes(StandardCharsets.UTF_8)));

        PackagedSource result = materializer(nameContext(name -> Maybe.complete(name))).materialize(borrowed).get();

        assertThat(result).isNotSameAs(borrowed);
        assertThat(result.getPath()).isEqualTo("HICONIC-CONF/logo.svg");
        assertThat(result.getInputStreamProvider()).isSameAs(borrowed.getInputStreamProvider());
        try (InputStream in = result.openStream()) {
            assertThat(new String(in.readAllBytes(), StandardCharsets.UTF_8)).isEqualTo("payload");
        }
    }

    /** An entity that reports no transient data is cloned as before, i.e. nothing is read from it that it does not offer. */
    @Test
    public void entityWithoutTransientDataIsUnaffected() {
        PackagedSource borrowed = PackagedSource.T.createRaw();
        borrowed.setArtifact("my-artifact");
        borrowed.setPath("HICONIC-CONF/logo.svg");

        PackagedSource result = materializer(nameContext(name -> Maybe.complete(name))).materialize(borrowed).get();

        assertThat(result.hasTransientData()).isFalse();
        assertThat(result.getInputStreamProvider()).isNull();
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

	@Test
	public void residualExpressionContainsResolvedSiblings() {
		Concatenation concatenation = Concatenation.T.create();
		concatenation.setOperands(Arrays.asList("prefix-", variable("KNOWN"), "-", variable("MISSING")));
		ToString expression = ToString.T.create();
		expression.setOperand(concatenation);

		LoadedEntity source = LoadedEntity.T.createRaw();
		LoadedEntity.T.getProperty("cpValue").setVdDirect(source, expression);

		StandardValueDescriptorEvaluationContext context = new StandardValueDescriptorEvaluationContext(
				ReasonedConfigPlaceholders.registry(variable -> "KNOWN".equals(variable.getName())
						? Maybe.complete("resolved")
						: PropertyNotFound.create(variable.getName()).asMaybe()));
		LoadedEntity result = new ReasonedValueDescriptorMaterializer(context,
				ResidualValuePolicy.preserving(reason -> NotFound.T.isInstance(reason))).materialize(source).get();

		ToString residual = LoadedEntity.T.getProperty("cpValue").getVdDirect(result);
		Concatenation residualConcatenation = (Concatenation) residual.getOperand();
		assertThat(residualConcatenation.getOperands().get(1)).isEqualTo("resolved");
		assertVariable(residualConcatenation.getOperands().get(3), "MISSING");

		Concatenation originalConcatenation = (Concatenation) expression.getOperand();
		assertVariable(originalConcatenation.getOperands().get(1), "KNOWN");
	}

	@Test
	public void deliberatelyDeferredDescriptorPreservesItsCompleteArgumentTree() {
		Variable inner = variable("RUNTIME_VALUE");
		ToString deferred = ToString.T.create();
		deferred.setOperand(inner);
		LoadedEntity source = LoadedEntity.T.createRaw();
		LoadedEntity.T.getProperty("cpValue").setVdDirect(source, deferred);

		AtomicBoolean innerEvaluated = new AtomicBoolean();
		StandardValueDescriptorEvaluationContext context = new StandardValueDescriptorEvaluationContext(
				ReasonedConfigPlaceholders.registry(variable -> {
					innerEvaluated.set(true);
					return Maybe.complete("must-not-be-materialized");
				}));
		context.withAspect(ValueDescriptorEvaluationPolicy.class,
				(_context, descriptor) -> descriptor instanceof ToString ? PropertyNotFound.create("RUNTIME_PREREQUISITE") : null);

		LoadedEntity result = new ReasonedValueDescriptorMaterializer(context,
				ResidualValuePolicy.preserving(reason -> NotFound.T.isInstance(reason))).materialize(source).get();

		ToString residual = LoadedEntity.T.getProperty("cpValue").getVdDirect(result);
		assertThat(residual).isNotSameAs(deferred);
		assertVariable(residual.getOperand(), "RUNTIME_VALUE");
		assertThat(innerEvaluated).isFalse();
	}

	@Test
	public void deliberatelyDeferredDescriptorPreservesNestedDescriptorInScalarProperty() {
		TestImportText inner = TestImportText.T.create();
		inner.setPath("./secret.encrypted");
		TestDecrypt deferred = TestDecrypt.T.create();
		TestDecrypt.T.getProperty("cipherText").setVdDirect(deferred, inner);
		LoadedEntity source = LoadedEntity.T.createRaw();
		LoadedEntity.T.getProperty("cpValue").setVdDirect(source, deferred);

		StandardValueDescriptorEvaluationContext context = new StandardValueDescriptorEvaluationContext(new ValueDescriptorExpertRegistry());
		context.withAspect(ValueDescriptorEvaluationPolicy.class,
				(_context, descriptor) -> descriptor instanceof TestDecrypt ? PropertyNotFound.create("RUNTIME_SECRET") : null);

		LoadedEntity result = new ReasonedValueDescriptorMaterializer(context,
				ResidualValuePolicy.preserving(reason -> NotFound.T.isInstance(reason))).materialize(source).get();

		TestDecrypt residual = LoadedEntity.T.getProperty("cpValue").getVdDirect(result);
		TestImportText residualInner = TestDecrypt.T.getProperty("cipherText").getVdDirect(residual);
		assertThat(residualInner).isNotNull().isNotSameAs(inner);
		assertThat(residualInner.getPath()).isEqualTo("./secret.encrypted");
	}

	@Test
	public void evaluatesAllIndependentCollectionElementsBeforeReportingErrors() {
		ToInteger firstInvalid = ToInteger.T.create();
		firstInvalid.setOperand("not-an-integer-one");
		ToInteger secondInvalid = ToInteger.T.create();
		secondInvalid.setOperand("not-an-integer-two");

		Concatenation concatenation = Concatenation.T.create();
		concatenation.setOperands(Arrays.asList(firstInvalid, secondInvalid));
		LoadedEntity source = LoadedEntity.T.createRaw();
		LoadedEntity.T.getProperty("cpValue").setVdDirect(source, concatenation);

		StandardValueDescriptorEvaluationContext context = new StandardValueDescriptorEvaluationContext(
				ReasonedConfigPlaceholders.registry(variable -> PropertyNotFound.create(variable.getName()).asMaybe()));
		Maybe<LoadedEntity> result = new ReasonedValueDescriptorMaterializer(context,
				ResidualValuePolicy.preserving(reason -> NotFound.T.isInstance(reason))).materialize(source);

		assertThat(result.isUnsatisfied()).isTrue();
		assertThat(result.whyUnsatisfied().stringify())
				.contains("not-an-integer-one")
				.contains("not-an-integer-two");
	}

	@Test
	public void evaluatesAllIndependentPropertiesBeforeReportingErrors() {
		LoadedEntity source = LoadedEntity.T.createRaw();
		ToInteger firstInvalid = ToInteger.T.create();
		firstInvalid.setOperand("invalid-property-one");
		LoadedEntity.T.getProperty("cpValue").setVdDirect(source, firstInvalid);
		ToInteger secondInvalid = ToInteger.T.create();
		secondInvalid.setOperand("invalid-property-two");
		LoadedEntity.T.getProperty("fs1Value").setVdDirect(source, secondInvalid);

		StandardValueDescriptorEvaluationContext context = new StandardValueDescriptorEvaluationContext(
				ReasonedConfigPlaceholders.registry(variable -> PropertyNotFound.create(variable.getName()).asMaybe()));
		Maybe<LoadedEntity> result = new ReasonedValueDescriptorMaterializer(context,
				ResidualValuePolicy.preserving(reason -> NotFound.T.isInstance(reason))).materialize(source);

		assertThat(result.isUnsatisfied()).isTrue();
		assertThat(result.whyUnsatisfied().stringify())
				.contains("invalid-property-one")
				.contains("invalid-property-two");
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

	private static void assertVariable(Object value, String name) {
		Object directValue = VdHolder.isVdHolder(value) ? ((VdHolder) value).vd : value;
		assertThat(directValue).isInstanceOf(Variable.class);
		assertThat(((Variable) directValue).getName()).isEqualTo(name);
	}

    @FunctionalInterface
    private interface NameResolution {
        Maybe<?> resolve(String name);
    }
}
