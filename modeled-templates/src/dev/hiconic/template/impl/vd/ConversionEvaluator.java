package dev.hiconic.template.impl.vd;

import com.braintribe.gm.model.reason.Maybe;
import com.braintribe.gm.model.reason.Reason;
import com.braintribe.gm.model.reason.essential.InvalidArgument;
import com.braintribe.model.generic.reflection.GenericModelType;
import com.braintribe.model.generic.GMF;

import dev.hiconic.template.api.TemplateEvaluationContext;
import dev.hiconic.template.api.TemplateExpertRegistry;
import dev.hiconic.template.api.ValidationContext;
import dev.hiconic.template.api.ValueConversion;
import dev.hiconic.template.api.ValueConversionBinding;
import dev.hiconic.template.api.VdEvaluator;
import dev.hiconic.template.impl.parser.DefinitionTools;
import dev.hiconic.template.model.core.vd.UnaryOperation;

/** Evaluates ordinary unary VDs which are also registered as conversion edges. */
public class ConversionEvaluator<V extends UnaryOperation> implements VdEvaluator<V, Object> {
	private final TemplateExpertRegistry registry;

	public ConversionEvaluator(TemplateExpertRegistry registry) { this.registry = registry; }

	@Override
	public Maybe<Object> transform(TemplateEvaluationContext context, V descriptor) {
		GenericModelType inputType = descriptor.getInputType() == null ? null
				: GMF.getTypeReflection().findType(descriptor.getInputType().getTypeSignature());
		ValueConversionBinding binding = inputType == null ? null : registry.findConversion(descriptor.entityType(), inputType);
		if (binding == null)
			return Maybe.empty(InvalidArgument.create("No value conversion registered for "
					+ descriptor.entityType().getTypeSignature()));
		return convert(binding, context, descriptor, descriptor.getOperand());
	}

	@Override
	public Reason complete(ValidationContext context, V descriptor) {
		GenericModelType inputType = context.getType(descriptor, UnaryOperation.operand);
		if (inputType == null) return InvalidArgument.create(descriptor.entityType().getShortName() + ".operand type is unknown");
		ValueConversionBinding binding = registry.findConversion(descriptor.entityType(), inputType);
		if (binding == null)
			return InvalidArgument.create("No value conversion registered for " + descriptor.entityType().getTypeSignature()
					+ " accepting " + inputType.getTypeSignature());
		descriptor.setInputType(DefinitionTools.type(inputType.getTypeSignature()));
		descriptor.setTypeSignature(binding.outputType().getTypeSignature());
		return validate(binding, context, descriptor);
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	private Maybe<Object> convert(ValueConversionBinding binding, TemplateEvaluationContext context, V descriptor, Object input) {
		ValueConversion expert = binding.expert();
		return expert.convert(context, input, descriptor);
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	private Reason validate(ValueConversionBinding binding, ValidationContext context, V descriptor) {
		ValueConversion expert = binding.expert();
		return expert.validate(context, descriptor);
	}
}
