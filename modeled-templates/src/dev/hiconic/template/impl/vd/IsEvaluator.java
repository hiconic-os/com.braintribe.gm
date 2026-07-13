package dev.hiconic.template.impl.vd;

import com.braintribe.gm.model.reason.Maybe;
import com.braintribe.gm.model.reason.Reason;
import com.braintribe.gm.model.reason.essential.InvalidArgument;
import com.braintribe.model.generic.GMF;
import com.braintribe.model.generic.reflection.GenericModelType;

import dev.hiconic.template.api.TemplateEvaluationContext;
import dev.hiconic.template.api.ValidationContext;
import dev.hiconic.template.api.VdEvaluator;
import dev.hiconic.template.model.core.vd.Is;

public class IsEvaluator implements VdEvaluator<Is, Boolean> {
	@Override
	public Maybe<Boolean> transform(TemplateEvaluationContext context, Is descriptor) {
		Object value = descriptor.getOperand();
		if (value == null) return Maybe.complete(false);
		GenericModelType target = resolve(descriptor.getTarget() == null ? null : descriptor.getTarget().getTypeSignature());
		if (target == null) return Maybe.empty(InvalidArgument.create("Is.target does not resolve to a reflected type"));
		GenericModelType actual = GMF.getTypeReflection().getType(value);
		return Maybe.complete(actual != null && target.isAssignableFrom(actual));
	}

	@Override
	public Reason complete(ValidationContext context, Is descriptor) {
		GenericModelType targetType = context.getType(descriptor, Is.target);
		if (targetType == null || !dev.hiconic.template.model.core.TypeReference.T.isAssignableFrom(targetType))
			return InvalidArgument.create("Is.target must evaluate to TypeReference");
		if (Is.target.property().getVdDirect(descriptor) != null) return null;
		if (descriptor.getTarget() == null) return InvalidArgument.create("Is.target must not be null");
		return context.resolveType(descriptor.getTarget().getTypeSignature()) == null
				? InvalidArgument.create("Unknown Is target type: " + descriptor.getTarget().getTypeSignature()) : null;
	}

	private static GenericModelType resolve(String signature) {
		return signature == null ? null : GMF.getTypeReflection().findType(signature);
	}
}
