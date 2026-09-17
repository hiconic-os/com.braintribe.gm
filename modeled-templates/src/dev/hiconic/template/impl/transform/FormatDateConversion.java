package dev.hiconic.template.impl.transform;

import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;
import java.util.Date;

import com.braintribe.gm.model.reason.Maybe;

import dev.hiconic.template.api.TemplateEvaluationContext;
import dev.hiconic.template.api.ValueConversion;
import dev.hiconic.template.api.TemplateDefaults;
import dev.hiconic.template.model.core.output.FormatDate;

public class FormatDateConversion implements ValueConversion<Date, FormatDate, String> {
	@Override
	public Maybe<String> convert(TemplateEvaluationContext context, Date input, FormatDate params) {
		if (input == null)
			return Maybe.complete("");

		Locale locale = TemplateDefaults.locale(params.getLocale(), context.resolvedDefaults().locale());
		String zone = params.getZone();
		if (zone == null || zone.isBlank())
			zone = params.getZoneId();
		String pattern = params.getPattern();
		DateTimeFormatter formatter;
		if (pattern != null && !pattern.isBlank())
			formatter = DateTimeFormatter.ofPattern(pattern);
		else if ("DATE".equals(params.getTemporal()))
			formatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM);
		else if ("TIME".equals(params.getTemporal()))
			formatter = DateTimeFormatter.ofLocalizedTime(FormatStyle.MEDIUM);
		else if ("TIMESTAMP".equals(params.getTemporal()))
			formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM);
		else
			formatter = DateTimeFormatter.ofPattern(context.resolvedDefaults().datePattern());
		formatter = formatter
				.withLocale(locale)
				.withZone(TemplateDefaults.zone(zone, context.resolvedDefaults().zone()));
		return Maybe.complete(formatter.format(input.toInstant()));
	}
}
