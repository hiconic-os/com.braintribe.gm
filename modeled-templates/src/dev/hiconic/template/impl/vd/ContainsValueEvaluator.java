package dev.hiconic.template.impl.vd;

import java.util.Map;

import com.braintribe.gm.model.reason.Maybe;
import com.braintribe.gm.model.reason.Reason;
import com.braintribe.gm.model.reason.essential.InvalidArgument;
import com.braintribe.model.generic.reflection.GenericModelType;
import com.braintribe.model.generic.reflection.MapType;
import com.braintribe.model.generic.reflection.Property;

import dev.hiconic.template.api.TemplateEvaluationContext;
import dev.hiconic.template.api.ValidationContext;
import dev.hiconic.template.api.VdEvaluator;
import dev.hiconic.template.model.core.vd.ContainsValue;

public class ContainsValueEvaluator implements VdEvaluator<ContainsValue, Boolean> {
	@Override
	public GenericModelType expectedArgumentType(ValidationContext context, ContainsValue vd, Property property) {
		GenericModelType mapType = context.getType(vd, ContainsValue.map);
		return property == ContainsValue.value.property() && mapType instanceof MapType map ? map.getValueType() : null;
	}

	@Override
	public Maybe<Boolean> transform(TemplateEvaluationContext context, ContainsValue vd) {
		Object map = vd.getMap();
		return map instanceof Map<?, ?> values
				? Maybe.complete(values.containsValue(vd.getValue()))
				: Maybe.empty(InvalidArgument.create("ContainsValue.map is not a map"));
	}

	@Override
	public Reason validate(ValidationContext context, ContainsValue vd) {
		GenericModelType mapType = context.getType(vd, ContainsValue.map);
		if (!(mapType instanceof MapType map))
			return InvalidArgument.create("ContainsValue.map must evaluate to a map, but evaluates to " + typeSignature(mapType));
		GenericModelType valueType = context.getType(vd, ContainsValue.value);
		GenericModelType expected = map.getValueType();
		if (!context.isExplicitNull(vd, ContainsValue.value) && (valueType == null || !expected.isAssignableFrom(valueType)))
			return InvalidArgument.create("ContainsValue.value expects " + expected.getTypeSignature()
					+ " but got " + typeSignature(valueType));
		return null;
	}

	private static String typeSignature(GenericModelType type) {
		return type == null ? "<unknown>" : type.getTypeSignature();
	}
}
