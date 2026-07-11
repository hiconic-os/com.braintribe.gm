package dev.hiconic.template.api;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.time.ZoneId;
import java.util.Locale;
import java.util.Objects;

import dev.hiconic.template.model.core.TemplateEvaluationDefaults;

public final class ResolvedTemplateDefaults {
	private final TemplateEvaluationDefaults model;
	private final Locale locale;
	private final ZoneId zone;
	private final String datePattern;
	private final String numberPattern;

	private ResolvedTemplateDefaults(TemplateEvaluationDefaults model, Locale locale, ZoneId zone,
			String datePattern, String numberPattern) {
		this.model = Objects.requireNonNull(model, "model");
		this.locale = Objects.requireNonNull(locale, "locale");
		this.zone = Objects.requireNonNull(zone, "zone");
		this.datePattern = Objects.requireNonNull(datePattern, "datePattern");
		this.numberPattern = numberPattern;
	}

	public static ResolvedTemplateDefaults of(TemplateEvaluationDefaults model) {
		return new ResolvedTemplateDefaults(model,
				TemplateDefaults.locale(model),
				TemplateDefaults.zone(model),
				TemplateDefaults.datePattern(model),
				model.getNumberPattern());
	}

	public TemplateEvaluationDefaults model() {
		return model;
	}

	public Locale locale() {
		return locale;
	}

	public ZoneId zone() {
		return zone;
	}

	public String datePattern() {
		return datePattern;
	}

	public String numberPattern() {
		return numberPattern;
	}

	public NumberFormat newNumberFormat() {
		if (numberPattern == null || numberPattern.isBlank())
			return NumberFormat.getNumberInstance(locale);
		return new DecimalFormat(numberPattern, DecimalFormatSymbols.getInstance(locale));
	}
}
