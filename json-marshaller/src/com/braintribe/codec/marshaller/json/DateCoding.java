// ============================================================================
// Copyright BRAINTRIBE TECHNOLOGY GMBH, Austria, 2002-2022
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
// ============================================================================
package com.braintribe.codec.marshaller.json;

import java.io.Writer;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalQueries;
import java.util.Date;
import java.util.Locale;

/* package */ class DateCoding {

	private final DateTimeFormatter inFormatter;
	private DateTimeFormatter outFormatter;
	private String pattern;
	private final ZoneId defaultZone;
	private Locale locale;

	public DateCoding(String pattern, ZoneId defaultZone, Locale locale) {
		this.pattern = pattern;
		this.defaultZone = defaultZone;
		this.locale = locale;
		inFormatter = outFormatter = DateTimeFormatter.ofPattern(pattern) //
				.withZone(defaultZone).withLocale(locale);
	}

	public DateCoding(DateTimeFormatter inFormatter, DateTimeFormatter outFormatter) {
		this.defaultZone = ZoneOffset.UTC;
		this.inFormatter = inFormatter;
		this.outFormatter = outFormatter;
	}

	public Date decode(String s) {
		try {
			TemporalAccessor accessor = inFormatter.parse(s);
			LocalTime time = accessor.query(TemporalQueries.localTime());
			LocalDate date = accessor.query(TemporalQueries.localDate());
			ZoneId zone = accessor.query(TemporalQueries.zone());
			if (time == null) {
				time = LocalTime.of(0, 0);
			}
			if (date == null) {
				date = LocalDate.of(0, 1, 1);
			}

			final Date result;
			if (zone != null) {
				result = Date.from(ZonedDateTime.of(date, time, zone).toInstant());
			} else {
				result = Date.from(LocalDateTime.of(date, time).atZone(ZoneId.systemDefault()).toInstant());
			}

			return result;
		} catch (Exception e) {
			throw new IllegalArgumentException("Date [" + s + "] could not be parsed with the meta-data mapped format: pattern [" + pattern
					+ "], defaultTimeZone [" + defaultZone + "], locale [" + (locale != null ? locale.toLanguageTag() : "null") + "]", e);
		}
	}

	public void encodeTo(Date date, Writer writer) {
		ZonedDateTime dateTime = ZonedDateTime.ofInstant(date.toInstant(), defaultZone);
		outFormatter.formatTo(dateTime, writer);
	}

}