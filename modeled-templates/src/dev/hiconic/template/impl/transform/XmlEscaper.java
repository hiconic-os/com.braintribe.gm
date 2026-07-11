package dev.hiconic.template.impl.transform;

import dev.hiconic.template.model.core.output.XmlEscape;
import dev.hiconic.template.model.core.output.XmlOutput;

public class XmlEscaper extends AbstractEscaper<XmlEscape, XmlOutput> {
	public XmlEscaper() {
		super(XmlOutput.T);
	}

	@Override
	protected String escape(String input) {
		return EscapeTools.xml(input);
	}
}
