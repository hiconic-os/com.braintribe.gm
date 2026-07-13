package dev.hiconic.template.impl;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.Property;
import com.braintribe.model.generic.reflection.PropertyAccessInterceptor;
import com.braintribe.model.generic.reflection.VdHolder;
import com.braintribe.model.generic.value.ValueDescriptor;

import dev.hiconic.template.api.TemplateEvaluationContext;

public class EvaluationPai extends PropertyAccessInterceptor {
	
	@Override
	public Object getProperty(Property property, GenericEntity entity, boolean isVd) {
		Object value = property.getDirectUnsafe(entity);

		ValueDescriptor vd = descriptor(value);

		if (!TemplateEvaluationContext.CURRENT.isBound())
			return vd == null ? value : null;

		return TemplateValues.evaluate(TemplateEvaluationContext.CURRENT.get(), value);
	}

	private static ValueDescriptor descriptor(Object value) {
		if (value instanceof ValueDescriptor descriptor)
			return descriptor;
		return VdHolder.getValueDescriptorIfPossible(value);
	}
}
