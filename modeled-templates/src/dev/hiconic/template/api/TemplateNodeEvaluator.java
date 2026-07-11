package dev.hiconic.template.api;

import com.braintribe.gm.model.reason.Reason;

import dev.hiconic.template.model.core.TemplateNode;

public interface TemplateNodeEvaluator<N extends TemplateNode> {
    void evaluate(TemplateEvaluationContext context, N params);
    default Reason validate(ValidationContext context, N params) { return null; }
    default Reason complete(ValidationContext context, N params) { return validate(context, params); }
}
