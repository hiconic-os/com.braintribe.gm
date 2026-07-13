package dev.hiconic.template.api;

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.GenericModelType;
import com.braintribe.model.generic.value.ValueDescriptor;

public final class ValueConversionBinding {
	private final EntityType<? extends ValueDescriptor> descriptorType;
	private final GenericModelType inputType;
	private final GenericModelType outputType;
	private final ValueConversion<?, ?, ?> expert;
	private final boolean defaultConversion;

	public ValueConversionBinding(EntityType<? extends ValueDescriptor> descriptorType, GenericModelType inputType,
			GenericModelType outputType, ValueConversion<?, ?, ?> expert, boolean defaultConversion) {
		this.descriptorType = descriptorType;
		this.inputType = inputType;
		this.outputType = outputType;
		this.expert = expert;
		this.defaultConversion = defaultConversion;
	}

	public EntityType<? extends ValueDescriptor> descriptorType() { return descriptorType; }
	public GenericModelType inputType() { return inputType; }
	public GenericModelType outputType() { return outputType; }
	public ValueConversion<?, ?, ?> expert() { return expert; }
	public boolean defaultConversion() { return defaultConversion; }
}
