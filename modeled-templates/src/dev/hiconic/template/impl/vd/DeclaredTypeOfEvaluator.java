package dev.hiconic.template.impl.vd;

import com.braintribe.gm.model.reason.Maybe;
import com.braintribe.gm.model.reason.Reason;
import com.braintribe.gm.model.reason.essential.InvalidArgument;
import com.braintribe.model.generic.reflection.GenericModelType;

import dev.hiconic.template.api.TemplateEvaluationContext;
import dev.hiconic.template.api.ValidationContext;
import dev.hiconic.template.api.VdEvaluator;
import dev.hiconic.template.impl.parser.DefinitionTools;
import dev.hiconic.template.model.core.TypeReference;
import dev.hiconic.template.model.core.vd.DeclaredTypeOf;
import dev.hiconic.template.model.core.vd.UnaryOperation;

public class DeclaredTypeOfEvaluator implements VdEvaluator<DeclaredTypeOf, TypeReference> {
	@Override
	public Maybe<TypeReference> transform(TemplateEvaluationContext context, DeclaredTypeOf descriptor) {
		return descriptor.getInputType() == null
				? Maybe.empty(InvalidArgument.create("DeclaredTypeOf has not been completed"))
				: Maybe.complete(descriptor.getInputType());
	}

	@Override
	public Reason complete(ValidationContext context, DeclaredTypeOf descriptor) {
		GenericModelType input = context.getType(descriptor, UnaryOperation.operand);
		if (input == null) return InvalidArgument.create("DeclaredTypeOf operand type is unknown");
		descriptor.setInputType(DefinitionTools.type(input.getTypeSignature()));
		descriptor.setTypeSignature(TypeReference.T.getTypeSignature());
		return null;
	}
}
