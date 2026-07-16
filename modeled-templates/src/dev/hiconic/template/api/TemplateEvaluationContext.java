package dev.hiconic.template.api;

import java.util.Map;

import com.braintribe.model.generic.value.ValueDescriptor;

import dev.hiconic.template.model.core.TemplateNode;
import dev.hiconic.template.model.core.Symbol;
import dev.hiconic.template.model.core.TemplateEvaluationDefaults;

public interface TemplateEvaluationContext {
	static ScopedValue<TemplateEvaluationContext> CURRENT = ScopedValue.newInstance();

	Object evaluate(ValueDescriptor vd);
	void evaluate(TemplateNode node);
	void append(String text);
	void withVariables(Map<String, Object> variables, Runnable evaluation);
	void withSymbolVariables(Map<Symbol, Object> variables, Runnable evaluation);
	void declareVariable(String name, Object value);
	void declareVariable(Symbol symbol, Object value);
	void declareReadonlyVariable(String name, Object value);
	void setVariable(String name, Object value);
	void setVariable(Symbol symbol, Object value);
	void setProperty(dev.hiconic.template.model.core.vd.TemplatePropertyPath path, Object value);
	Object getVariable(String name);
	Object getVariable(Symbol symbol);
	boolean allowsNoEscape();
	TemplateEvaluationDefaults defaults();
	ResolvedTemplateDefaults resolvedDefaults();

	default Template<?> resolveTemplate(String name) {
		return null;
	}
}
