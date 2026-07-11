package dev.hiconic.template.api;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.time.ZoneId;
import java.util.Locale;
import java.util.Objects;

import dev.hiconic.template.model.core.TemplateEvaluationDefaults;

public final class TemplateDefaults {
	public static final String DEFAULT_DATE_PATTERN = "yyyy-MM-dd";

	private TemplateDefaults() {
	}

	public static TemplateEvaluationDefaults standard() {
		return builder().build();
	}

	public static Builder builder() {
		return new Builder();
	}

	public static Builder derive(TemplateEvaluationDefaults defaults) {
		Objects.requireNonNull(defaults, "defaults");
		return builder()
				.locale(locale(defaults))
				.zone(zone(defaults))
				.datePattern(datePattern(defaults))
				.numberPattern(defaults.getNumberPattern());
	}

	public static Locale locale(TemplateEvaluationDefaults defaults) {
		return locale(defaults.getLocale(), Locale.getDefault());
	}

	public static Locale locale(String locale, Locale defaultLocale) {
		return locale == null || locale.isBlank()
				? defaultLocale
				: Locale.forLanguageTag(locale.replace('_', '-'));
	}

	public static ZoneId zone(TemplateEvaluationDefaults defaults) {
		return zone(defaults.getZoneId(), ZoneId.systemDefault());
	}

	public static ZoneId zone(String zoneId, ZoneId defaultZone) {
		return zoneId == null || zoneId.isBlank()
				? defaultZone
				: ZoneId.of(zoneId);
	}

	public static String datePattern(TemplateEvaluationDefaults defaults) {
		String pattern = defaults.getDatePattern();
		return pattern == null || pattern.isBlank()
				? DEFAULT_DATE_PATTERN
				: pattern;
	}

	public static NumberFormat newNumberFormat(TemplateEvaluationDefaults defaults) {
		Locale locale = locale(defaults);
		String pattern = defaults.getNumberPattern();
		if (pattern == null || pattern.isBlank())
			return NumberFormat.getNumberInstance(locale);
		return new DecimalFormat(pattern, DecimalFormatSymbols.getInstance(locale));
	}

	public static final class Builder {
		private Locale locale = Locale.getDefault();
		private ZoneId zone = ZoneId.systemDefault();
		private String datePattern = DEFAULT_DATE_PATTERN;
		private String numberPattern;

		public Builder locale(Locale locale) {
			this.locale = Objects.requireNonNull(locale, "locale");
			return this;
		}

		public Builder zone(ZoneId zone) {
			this.zone = Objects.requireNonNull(zone, "zone");
			return this;
		}

		public Builder datePattern(String datePattern) {
			this.datePattern = Objects.requireNonNull(datePattern, "datePattern");
			return this;
		}

		public Builder numberPattern(String numberPattern) {
			this.numberPattern = numberPattern;
			return this;
		}

		public TemplateEvaluationDefaults build() {
			TemplateEvaluationDefaults defaults = TemplateEvaluationDefaults.T.create();
			defaults.setLocale(locale.toLanguageTag());
			defaults.setZoneId(zone.getId());
			defaults.setDatePattern(datePattern);
			defaults.setNumberPattern(numberPattern);
			return defaults;
		}
	}
}
