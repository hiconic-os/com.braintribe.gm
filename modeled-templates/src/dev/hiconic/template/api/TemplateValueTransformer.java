package dev.hiconic.template.api;

import com.braintribe.gm.model.reason.Maybe;
import com.braintribe.gm.model.reason.Reason;
import com.braintribe.model.generic.GenericEntity;

public interface TemplateValueTransformer<I, P extends GenericEntity, O> {
    Maybe<O> transform(TemplateEvaluationContext context, P params, I input);
    default Reason validate(ValidationContext context, P params, I input) { return null; }
}
