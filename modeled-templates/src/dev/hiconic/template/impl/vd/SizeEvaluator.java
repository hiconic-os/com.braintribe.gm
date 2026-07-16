package dev.hiconic.template.impl.vd;

import java.util.Collection;
import java.util.Map;

import com.braintribe.gm.model.reason.Maybe;
import com.braintribe.gm.model.reason.Reason;
import com.braintribe.gm.model.reason.essential.InvalidArgument;
import com.braintribe.model.generic.reflection.CollectionType;
import com.braintribe.model.generic.reflection.GenericModelType;
import com.braintribe.model.generic.reflection.MapType;

import dev.hiconic.template.api.TemplateEvaluationContext;
import dev.hiconic.template.api.ValidationContext;
import dev.hiconic.template.api.VdEvaluator;
import dev.hiconic.template.impl.parser.DefinitionTools;
import dev.hiconic.template.model.core.vd.Size;

public class SizeEvaluator implements VdEvaluator<Size, Integer> {
	@Override
	public Maybe<Integer> transform(TemplateEvaluationContext context, Size vd) {
		Object value = vd.getOperand();
		if (value instanceof Collection<?> collection) return Maybe.complete(collection.size());
		if (value instanceof Map<?, ?> map) return Maybe.complete(map.size());
		return Maybe.empty(InvalidArgument.create("Size.operand is neither a collection nor a map"));
	}

	@Override
	public Reason complete(ValidationContext context, Size vd) {
		GenericModelType input = context.getType(vd, Size.operand);
		if (!(input instanceof CollectionType) && !(input instanceof MapType))
			return InvalidArgument.create("Size.operand must evaluate to a collection or map, but evaluates to "
					+ typeSignature(input));
		vd.setInputType(DefinitionTools.type(input.getTypeSignature()));
		return null;
	}

	private static String typeSignature(GenericModelType type) {
		return type == null ? "<unknown>" : type.getTypeSignature();
	}
}
