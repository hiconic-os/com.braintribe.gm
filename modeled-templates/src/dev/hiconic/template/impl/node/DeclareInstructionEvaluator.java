package dev.hiconic.template.impl.node;

import java.util.HashSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.braintribe.gm.model.reason.Reason;
import com.braintribe.gm.model.reason.essential.InvalidArgument;
import com.braintribe.model.generic.reflection.GenericModelType;
import com.braintribe.model.generic.value.ValueDescriptor;

import dev.hiconic.template.api.TemplateEvaluationContext;
import dev.hiconic.template.api.TemplateNodeEvaluator;
import dev.hiconic.template.api.ValidationContext;
import dev.hiconic.template.model.core.decl.DeclareInstruction;
import dev.hiconic.template.model.core.decl.VariableDefinition;
import static dev.hiconic.template.impl.parser.DefinitionTools.*;
import dev.hiconic.template.model.core.decl.RuntimePropertySpecification;
import dev.hiconic.template.model.core.decl.RuntimeTypeSpecification;

public class DeclareInstructionEvaluator implements TemplateNodeEvaluator<DeclareInstruction> {
	@Override
	public void evaluate(TemplateEvaluationContext context, DeclareInstruction node) {
		// Declarations are normalized into their call sites during parsing.
	}

	@Override
	public Reason complete(ValidationContext context, DeclareInstruction node) {
		Reason completion = completeSignature(context, node);
		return completion == null ? validate(context, node) : completion;
	}

	public Reason completeSignature(ValidationContext context, DeclareInstruction node) {
		if (node.getParameters() != null) {
			for (VariableDefinition parameter : node.getParameters()) {
				Reason completion = completeParameter(context, parameter);
				if (completion != null) return completion;
			}
		}
		Reason validation = validateParameters(context, node);
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
				VariableDefinition parameter = node.getParameters().get(i);
				final GenericModelType type;
				try {
					type = context.resolveType(type(parameter));
				} catch (RuntimeException e) {
					return InvalidArgument.create("Invalid parameter type '" + type(parameter)
							+ "' for instruction " + node.getName() + ": " + e.getMessage());
				}
				if (type == null)
					return InvalidArgument.create("Unknown parameter type '" + type(parameter)
							+ "' for instruction " + node.getName());
				RuntimePropertySpecification property = RuntimePropertySpecification.T.create();
				property.setName(name(parameter));
				property.setTypeSignature(type.getTypeSignature());
				property.setPositionalIndex(i);
				property.setRequired(parameter.getRequired());
				ValueDescriptor defaultDescriptor = VariableDefinition.defaultValue.property().getVdDirect(parameter);
				if (defaultDescriptor == null)
					property.setDefaultValue(parameter.getDefault());
				else
					RuntimePropertySpecification.defaultValue.property().setVdDirect(property, defaultDescriptor);
				property.setMetaData(new ArrayList<>());
				argumentType.getProperties().add(property);
			}
		}
		return validateSpecification(context, node, argumentType);
	}

	@Override
	public Reason validate(ValidationContext context, DeclareInstruction node) {
		if (node.getName() == null || node.getName().getName().isBlank())
			return InvalidArgument.create("DeclareInstruction.name must not be blank");
		if (node.getBlock() == null)
			return InvalidArgument.mandatoryPropertyNull(DeclareInstruction.T, DeclareInstruction.block.name());
		return validateParameters(context, node);
	}

	private Reason completeParameter(ValidationContext context, VariableDefinition parameter) {
		if (parameter == null) return null;
		boolean hasDefault = hasDefault(context, parameter);
		boolean requiredExplicit = context.getRange(parameter, VariableDefinition.required) != null;
		if (hasDefault) {
			if (requiredExplicit && parameter.getRequired())
				return InvalidArgument.create("A parameter with default cannot be explicitly required");
			parameter.setRequired(false);
		}
		if (parameter.getType() == null) {
			if (!hasDefault)
				return InvalidArgument.create("Parameter type can only be inferred from an explicit default");
			GenericModelType inferred = context.getType(parameter, VariableDefinition.defaultValue);
			if (inferred == null)
				return InvalidArgument.create("Cannot infer parameter type from default");
			parameter.setType(dev.hiconic.template.impl.parser.DefinitionTools.type(inferred.getTypeSignature()));
		}
		if (hasDefault && !context.isExplicitNull(parameter, VariableDefinition.defaultValue)) {
			GenericModelType declared = context.resolveType(type(parameter));
			GenericModelType actual = context.getType(parameter, VariableDefinition.defaultValue);
			if (declared != null && (actual == null || !declared.isAssignableFrom(actual)))
				return InvalidArgument.create("Parameter default is not assignable to " + type(parameter));
		}
		return null;
	}

	private static boolean hasDefault(ValidationContext context, VariableDefinition parameter) {
		return VariableDefinition.defaultValue.property().getVdDirect(parameter) != null
				|| parameter.getDefault() != null
				|| context.isExplicitNull(parameter, VariableDefinition.defaultValue)
				|| context.getRange(parameter, VariableDefinition.defaultValue) != null;
	}

	private Reason validateParameters(ValidationContext context, DeclareInstruction node) {
		List<VariableDefinition> parameters = node.getParameters();
		if (parameters == null)
			return InvalidArgument.mandatoryPropertyNull(DeclareInstruction.T, DeclareInstruction.parameters.name());
		Set<String> names = new HashSet<>();
		for (VariableDefinition parameter : parameters) {
			if (parameter == null || parameter.getSymbol() == null || name(parameter).isBlank())
				return InvalidArgument.create("Instruction parameter name must not be blank");
			if (parameter.getType() == null || type(parameter).isBlank())
				return InvalidArgument.create("Instruction parameter type must not be blank");
			if (parameter.getRequired() && hasDefault(context, parameter))
				return InvalidArgument.create("Required instruction parameter cannot have a default: " + name(parameter));
			if (!names.add(name(parameter)))
				return InvalidArgument.create("Duplicate instruction parameter: " + name(parameter));
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
			VariableDefinition parameter = node.getParameters().get(i);
			var parameterType = context.resolveType(type(parameter));
			if (!name(parameter).equals(property.getName()) || parameterType == null
					|| !parameterType.getTypeSignature().equals(resolvedType.getTypeSignature())
					|| parameter.getRequired() != property.getRequired())
				return InvalidArgument.create("Runtime argument property '" + property.getName()
						+ "' does not match declaration parameter at position " + i);
		}
		return null;
	}
}
