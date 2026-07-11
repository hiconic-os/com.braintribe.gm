package dev.hiconic.template.impl.node;

import java.util.HashSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.braintribe.gm.model.reason.Reason;
import com.braintribe.gm.model.reason.essential.InvalidArgument;

import dev.hiconic.template.api.TemplateEvaluationContext;
import dev.hiconic.template.api.TemplateNodeEvaluator;
import dev.hiconic.template.api.ValidationContext;
import dev.hiconic.template.model.core.decl.DeclareInstruction;
import dev.hiconic.template.model.core.decl.Parameter;
import dev.hiconic.template.model.core.decl.RuntimePropertySpecification;
import dev.hiconic.template.model.core.decl.RuntimeTypeSpecification;

public class DeclareInstructionEvaluator implements TemplateNodeEvaluator<DeclareInstruction> {
	@Override
	public void evaluate(TemplateEvaluationContext context, DeclareInstruction node) {
		// Declarations are normalized into their call sites during parsing.
	}

	@Override
	public Reason complete(ValidationContext context, DeclareInstruction node) {
		Reason validation = validate(context, node);
		if (validation != null)
			return validation;

		RuntimeTypeSpecification argumentType = node.getArgumentType();
		if (argumentType == null) {
			argumentType = RuntimeTypeSpecification.T.create();
			argumentType.setName(node.getName() + "Arguments");
			argumentType.setProperties(new ArrayList<>());
			node.setArgumentType(argumentType);
		}

		if (argumentType.getProperties() == null)
			argumentType.setProperties(new ArrayList<>());
		if (argumentType.getProperties().isEmpty()) {
			for (int i = 0; i < node.getParameters().size(); i++) {
				Parameter parameter = node.getParameters().get(i);
				var type = context.resolveType(parameter.getType());
				if (type == null)
					return InvalidArgument.create("Unknown parameter type '" + parameter.getType()
							+ "' for instruction " + node.getName());
				RuntimePropertySpecification property = RuntimePropertySpecification.T.create();
				property.setName(parameter.getName());
				property.setTypeSignature(type.getTypeSignature());
				property.setPositionalIndex(i);
				property.setRequired(true);
				property.setMetaData(new ArrayList<>());
				argumentType.getProperties().add(property);
			}
		}
		return validateSpecification(context, node, argumentType);
	}

	@Override
	public Reason validate(ValidationContext context, DeclareInstruction node) {
		if (node.getName() == null || node.getName().isBlank())
			return InvalidArgument.create("DeclareInstruction.name must not be blank");
		if (node.getBlock() == null)
			return InvalidArgument.mandatoryPropertyNull(DeclareInstruction.T, DeclareInstruction.block.name());
		List<Parameter> parameters = node.getParameters();
		if (parameters == null)
			return InvalidArgument.mandatoryPropertyNull(DeclareInstruction.T, DeclareInstruction.parameters.name());
		Set<String> names = new HashSet<>();
		for (Parameter parameter : parameters) {
			if (parameter == null || parameter.getName() == null || parameter.getName().isBlank())
				return InvalidArgument.create("Instruction parameter name must not be blank");
			if (parameter.getType() == null || parameter.getType().isBlank())
				return InvalidArgument.create("Instruction parameter type must not be blank");
			if (!names.add(parameter.getName()))
				return InvalidArgument.create("Duplicate instruction parameter: " + parameter.getName());
		}
		return null;
	}

	private Reason validateSpecification(ValidationContext context, DeclareInstruction node,
			RuntimeTypeSpecification specification) {
		if (specification.getProperties().size() != node.getParameters().size())
			return InvalidArgument.create("Argument specification of instruction " + node.getName()
					+ " does not match its parameter count");
		Set<String> names = new HashSet<>();
		for (int i = 0; i < specification.getProperties().size(); i++) {
			RuntimePropertySpecification property = specification.getProperties().get(i);
			if (property.getName() == null || !names.add(property.getName()))
				return InvalidArgument.create("Duplicate or blank runtime argument property");
			var resolvedType = context.resolveType(property.getTypeSignature());
			if (resolvedType == null)
				return InvalidArgument.create("Unknown runtime argument type: " + property.getTypeSignature());
			Parameter parameter = node.getParameters().get(i);
			var parameterType = context.resolveType(parameter.getType());
			if (!parameter.getName().equals(property.getName()) || parameterType == null
					|| !parameterType.getTypeSignature().equals(resolvedType.getTypeSignature()))
				return InvalidArgument.create("Runtime argument property '" + property.getName()
						+ "' does not match declaration parameter at position " + i);
		}
		return null;
	}
}
