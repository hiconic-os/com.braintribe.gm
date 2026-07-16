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
import dev.hiconic.template.model.core.vd.ContainsKey;

public class ContainsKeyEvaluator implements VdEvaluator<ContainsKey, Boolean> {
	@Override
	public GenericModelType expectedArgumentType(ValidationContext context, ContainsKey vd, Property property) {
		GenericModelType mapType = context.getType(vd, ContainsKey.map);
		return property == ContainsKey.key.property() && mapType instanceof MapType map ? map.getKeyType() : null;
	}

	@Override
	public Maybe<Boolean> transform(TemplateEvaluationContext context, ContainsKey vd) {
		Object map = vd.getMap();
		return map instanceof Map<?, ?> values
				? Maybe.complete(values.containsKey(vd.getKey()))
				: Maybe.empty(InvalidArgument.create("ContainsKey.map is not a map"));
	}

	@Override
	public Reason validate(ValidationContext context, ContainsKey vd) {
		GenericModelType mapType = context.getType(vd, ContainsKey.map);
		if (!(mapType instanceof MapType map))
			return InvalidArgument.create("ContainsKey.map must evaluate to a map, but evaluates to " + typeSignature(mapType));
		GenericModelType keyType = context.getType(vd, ContainsKey.key);
		GenericModelType expected = map.getKeyType();
		if (!context.isExplicitNull(vd, ContainsKey.key) && (keyType == null || !expected.isAssignableFrom(keyType)))
			return InvalidArgument.create("ContainsKey.key expects " + expected.getTypeSignature()
					+ " but got " + typeSignature(keyType));
		return null;
	}

	private static String typeSignature(GenericModelType type) {
		return type == null ? "<unknown>" : type.getTypeSignature();
	}
}
