package dev.hiconic.template.api;

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.GenericModelType;
import com.braintribe.model.generic.value.ValueDescriptor;

import dev.hiconic.template.model.core.TemplateNode;
import dev.hiconic.template.model.core.output.Transformer;

public interface TemplateExpertRegistry {
	<T extends Transformer, I, O> void registerTransformer(EntityType<T> transformerType, GenericModelType inType, GenericModelType outType, TemplateValueTransformer<I, T, O> transformer);
	default <T extends Transformer, I, O> void registerDefaultTransformer(EntityType<T> transformerType, GenericModelType inType, GenericModelType outType, TemplateValueTransformer<I, T, O> transformer) {
		registerTransformer(transformerType, inType, outType, transformer);
	}
	<N extends TemplateNode> void registerEvaluator(EntityType<N> nodeType, TemplateNodeEvaluator<N> transformer);
	<V extends ValueDescriptor, O> void registerVdEvaluator(EntityType<V> descriptorType, VdEvaluator<V, O> evaluator);
	<T extends Transformer, I, O> void registerInstruction(EntityType<T> transformerType, GenericModelType inType, GenericModelType outType, TemplateValueTransformer<I, T, O> transformer);

	default TemplateNodeEvaluator<?> findEvaluator(EntityType<?> nodeType) {
		return null;
	}

	default VdEvaluator<?, ?> findVdEvaluator(EntityType<?> descriptorType) {
		return null;
	}

	default java.util.List<EntityType<? extends ValueDescriptor>> valueDescriptorTypes() {
		return java.util.List.of();
	}

	default TransformerBinding findTransformer(EntityType<?> transformerType, GenericModelType inputType) {
		return null;
	}

	default java.util.List<TransformerBinding> findTransformers(EntityType<?> transformerType) {
		return java.util.List.of();
	}

	default java.util.List<TransformerBinding> defaultTransformers() {
		return java.util.List.of();
	}

	default java.util.List<EntityType<? extends Transformer>> transformerTypes() {
		return java.util.List.of();
	}
}
