package dev.hiconic.template.impl.transform;

import com.braintribe.gm.model.reason.Maybe;
import com.braintribe.gm.model.reason.essential.InvalidArgument;

import dev.hiconic.template.api.TemplateEvaluationContext;
import dev.hiconic.template.api.ValueConversion;
import dev.hiconic.template.model.core.output.NoEscape;
import dev.hiconic.template.model.core.output.RawOutput;

public class NoEscapeConversion implements ValueConversion<String, NoEscape, RawOutput> {
	@Override
	public Maybe<RawOutput> convert(TemplateEvaluationContext context, String input, NoEscape params) {
		if (!context.allowsNoEscape())
			return Maybe.empty(InvalidArgument.create("NoEscape is disabled by the template evaluation policy"));

		RawOutput output = RawOutput.T.create();
		output.setText(input == null ? "" : input);
		return Maybe.complete(output);
	}
}
