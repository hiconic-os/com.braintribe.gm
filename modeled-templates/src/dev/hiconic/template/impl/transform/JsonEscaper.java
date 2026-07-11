package dev.hiconic.template.impl.transform;

import dev.hiconic.template.model.core.output.JsonEscape;
import dev.hiconic.template.model.core.output.JsonOutput;

public class JsonEscaper extends AbstractEscaper<JsonEscape, JsonOutput> {
	public JsonEscaper() {
		super(JsonOutput.T);
	}

	@Override
	protected String escape(String input) {
		return EscapeTools.json(input);
	}
}
