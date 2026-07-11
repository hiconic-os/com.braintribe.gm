package dev.hiconic.template.impl.transform;

import dev.hiconic.template.model.core.output.CssEscape;
import dev.hiconic.template.model.core.output.CssOutput;

public class CssEscaper extends AbstractEscaper<CssEscape, CssOutput> {
	public CssEscaper() {
		super(CssOutput.T);
	}

	@Override
	protected String escape(String input) {
		return EscapeTools.css(input);
	}
}
