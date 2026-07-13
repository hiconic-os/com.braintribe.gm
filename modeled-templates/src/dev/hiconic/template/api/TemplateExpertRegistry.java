package dev.hiconic.template.api;

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.GenericModelType;
import com.braintribe.model.generic.value.ValueDescriptor;

import dev.hiconic.template.model.core.TemplateNode;
public interface TemplateExpertRegistry {
	<V extends ValueDescriptor, I, O> void registerConversion(EntityType<V> descriptorType, GenericModelType inType,
			GenericModelType outType, ValueConversion<I, V, O> conversion);
	default <V extends ValueDescriptor, I, O> void registerDefaultConversion(EntityType<V> descriptorType,
			GenericModelType inType, GenericModelType outType, ValueConversion<I, V, O> conversion) {
		registerConversion(descriptorType, inType, outType, conversion);
	}
	<N extends TemplateNode> void registerEvaluator(EntityType<N> nodeType, TemplateNodeEvaluator<N> transformer);
	<E extends com.braintribe.model.generic.GenericEntity> void registerScalarParser(EntityType<E> entityType, ScalarEntityParser<E> parser);
	<V extends ValueDescriptor, O> void registerVdEvaluator(EntityType<V> descriptorType, VdEvaluator<V, O> evaluator);

	default TemplateNodeEvaluator<?> findEvaluator(EntityType<?> nodeType) {
		return null;
	}

	default VdEvaluator<?, ?> findVdEvaluator(EntityType<?> descriptorType) {
		return null;
	}
	default ScalarEntityParser<?> findScalarParser(EntityType<?> entityType) { return null; }

	/** Entity types known through scalar parsers and therefore available to the
	 * generic entity/alias resolver even when no model CMD resolver is present. */
	default java.util.List<EntityType<?>> scalarEntityTypes() {
		return java.util.List.of();
	}

	default java.util.List<EntityType<? extends ValueDescriptor>> valueDescriptorTypes() {
		return java.util.List.of();
	}

	default ValueConversionBinding findConversion(EntityType<?> descriptorType, GenericModelType inputType) {
		return null;
	}

	default java.util.List<ValueConversionBinding> findConversions(EntityType<?> descriptorType) {
		return java.util.List.of();
	}

	default java.util.List<ValueConversionBinding> defaultConversions() {
		return java.util.List.of();
	}

	default java.util.List<EntityType<? extends ValueDescriptor>> conversionTypes() {
		return java.util.List.of();
	}
}
