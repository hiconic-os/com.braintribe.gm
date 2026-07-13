package dev.hiconic.template.api;

import com.braintribe.gm.model.reason.Maybe;
import com.braintribe.gm.model.reason.Reason;
import com.braintribe.model.generic.value.ValueDescriptor;

/** Evaluation expert for a value descriptor that may also form a conversion edge. */
public interface ValueConversion<I, V extends ValueDescriptor, O> {
	Maybe<O> convert(TemplateEvaluationContext context, I input, V descriptor);
	default Reason validate(ValidationContext context, V descriptor) { return null; }
}
