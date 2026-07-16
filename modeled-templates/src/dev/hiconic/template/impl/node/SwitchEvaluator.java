package dev.hiconic.template.impl.node;

import java.util.List;
import java.util.Objects;
import java.util.Map;

import com.braintribe.gm.model.reason.Reason;
import com.braintribe.gm.model.reason.essential.InvalidArgument;
import com.braintribe.model.generic.reflection.Property;
import com.braintribe.model.generic.value.ValueDescriptor;

import dev.hiconic.template.api.TemplateEvaluationContext;
import dev.hiconic.template.api.TemplateNodeEvaluator;
import dev.hiconic.template.api.ValidationContext;
import dev.hiconic.template.impl.TemplateValues;
import dev.hiconic.template.model.core.instr.Case;
import dev.hiconic.template.model.core.instr.Default;
import dev.hiconic.template.model.core.instr.Switch;
import dev.hiconic.template.model.core.instr.SwitchCase;
import dev.hiconic.template.model.core.instr.When;

public class SwitchEvaluator implements TemplateNodeEvaluator<Switch> {
	@Override
	public void evaluate(TemplateEvaluationContext context, Switch node) {
		Object switchValue = value(context, node, Switch.value.property());
		SwitchCase defaultCase = null;
		List<SwitchCase> cases = node.getCases();
		if (cases != null)
			for (SwitchCase candidate : cases) {
				if (candidate instanceof Default) {
					if (defaultCase == null) defaultCase = candidate;
					continue;
				}
				if (matches(context, switchValue, candidate)) {
					context.withVariables(Map.of(), () -> context.evaluate(candidate.getBlock()));
					return;
				}
			}
		if (defaultCase != null) {
			SwitchCase selectedDefault = defaultCase;
			context.withVariables(Map.of(), () -> context.evaluate(selectedDefault.getBlock()));
		}
	}

	@Override
	public Reason validate(ValidationContext context, Switch node) {
		if (node.getCases() == null)
			return InvalidArgument.mandatoryPropertyNull(Switch.T, Switch.cases.name());
		boolean defaultSeen = false;
		for (int i = 0; i < node.getCases().size(); i++) {
			SwitchCase candidate = node.getCases().get(i);
			if (candidate == null || candidate.getBlock() == null)
				return InvalidArgument.create("Switch.cases[" + i + "] must have a block");
			if (candidate instanceof Default && defaultSeen)
				return InvalidArgument.create("Switch must not have more than one default clause");
			if (candidate instanceof Default) defaultSeen = true;
		}
		return null;
	}

	private boolean matches(TemplateEvaluationContext context, Object switchValue, SwitchCase candidate) {
		if (candidate instanceof Case caseClause)
			return Objects.equals(switchValue, value(context, caseClause, Case.value.property()));
		if (candidate instanceof When whenClause) {
			Object condition = value(context, whenClause, When.condition.property());
			if (!(condition instanceof Boolean matched))
				throw new IllegalArgumentException("Switch when condition must evaluate to boolean but was "
						+ (condition == null ? "null" : condition.getClass().getName()));
			return matched;
		}
		return false;
	}

	private Object value(TemplateEvaluationContext context, Object entity, Property property) {
		ValueDescriptor descriptor = property.getVdDirect((com.braintribe.model.generic.GenericEntity) entity);
		Object value = descriptor == null
				? property.getDirect((com.braintribe.model.generic.GenericEntity) entity)
				: context.evaluate(descriptor);
		return TemplateValues.evaluate(context, value);
	}
}
