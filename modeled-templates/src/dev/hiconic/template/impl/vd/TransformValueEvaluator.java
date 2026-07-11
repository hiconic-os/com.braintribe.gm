package dev.hiconic.template.impl.vd;

import com.braintribe.gm.model.reason.Maybe;
import com.braintribe.gm.model.reason.Reason;
import com.braintribe.gm.model.reason.essential.InvalidArgument;
import com.braintribe.model.generic.GMF;
import com.braintribe.model.generic.reflection.GenericModelType;
import com.braintribe.model.generic.reflection.VdHolder;
import com.braintribe.model.generic.value.ValueDescriptor;

import dev.hiconic.template.api.TemplateEvaluationContext;
import dev.hiconic.template.api.TemplateExpertRegistry;
import dev.hiconic.template.api.TemplateValueTransformer;
import dev.hiconic.template.api.TransformerBinding;
import dev.hiconic.template.api.ValidationContext;
import dev.hiconic.template.api.VdEvaluator;
import dev.hiconic.template.model.core.vd.TransformValue;

public class TransformValueEvaluator implements VdEvaluator<TransformValue, Object> {
	private final TemplateExpertRegistry registry;

	public TransformValueEvaluator(TemplateExpertRegistry registry) {
		this.registry = registry;
	}

	@Override
	public Maybe<Object> transform(TemplateEvaluationContext context, TransformValue descriptor) {
		TransformerBinding binding = binding(descriptor);
		if (binding == null)
			return Maybe.empty(InvalidArgument.create("No transformer registered for "
					+ descriptor.getTransformer().entityType().getTypeSignature() + " accepting "
					+ descriptor.getInputTypeSignature()));

		Object input = descriptor.getInput();
		ValueDescriptor inputDescriptor = VdHolder.getValueDescriptorIfPossible(input);
		if (inputDescriptor != null)
			input = context.evaluate(inputDescriptor);

		return transform(binding, context, descriptor, input);
	}

	@Override
	public Reason complete(ValidationContext context, TransformValue descriptor) {
		if (descriptor.getTransformer() == null)
			return InvalidArgument.create("TransformValue.transformer must not be null");

		GenericModelType inputType = descriptor.getInputTypeSignature() == null
				? VdValidation.getType(context, descriptor, TransformValue.input)
				: context.resolveType(descriptor.getInputTypeSignature());
		if (inputType == null)
			return InvalidArgument.create("TransformValue.input type is unknown");

		TransformerBinding binding = registry.findTransformer(descriptor.getTransformer().entityType(), inputType);
		if (binding == null)
			return InvalidArgument.create("No transformer registered for "
					+ descriptor.getTransformer().entityType().getTypeSignature() + " accepting "
					+ inputType.getTypeSignature());

		descriptor.setInputTypeSignature(inputType.getTypeSignature());
		descriptor.setTypeSignature(binding.outputType().getTypeSignature());
		return validateTransformer(context, descriptor, binding);
	}

	private TransformerBinding binding(TransformValue descriptor) {
		GenericModelType inputType = descriptor.getInputTypeSignature() == null
				? null
				: GMF.getTypeReflection().findType(descriptor.getInputTypeSignature());
		return inputType == null ? null : registry.findTransformer(descriptor.getTransformer().entityType(), inputType);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private Maybe<Object> transform(TransformerBinding binding, TemplateEvaluationContext context, TransformValue descriptor,
			Object input) {
		TemplateValueTransformer expert = binding.expert();
		return expert.transform(context, descriptor.getTransformer(), input);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private Reason validateTransformer(ValidationContext context, TransformValue descriptor, TransformerBinding binding) {
		TemplateValueTransformer expert = binding.expert();
		return expert.validate(context, descriptor.getTransformer(), null);
	}
}
