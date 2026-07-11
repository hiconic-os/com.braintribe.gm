package dev.hiconic.template.impl.transform;

import dev.hiconic.template.model.core.output.JavaLiteralEscape;
import dev.hiconic.template.model.core.output.JavaLiteralOutput;

public class JavaLiteralEscaper extends AbstractEscaper<JavaLiteralEscape, JavaLiteralOutput> {
	public JavaLiteralEscaper() {
		super(JavaLiteralOutput.T);
	}

	@Override
	protected String escape(String input) {
		return EscapeTools.javaLiteral(input);
	}
}
