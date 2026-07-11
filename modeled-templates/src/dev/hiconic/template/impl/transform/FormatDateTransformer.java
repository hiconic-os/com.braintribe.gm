package dev.hiconic.template.impl.transform;

import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Date;

import com.braintribe.gm.model.reason.Maybe;

import dev.hiconic.template.api.TemplateEvaluationContext;
import dev.hiconic.template.api.TemplateValueTransformer;
import dev.hiconic.template.api.TemplateDefaults;
import dev.hiconic.template.model.core.output.FormatDate;

public class FormatDateTransformer implements TemplateValueTransformer<Date, FormatDate, String> {
	@Override
	public Maybe<String> transform(TemplateEvaluationContext context, FormatDate params, Date input) {
		if (input == null)
			return Maybe.complete("");

		String pattern = params.getPattern();
		if (pattern == null || pattern.isBlank())
			pattern = context.resolvedDefaults().datePattern();

		Locale locale = TemplateDefaults.locale(params.getLocale(), context.resolvedDefaults().locale());
		String zone = params.getZone();
		if (zone == null || zone.isBlank())
			zone = params.getZoneId();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern)
				.withLocale(locale)
				.withZone(TemplateDefaults.zone(zone, context.resolvedDefaults().zone()));
		return Maybe.complete(formatter.format(input.toInstant()));
	}
}
