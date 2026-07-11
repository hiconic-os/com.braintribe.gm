package dev.hiconic.template.api;

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.GenericModelType;

import dev.hiconic.template.model.core.output.Transformer;

public final class TransformerBinding {
	private final EntityType<? extends Transformer> transformerType;
	private final GenericModelType inputType;
	private final GenericModelType outputType;
	private final TemplateValueTransformer<?, ?, ?> expert;
	private final boolean defaultConversion;

	public TransformerBinding(EntityType<? extends Transformer> transformerType, GenericModelType inputType,
			GenericModelType outputType, TemplateValueTransformer<?, ?, ?> expert, boolean defaultConversion) {
		this.transformerType = transformerType;
		this.inputType = inputType;
		this.outputType = outputType;
		this.expert = expert;
		this.defaultConversion = defaultConversion;
	}

	public EntityType<? extends Transformer> transformerType() {
		return transformerType;
	}

	public GenericModelType inputType() {
		return inputType;
	}

	public GenericModelType outputType() {
		return outputType;
	}

	public TemplateValueTransformer<?, ?, ?> expert() {
		return expert;
	}

	public boolean defaultConversion() {
		return defaultConversion;
	}
}
