package dev.hiconic.template.impl.transform;

import com.braintribe.gm.model.reason.Maybe;
import com.braintribe.model.generic.reflection.EntityType;

import dev.hiconic.template.api.TemplateEvaluationContext;
import dev.hiconic.template.api.TemplateValueTransformer;
import dev.hiconic.template.model.core.output.SafeOutput;
import dev.hiconic.template.model.core.output.Transformer;

abstract class AbstractEscaper<P extends Transformer, O extends SafeOutput> implements TemplateValueTransformer<String, P, O> {
	private final EntityType<O> outputType;

	protected AbstractEscaper(EntityType<O> outputType) {
		this.outputType = outputType;
	}

	@Override
	public Maybe<O> transform(TemplateEvaluationContext context, P params, String input) {
		O output = outputType.create();
		output.setText(escape(input == null ? "" : input));
		return Maybe.complete(output);
	}

	protected abstract String escape(String input);
}
