package dev.hiconic.template.impl.transform;

import com.braintribe.gm.model.reason.Maybe;
import com.braintribe.gm.model.reason.essential.InvalidArgument;

import dev.hiconic.template.api.TemplateEvaluationContext;
import dev.hiconic.template.api.TemplateValueTransformer;
import dev.hiconic.template.model.core.output.NoEscape;
import dev.hiconic.template.model.core.output.RawOutput;

public class NoEscapeTransformer implements TemplateValueTransformer<String, NoEscape, RawOutput> {
	@Override
	public Maybe<RawOutput> transform(TemplateEvaluationContext context, NoEscape params, String input) {
		if (!context.allowsNoEscape())
			return Maybe.empty(InvalidArgument.create("NoEscape is disabled by the template evaluation policy"));

		RawOutput output = RawOutput.T.create();
		output.setText(input == null ? "" : input);
		return Maybe.complete(output);
	}
}
