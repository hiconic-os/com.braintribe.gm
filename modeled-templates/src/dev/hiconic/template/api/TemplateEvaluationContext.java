package dev.hiconic.template.api;

import java.util.Map;

import com.braintribe.model.generic.value.ValueDescriptor;

import dev.hiconic.template.model.core.TemplateNode;
import dev.hiconic.template.model.core.TemplateEvaluationDefaults;

public interface TemplateEvaluationContext {
	static ScopedValue<TemplateEvaluationContext> CURRENT = ScopedValue.newInstance();

	Object evaluate(ValueDescriptor vd);
	void evaluate(TemplateNode node);
	void append(String text);
	void withVariables(Map<String, Object> variables, Runnable evaluation);
	void declareVariable(String name, Object value);
	void setVariable(String name, Object value);
	Object getVariable(String name);
	boolean allowsNoEscape();
	TemplateEvaluationDefaults defaults();
	ResolvedTemplateDefaults resolvedDefaults();
}
