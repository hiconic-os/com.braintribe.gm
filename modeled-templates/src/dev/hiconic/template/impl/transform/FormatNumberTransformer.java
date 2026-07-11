package dev.hiconic.template.impl.transform;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;

import com.braintribe.gm.model.reason.Maybe;

import dev.hiconic.template.api.TemplateDefaults;
import dev.hiconic.template.api.TemplateEvaluationContext;
import dev.hiconic.template.api.TemplateValueTransformer;
import dev.hiconic.template.model.core.output.FormatNumber;

public class FormatNumberTransformer implements TemplateValueTransformer<Number, FormatNumber, String> {
	@Override
	public Maybe<String> transform(TemplateEvaluationContext context, FormatNumber params, Number input) {
		if (input == null)
			return Maybe.complete("");

		Locale locale = TemplateDefaults.locale(params.getLocale(), context.resolvedDefaults().locale());
		String pattern = params.getPattern();
		if (pattern == null || pattern.isBlank())
			pattern = context.resolvedDefaults().numberPattern();

		NumberFormat format = pattern == null || pattern.isBlank()
				? NumberFormat.getNumberInstance(locale)
				: new DecimalFormat(pattern, DecimalFormatSymbols.getInstance(locale));

		return Maybe.complete(format.format(input));
	}
}
