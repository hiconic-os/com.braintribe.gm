package dev.hiconic.template.impl.node;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import com.braintribe.gm.model.reason.Reason;
import com.braintribe.gm.model.reason.essential.InvalidArgument;
import com.braintribe.model.generic.value.ValueDescriptor;

import dev.hiconic.template.api.TemplateEvaluationContext;
import dev.hiconic.template.api.TemplateNodeEvaluator;
import dev.hiconic.template.api.ValidationContext;
import dev.hiconic.template.impl.RuntimeTemplateEvaluationContext;
import dev.hiconic.template.impl.TemplateValues;
import dev.hiconic.template.model.core.decl.RuntimeArguments;
import dev.hiconic.template.model.core.decl.RuntimePropertySpecification;
import dev.hiconic.template.model.core.decl.RuntimePropertyValue;
import dev.hiconic.template.model.core.instr.InvokeInstruction;

public class InvokeInstructionEvaluator implements TemplateNodeEvaluator<InvokeInstruction> {
	@Override
	public void evaluate(TemplateEvaluationContext context, InvokeInstruction node) {
		Map<String, Object> parameters = new LinkedHashMap<>();
		for (RuntimePropertySpecification specification : node.getDeclaration().getArgumentType().getProperties()) {
			ValueDescriptor descriptor = RuntimePropertySpecification.defaultValue.property().getVdDirect(specification);
			parameters.put(specification.getName(), TemplateValues.evaluate(context, descriptor == null
					? specification.getDefaultValue() : context.evaluate(descriptor)));
		}
		for (RuntimePropertyValue value : node.getArguments().getValues()) {
			ValueDescriptor descriptor = RuntimePropertyValue.value.property().getVdDirect(value);
			parameters.put(value.getSpecification().getName(), TemplateValues.evaluate(context, descriptor == null
					? value.getValue() : context.evaluate(descriptor)));
		}
		Runnable evaluation = () -> context.withVariables(parameters, () -> context.evaluate(node.getDeclaration().getBlock()));
		if (context instanceof RuntimeTemplateEvaluationContext runtime) {
			String indent = runtime.currentLineIndent();
			runtime.withLinePrefix(indent, evaluation);
		} else {
			evaluation.run();
		}
	}

	@Override
	public Reason validate(ValidationContext context, InvokeInstruction node) {
		if (node.getName() == null || node.getName().isBlank())
			return InvalidArgument.create("InvokeInstruction.name must not be blank");
		if (node.getArguments() == null)
			return InvalidArgument.mandatoryPropertyNull(InvokeInstruction.T, InvokeInstruction.arguments.name());
		RuntimeArguments arguments = node.getArguments();
		if (node.getDeclaration() == null)
			return InvalidArgument.mandatoryPropertyNull(InvokeInstruction.T, InvokeInstruction.declaration.name());
		if (node.getDeclaration().getArgumentType() == null)
			return InvalidArgument.create("Instruction declaration has no completed argument type: " + node.getName());
		if (arguments.getTypeSpecification() == null)
			return InvalidArgument.mandatoryPropertyNull(RuntimeArguments.T, RuntimeArguments.typeSpecification.name());
		if (arguments.getValues() == null)
			return InvalidArgument.mandatoryPropertyNull(RuntimeArguments.T, RuntimeArguments.values.name());

		if (node.getDeclaration().getArgumentType() != arguments.getTypeSpecification()
				&& !node.getDeclaration().getArgumentType().getName()
						.equals(arguments.getTypeSpecification().getName()))
			return InvalidArgument.create("Invocation arguments do not match declaration " + node.getName());

		Map<String, RuntimePropertySpecification> expected = new LinkedHashMap<>();
		for (RuntimePropertySpecification property : node.getDeclaration().getArgumentType().getProperties())
			expected.put(property.getName(), property);
		Set<String> supplied = new HashSet<>();
		for (RuntimePropertyValue value : arguments.getValues()) {
			if (value == null || value.getSpecification() == null)
				return InvalidArgument.create("Invocation argument value has no property specification");
			String name = value.getSpecification().getName();
			RuntimePropertySpecification property = expected.get(name);
			if (property == null)
				return InvalidArgument.create("Unknown argument '" + name + "' for instruction " + node.getName());
			if (!supplied.add(name))
				return InvalidArgument.create("Duplicate argument '" + name + "' for instruction " + node.getName());
			var expectedType = context.resolveType(property.getTypeSignature());
			if (expectedType == null)
				return InvalidArgument.create("Unknown argument type '" + property.getTypeSignature()
						+ "' for instruction " + node.getName());
		}
		for (RuntimePropertySpecification property : expected.values())
			if (property.getRequired() && !supplied.contains(property.getName()))
				return InvalidArgument.create("Missing argument '" + property.getName()
						+ "' for instruction " + node.getName());
		return null;
	}

}
