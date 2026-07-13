package dev.hiconic.template.impl.transform;

import com.braintribe.gm.model.reason.Maybe;
import com.braintribe.model.generic.reflection.EntityType;

import dev.hiconic.template.api.TemplateEvaluationContext;
import dev.hiconic.template.api.ValueConversion;
import dev.hiconic.template.model.core.output.SafeOutput;
import com.braintribe.model.generic.value.ValueDescriptor;

abstract class AbstractEscaper<P extends ValueDescriptor, O extends SafeOutput> implements ValueConversion<String, P, O> {
	private final EntityType<O> outputType;

	protected AbstractEscaper(EntityType<O> outputType) {
		this.outputType = outputType;
	}

	@Override
	public Maybe<O> convert(TemplateEvaluationContext context, String input, P params) {
		O output = outputType.create();
		output.setText(escape(input == null ? "" : input));
		return Maybe.complete(output);
	}

	protected abstract String escape(String input);
}
