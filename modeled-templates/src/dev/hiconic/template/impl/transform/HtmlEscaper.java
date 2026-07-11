package dev.hiconic.template.impl.transform;

import dev.hiconic.template.model.core.output.HtmlEsc;
import dev.hiconic.template.model.core.output.HtmlOutput;

public class HtmlEscaper extends AbstractEscaper<HtmlEsc, HtmlOutput> {
	public HtmlEscaper() {
		super(HtmlOutput.T);
	}

	@Override
	protected String escape(String input) {
		return EscapeTools.html(input);
	}
}
