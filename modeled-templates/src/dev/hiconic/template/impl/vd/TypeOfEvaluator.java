package dev.hiconic.template.impl.vd;

import com.braintribe.gm.model.reason.Maybe;
import com.braintribe.gm.model.reason.Reason;
import com.braintribe.gm.model.reason.essential.InvalidArgument;
import com.braintribe.model.generic.GMF;
import com.braintribe.model.generic.reflection.GenericModelType;

import dev.hiconic.template.api.TemplateEvaluationContext;
import dev.hiconic.template.api.ValidationContext;
import dev.hiconic.template.api.VdEvaluator;
import dev.hiconic.template.impl.parser.DefinitionTools;
import dev.hiconic.template.model.core.TypeReference;
import dev.hiconic.template.model.core.vd.TypeOf;
import dev.hiconic.template.model.core.vd.UnaryOperation;

public class TypeOfEvaluator implements VdEvaluator<TypeOf, TypeReference> {
	@Override
	public Maybe<TypeReference> transform(TemplateEvaluationContext context, TypeOf descriptor) {
		Object value = descriptor.getOperand();
		if (value == null)
			return Maybe.empty(InvalidArgument.create("TypeOf.operand is null and has no concrete runtime type"));
		GenericModelType type = GMF.getTypeReflection().getType(value);
		return type == null
				? Maybe.empty(InvalidArgument.create("No reflected runtime type exists for " + value.getClass().getName()))
				: Maybe.complete(DefinitionTools.type(type.getTypeSignature()));
	}

	@Override
	public Reason complete(ValidationContext context, TypeOf descriptor) {
		GenericModelType input = context.getType(descriptor, UnaryOperation.operand);
		if (input == null) return InvalidArgument.create("TypeOf operand type is unknown");
		descriptor.setInputType(DefinitionTools.type(input.getTypeSignature()));
		descriptor.setTypeSignature(TypeReference.T.getTypeSignature());
		return null;
	}
}
