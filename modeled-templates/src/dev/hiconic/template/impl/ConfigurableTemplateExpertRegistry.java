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
import dev.hiconic.template.api.ScalarEntityParser;
import dev.hiconic.template.api.TemplateNodeEvaluator;
import dev.hiconic.template.api.ValueConversion;
import dev.hiconic.template.api.ValueConversionBinding;
import dev.hiconic.template.api.VdEvaluator;
import dev.hiconic.template.model.core.TemplateNode;

public class ConfigurableTemplateExpertRegistry implements TemplateExpertRegistry {
	private final Map<EntityType<?>, TemplateNodeEvaluator<?>> evaluators = new HashMap<>();
	private final Map<EntityType<?>, VdEvaluator<?, ?>> vdEvaluators = new HashMap<>();
	private final Map<EntityType<?>, ScalarEntityParser<?>> scalarParsers = new HashMap<>();
	private final Map<ConversionKey, ValueConversion<?, ?, ?>> conversions = new HashMap<>();
	private final List<ValueConversionBinding> conversionBindings = new ArrayList<>();

	public ConfigurableTemplateExpertRegistry() {
	}

	private ConfigurableTemplateExpertRegistry(ConfigurableTemplateExpertRegistry source) {
		evaluators.putAll(source.evaluators);
		vdEvaluators.putAll(source.vdEvaluators);
		scalarParsers.putAll(source.scalarParsers);
		conversions.putAll(source.conversions);
		conversionBindings.addAll(source.conversionBindings);
	}

	public ConfigurableTemplateExpertRegistry copy() {
		return new ConfigurableTemplateExpertRegistry(this);
	}

	@Override
	public <V extends ValueDescriptor, I, O> void registerConversion(EntityType<V> descriptorType, GenericModelType inType,
			GenericModelType outType, ValueConversion<I, V, O> conversion) {
		registerConversion(descriptorType, inType, outType, conversion, false);
	}

	@Override
	public <V extends ValueDescriptor, I, O> void registerDefaultConversion(EntityType<V> descriptorType, GenericModelType inType,
			GenericModelType outType, ValueConversion<I, V, O> conversion) {
		registerConversion(descriptorType, inType, outType, conversion, true);
	}

	private <V extends ValueDescriptor, I, O> void registerConversion(EntityType<V> descriptorType, GenericModelType inType,
			GenericModelType outType, ValueConversion<I, V, O> conversion, boolean defaultConversion) {
		ConversionKey key = new ConversionKey(descriptorType, inType, outType);
		conversions.put(key, conversion);
		conversionBindings.removeIf(binding -> key.matches(binding));
		conversionBindings.add(new ValueConversionBinding(descriptorType, inType, outType, conversion, defaultConversion));
	}

	@Override
	public <N extends TemplateNode> void registerEvaluator(EntityType<N> nodeType, TemplateNodeEvaluator<N> evaluator) {
		evaluators.put(nodeType, evaluator);
	}

	@Override
	public <E extends com.braintribe.model.generic.GenericEntity> void registerScalarParser(EntityType<E> entityType, ScalarEntityParser<E> parser) {
		scalarParsers.put(entityType, parser);
	}

	@Override
	public ScalarEntityParser<?> findScalarParser(EntityType<?> entityType) { return scalarParsers.get(entityType); }

	@Override
	public List<EntityType<?>> scalarEntityTypes() { return List.copyOf(scalarParsers.keySet()); }

	@Override
	public <V extends ValueDescriptor, O> void registerVdEvaluator(EntityType<V> descriptorType, VdEvaluator<V, O> evaluator) {
		vdEvaluators.put(descriptorType, evaluator);
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

	public ValueConversion<?, ?, ?> findConversion(EntityType<?> descriptorType, GenericModelType inputType,
			GenericModelType outputType) {
		return conversions.get(new ConversionKey(descriptorType, inputType, outputType));
	}

	@Override
	public ValueConversionBinding findConversion(EntityType<?> descriptorType, GenericModelType inputType) {
		for (ValueConversionBinding binding : conversionBindings)
			if (binding.descriptorType().isAssignableFrom(descriptorType)
					&& binding.inputType().isAssignableFrom(inputType))
				return binding;
		return null;
	}

	@Override
	public List<ValueConversionBinding> findConversions(EntityType<?> descriptorType) {
		List<ValueConversionBinding> result = new ArrayList<>();
		for (ValueConversionBinding binding : conversionBindings)
			if (binding.descriptorType().isAssignableFrom(descriptorType))
				result.add(binding);
		return Collections.unmodifiableList(result);
	}

	@Override
	public List<ValueConversionBinding> defaultConversions() {
		List<ValueConversionBinding> result = new ArrayList<>();
		for (ValueConversionBinding binding : conversionBindings)
			if (binding.defaultConversion())
				result.add(binding);
		return Collections.unmodifiableList(result);
	}

	@Override
	public List<EntityType<? extends ValueDescriptor>> conversionTypes() {
		Set<EntityType<? extends ValueDescriptor>> result = new LinkedHashSet<>();
		for (ValueConversionBinding binding : conversionBindings)
			result.add(binding.descriptorType());
		return List.copyOf(result);
	}

	private static final class ConversionKey {
		private final EntityType<?> descriptorType;
		private final GenericModelType inputType;
		private final GenericModelType outputType;

		private ConversionKey(EntityType<?> descriptorType, GenericModelType inputType, GenericModelType outputType) {
			this.descriptorType = descriptorType;
			this.inputType = inputType;
			this.outputType = outputType;
		}

		@Override
		public int hashCode() {
			return Objects.hash(descriptorType, inputType, outputType);
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (!(obj instanceof ConversionKey))
				return false;
			ConversionKey other = (ConversionKey) obj;
			return Objects.equals(descriptorType, other.descriptorType)
					&& Objects.equals(inputType, other.inputType)
					&& Objects.equals(outputType, other.outputType);
		}

		private boolean matches(ValueConversionBinding binding) {
			return Objects.equals(descriptorType, binding.descriptorType())
					&& Objects.equals(inputType, binding.inputType())
					&& Objects.equals(outputType, binding.outputType());
		}
	}
}
