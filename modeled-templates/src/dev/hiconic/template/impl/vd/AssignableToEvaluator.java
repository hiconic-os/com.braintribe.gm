package dev.hiconic.template.impl.vd;

import com.braintribe.gm.model.reason.Maybe;
import com.braintribe.gm.model.reason.Reason;
import com.braintribe.gm.model.reason.essential.InvalidArgument;
import com.braintribe.model.generic.GMF;
import com.braintribe.model.generic.reflection.GenericModelType;

import dev.hiconic.template.api.TemplateEvaluationContext;
import dev.hiconic.template.api.ValidationContext;
import dev.hiconic.template.api.VdEvaluator;
import dev.hiconic.template.model.core.vd.AssignableTo;

public class AssignableToEvaluator implements VdEvaluator<AssignableTo, Boolean> {
	@Override
	public Maybe<Boolean> transform(TemplateEvaluationContext context, AssignableTo descriptor) {
		GenericModelType source = resolve(descriptor.getSource() == null ? null : descriptor.getSource().getTypeSignature());
		GenericModelType target = resolve(descriptor.getTarget() == null ? null : descriptor.getTarget().getTypeSignature());
		if (source == null || target == null)
			return Maybe.empty(InvalidArgument.create("AssignableTo source or target does not resolve to a reflected type"));
		return Maybe.complete(target.isAssignableFrom(source));
	}

	@Override
	public Reason complete(ValidationContext context, AssignableTo descriptor) {
		GenericModelType sourceType = context.getType(descriptor, AssignableTo.source);
		GenericModelType targetType = context.getType(descriptor, AssignableTo.target);
		if (sourceType == null || !dev.hiconic.template.model.core.TypeReference.T.isAssignableFrom(sourceType)
				|| targetType == null || !dev.hiconic.template.model.core.TypeReference.T.isAssignableFrom(targetType))
			return InvalidArgument.create("AssignableTo.source and target must evaluate to TypeReference");
		if (AssignableTo.source.property().getVdDirect(descriptor) == null && descriptor.getSource() == null
				|| AssignableTo.target.property().getVdDirect(descriptor) == null && descriptor.getTarget() == null)
			return InvalidArgument.create("AssignableTo.source and target must not be null");
		if (descriptor.getSource() != null && context.resolveType(descriptor.getSource().getTypeSignature()) == null)
			return InvalidArgument.create("Unknown AssignableTo source type: " + descriptor.getSource().getTypeSignature());
		return descriptor.getTarget() != null && context.resolveType(descriptor.getTarget().getTypeSignature()) == null
				? InvalidArgument.create("Unknown AssignableTo target type: " + descriptor.getTarget().getTypeSignature()) : null;
	}

	private static GenericModelType resolve(String signature) {
		return signature == null ? null : GMF.getTypeReflection().findType(signature);
	}
}
