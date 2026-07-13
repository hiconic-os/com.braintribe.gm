package dev.hiconic.template.impl.vd;

import com.braintribe.gm.model.reason.Maybe;
import com.braintribe.gm.model.reason.Reason;
import com.braintribe.gm.model.reason.essential.InvalidArgument;
import com.braintribe.model.generic.GMF;
import com.braintribe.model.generic.reflection.GenericModelType;

import dev.hiconic.template.api.TemplateEvaluationContext;
import dev.hiconic.template.api.ValidationContext;
import dev.hiconic.template.api.VdEvaluator;
import dev.hiconic.template.model.core.vd.Cast;
import dev.hiconic.template.model.core.vd.UnaryOperation;
import dev.hiconic.template.impl.parser.DefinitionTools;

public class CastEvaluator implements VdEvaluator<Cast, Object> {
	@Override
	public Maybe<Object> transform(TemplateEvaluationContext context, Cast descriptor) {
		Object value = descriptor.getOperand();
		if (value == null) return Maybe.complete(null);
		GenericModelType actual = GMF.getTypeReflection().getType(value);
		GenericModelType target = GMF.getTypeReflection().findType(descriptor.getTarget().getTypeSignature());
		return target != null && target.isAssignableFrom(actual) ? Maybe.complete(value)
				: Maybe.empty(InvalidArgument.create("Cannot cast " + actual.getTypeSignature() + " to "
						+ descriptor.getTarget().getTypeSignature()));
	}

	@Override
	public Reason complete(ValidationContext context, Cast descriptor) {
		if (descriptor.getTarget() == null) return InvalidArgument.create("Cast.target must not be null");
		GenericModelType source = context.getType(descriptor, UnaryOperation.operand);
		GenericModelType target = context.resolveType(descriptor.getTarget().getTypeSignature());
		if (source == null || target == null) return InvalidArgument.create("Cast source or target type is unknown");
		if (!source.isAssignableFrom(target) && !target.isAssignableFrom(source))
			return InvalidArgument.create("Statically impossible cast from " + source.getTypeSignature() + " to "
					+ target.getTypeSignature());
		descriptor.setInputType(DefinitionTools.type(source.getTypeSignature()));
		descriptor.setTypeSignature(target.getTypeSignature());
		return null;
	}
}
