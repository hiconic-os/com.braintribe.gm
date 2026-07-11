package dev.hiconic.template.impl;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.GenericModelType;
import com.braintribe.model.generic.value.ValueDescriptor;

import dev.hiconic.template.api.TemplateExpertRegistry;
import dev.hiconic.template.api.TemplateNodeEvaluator;
import dev.hiconic.template.api.TemplateValueTransformer;
import dev.hiconic.template.api.TransformerBinding;
import dev.hiconic.template.api.VdEvaluator;
import dev.hiconic.template.model.core.TemplateNode;
import dev.hiconic.template.model.core.output.Transformer;

public class ConfigurableTemplateExpertRegistry implements TemplateExpertRegistry {
	private final Map<EntityType<?>, TemplateNodeEvaluator<?>> evaluators = new HashMap<>();
	private final Map<EntityType<?>, VdEvaluator<?, ?>> vdEvaluators = new HashMap<>();
	private final Map<TransformerKey, TemplateValueTransformer<?, ?, ?>> transformers = new HashMap<>();
	private final List<TransformerBinding> transformerBindings = new ArrayList<>();

	public ConfigurableTemplateExpertRegistry() {
	}

	private ConfigurableTemplateExpertRegistry(ConfigurableTemplateExpertRegistry source) {
		evaluators.putAll(source.evaluators);
		vdEvaluators.putAll(source.vdEvaluators);
		transformers.putAll(source.transformers);
		transformerBindings.addAll(source.transformerBindings);
	}

	public ConfigurableTemplateExpertRegistry copy() {
		return new ConfigurableTemplateExpertRegistry(this);
	}

	@Override
	public <T extends Transformer, I, O> void registerTransformer(EntityType<T> transformerType, GenericModelType inType,
			GenericModelType outType, TemplateValueTransformer<I, T, O> transformer) {
		registerTransformer(transformerType, inType, outType, transformer, false);
	}

	@Override
	public <T extends Transformer, I, O> void registerDefaultTransformer(EntityType<T> transformerType, GenericModelType inType,
			GenericModelType outType, TemplateValueTransformer<I, T, O> transformer) {
		registerTransformer(transformerType, inType, outType, transformer, true);
	}

	private <T extends Transformer, I, O> void registerTransformer(EntityType<T> transformerType, GenericModelType inType,
			GenericModelType outType, TemplateValueTransformer<I, T, O> transformer, boolean defaultConversion) {
		TransformerKey key = new TransformerKey(transformerType, inType, outType);
		transformers.put(key, transformer);
		transformerBindings.removeIf(binding -> key.matches(binding));
		transformerBindings.add(new TransformerBinding(transformerType, inType, outType, transformer, defaultConversion));
	}

	@Override
	public <N extends TemplateNode> void registerEvaluator(EntityType<N> nodeType, TemplateNodeEvaluator<N> evaluator) {
		evaluators.put(nodeType, evaluator);
	}

	@Override
	public <V extends ValueDescriptor, O> void registerVdEvaluator(EntityType<V> descriptorType, VdEvaluator<V, O> evaluator) {
		vdEvaluators.put(descriptorType, evaluator);
	}

	@Override
	public <T extends Transformer, I, O> void registerInstruction(EntityType<T> transformerType, GenericModelType inType,
			GenericModelType outType, TemplateValueTransformer<I, T, O> transformer) {
		registerTransformer(transformerType, inType, outType, transformer);
	}

	@Override
	public TemplateNodeEvaluator<?> findEvaluator(EntityType<?> nodeType) {
		return evaluators.get(nodeType);
	}

	@Override
	public VdEvaluator<?, ?> findVdEvaluator(EntityType<?> descriptorType) {
		return vdEvaluators.get(descriptorType);
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<EntityType<? extends ValueDescriptor>> valueDescriptorTypes() {
		Set<EntityType<? extends ValueDescriptor>> result = new LinkedHashSet<>();
		for (EntityType<?> type : vdEvaluators.keySet())
			result.add((EntityType<? extends ValueDescriptor>) type);
		return List.copyOf(result);
	}

	public TemplateValueTransformer<?, ?, ?> findTransformer(EntityType<?> transformerType, GenericModelType inputType,
			GenericModelType outputType) {
		return transformers.get(new TransformerKey(transformerType, inputType, outputType));
	}

	@Override
	public TransformerBinding findTransformer(EntityType<?> transformerType, GenericModelType inputType) {
		for (TransformerBinding binding : transformerBindings)
			if (binding.transformerType().isAssignableFrom(transformerType)
					&& binding.inputType().isAssignableFrom(inputType))
				return binding;
		return null;
	}

	@Override
	public List<TransformerBinding> findTransformers(EntityType<?> transformerType) {
		List<TransformerBinding> result = new ArrayList<>();
		for (TransformerBinding binding : transformerBindings)
			if (binding.transformerType().isAssignableFrom(transformerType))
				result.add(binding);
		return Collections.unmodifiableList(result);
	}

	@Override
	public List<TransformerBinding> defaultTransformers() {
		List<TransformerBinding> result = new ArrayList<>();
		for (TransformerBinding binding : transformerBindings)
			if (binding.defaultConversion())
				result.add(binding);
		return Collections.unmodifiableList(result);
	}

	@Override
	public List<EntityType<? extends Transformer>> transformerTypes() {
		Set<EntityType<? extends Transformer>> result = new LinkedHashSet<>();
		for (TransformerBinding binding : transformerBindings)
			result.add(binding.transformerType());
		return List.copyOf(result);
	}

	private static final class TransformerKey {
		private final EntityType<?> transformerType;
		private final GenericModelType inputType;
		private final GenericModelType outputType;

		private TransformerKey(EntityType<?> transformerType, GenericModelType inputType, GenericModelType outputType) {
			this.transformerType = transformerType;
			this.inputType = inputType;
			this.outputType = outputType;
		}

		@Override
		public int hashCode() {
			return Objects.hash(transformerType, inputType, outputType);
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (!(obj instanceof TransformerKey))
				return false;
			TransformerKey other = (TransformerKey) obj;
			return Objects.equals(transformerType, other.transformerType)
					&& Objects.equals(inputType, other.inputType)
					&& Objects.equals(outputType, other.outputType);
		}

		private boolean matches(TransformerBinding binding) {
			return Objects.equals(transformerType, binding.transformerType())
					&& Objects.equals(inputType, binding.inputType())
					&& Objects.equals(outputType, binding.outputType());
		}
	}
}
