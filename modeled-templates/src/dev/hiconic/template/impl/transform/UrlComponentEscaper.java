package dev.hiconic.template.impl.transform;

import dev.hiconic.template.model.core.output.UrlComponentEscape;
import dev.hiconic.template.model.core.output.UrlComponentOutput;

public class UrlComponentEscaper extends AbstractEscaper<UrlComponentEscape, UrlComponentOutput> {
	public UrlComponentEscaper() {
		super(UrlComponentOutput.T);
	}

	@Override
	protected String escape(String input) {
		return EscapeTools.urlComponent(input);
	}
}
