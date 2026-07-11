package dev.hiconic.template.impl.transform;

import dev.hiconic.template.model.core.output.JavaScriptEscape;
import dev.hiconic.template.model.core.output.JavaScriptOutput;

public class JavaScriptEscaper extends AbstractEscaper<JavaScriptEscape, JavaScriptOutput> {
	public JavaScriptEscaper() {
		super(JavaScriptOutput.T);
	}

	@Override
	protected String escape(String input) {
		return EscapeTools.javaScript(input);
	}
}
