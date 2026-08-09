package dev.hiconic.template.api;

import java.util.Map;

import com.braintribe.model.generic.value.ValueDescriptor;

import dev.hiconic.template.model.core.TemplateNode;
import dev.hiconic.template.model.core.Symbol;
import dev.hiconic.template.model.core.TemplateEvaluationDefaults;
import dev.hiconic.template.model.core.output.Output;
import dev.hiconic.template.model.core.output.SafeOutput;

public interface TemplateEvaluationContext {
	static ScopedValue<TemplateEvaluationContext> CURRENT = ScopedValue.newInstance();

	Object evaluate(ValueDescriptor vd);
	void evaluate(TemplateNode node);
	void append(String text);

	/**
	 * Emits an {@link Output} produced by an {@code OutputNode} into the active sink. The default is
	 * the text sink: the text-capable {@link SafeOutput} branch is appended as text and any other
	 * {@code Output} kind is ignored (validation forbids it for a text sink). A document sink
	 * overrides this to place runs / shapes / embedded documents at its insertion cursor.
	 */
	default void emit(Output output) {
		if (output instanceof SafeOutput safe)
			append(safe.getText());
	}
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
