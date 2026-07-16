package dev.hiconic.template.impl.node;

import com.braintribe.gm.model.reason.Reason;
import com.braintribe.gm.model.reason.essential.InvalidArgument;
import com.braintribe.model.generic.reflection.GenericModelType;
import com.braintribe.model.generic.reflection.Property;
import com.braintribe.model.generic.value.ValueDescriptor;

import dev.hiconic.template.api.Template;
import dev.hiconic.template.api.TemplateEvaluationContext;
import dev.hiconic.template.api.TemplateNodeEvaluator;
import dev.hiconic.template.api.ValidationContext;
import dev.hiconic.template.impl.TemplateValues;
import dev.hiconic.template.impl.parser.DefinitionTools;
import dev.hiconic.template.model.core.instr.RenderTemplate;

public class RenderTemplateEvaluator implements TemplateNodeEvaluator<RenderTemplate> {
	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void evaluate(TemplateEvaluationContext context, RenderTemplate node) {
		Template template = context.resolveTemplate(node.getName());
		if (template == null)
			throw new IllegalArgumentException("Unknown template delegate: " + node.getName());

		Property inputProperty = RenderTemplate.input.property();
		ValueDescriptor descriptor = inputProperty.getVdDirect(node);
		Object input = TemplateValues.evaluate(context,
				descriptor == null ? node.getInput() : context.evaluate(descriptor));
		context.append(template.evaluateToString(input));
	}

	@Override
	public Reason complete(ValidationContext context, RenderTemplate node) {
		if (node.getName() == null || node.getName().isBlank())
			return InvalidArgument.create("RenderTemplate.name must not be blank");
		GenericModelType templateType = context.templateRootType(node.getName());
		if (templateType == null)
			return InvalidArgument.create("Unknown template delegate: " + node.getName());
		GenericModelType inputType = context.getType(node, RenderTemplate.input);
		if (inputType == null || !templateType.isAssignableFrom(inputType))
			return InvalidArgument.create("RenderTemplate.input for '" + node.getName() + "' expects "
					+ templateType.getTypeSignature() + " but got "
					+ (inputType == null ? "<unknown>" : inputType.getTypeSignature()));
		node.setInputType(DefinitionTools.type(inputType.getTypeSignature()));
		return null;
	}

	@Override
	public GenericModelType expectedArgumentType(ValidationContext context, RenderTemplate node, Property property) {
		return property == RenderTemplate.input.property() ? context.templateRootType(node.getName()) : null;
	}
}
