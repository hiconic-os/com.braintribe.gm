package dev.hiconic.template.impl.vd;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import com.braintribe.gm.model.reason.Maybe;
import com.braintribe.gm.model.reason.Reason;
import com.braintribe.gm.model.reason.essential.InvalidArgument;

import dev.hiconic.template.api.TemplateEvaluationContext;
import dev.hiconic.template.api.ValidationContext;
import dev.hiconic.template.api.VdEvaluator;
import dev.hiconic.template.model.core.vd.Like;

public class LikeEvaluator implements VdEvaluator<Like, Boolean> {
	@Override
	public Maybe<Boolean> transform(TemplateEvaluationContext context, Like like) {
		try {
			return Maybe.complete(Pattern.matches(like.getPattern(), like.getCandidate()));
		} catch (PatternSyntaxException e) {
			return Maybe.empty(InvalidArgument.create("Invalid regular expression: " + e.getDescription()));
		}
	}

	@Override
	public Reason validate(ValidationContext context, Like like) {
		return VdValidation.requireString(context, like, Like.candidate, Like.pattern);
	}
}
