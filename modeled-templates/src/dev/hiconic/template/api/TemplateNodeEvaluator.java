package dev.hiconic.template.api;

import com.braintribe.gm.model.reason.Reason;
import com.braintribe.model.generic.reflection.GenericModelType;
import com.braintribe.model.generic.reflection.Property;

import dev.hiconic.template.model.core.TemplateNode;

public interface TemplateNodeEvaluator<N extends TemplateNode> {
    void evaluate(TemplateEvaluationContext context, N params);
	default GenericModelType expectedArgumentType(ValidationContext context, N params, Property property) { return null; }
    default Reason validate(ValidationContext context, N params) { return null; }
	default Reason completeScope(ValidationContext context, N params) { return null; }
    default Reason complete(ValidationContext context, N params) { return validate(context, params); }
}
