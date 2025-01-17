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
package com.braintribe.model.query.parser.impl;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.junit.Test;

import com.braintribe.model.processing.query.parser.QueryParser;
import com.braintribe.model.processing.query.parser.api.GmqlParsingError;
import com.braintribe.model.processing.query.parser.api.ParsedQuery;
import com.braintribe.model.processing.query.test.model.Person;
import com.braintribe.model.query.Query;
import com.braintribe.model.query.SelectQuery;
import com.braintribe.utils.genericmodel.GMCoreTools;

public class DateTest extends AbstractQueryParserTest {

	@Test
	public void testYearDefaultZone() throws Exception {

		Date expectedDate = getExpectedDate(2011, 0, 1, 0, 0, 0, 0, TimeZone.getTimeZone("GMT"));

		// @formatter:off
		SelectQuery expectedQuery = sq()
				.from(Person.class, "p")
				.where()
					.value(Integer.valueOf(1)).ne().value(expectedDate)
				.done();
		// @formatter:on

		String queryString = "select * from " + Person.class.getName() + " p where 1 != date(2011Y)";

		ParsedQuery parsedQuery = QueryParser.parse(queryString);
		validatedParsedQuery(parsedQuery);
		Query actualQuery = parsedQuery.getQuery();
		GMCoreTools.checkDescription(actualQuery, expectedQuery);
	}

	@Test
	public void testYearCETZone() throws Exception {

		Date expectedDate = getExpectedDate(2011, 0, 1, 0, 0, 0, 0, TimeZone.getTimeZone("CET"));

		// @formatter:off
		SelectQuery expectedQuery = sq()
				.from(Person.class, "p")
				.where()
					.value(Integer.valueOf(1)).ne().value(expectedDate)
				.done();
		// @formatter:on

		String queryString = "select * from " + Person.class.getName() + " p where 1 != date(2011Y,+0100Z)";

		ParsedQuery parsedQuery = QueryParser.parse(queryString);
		validatedParsedQuery(parsedQuery);
		Query actualQuery = parsedQuery.getQuery();
		GMCoreTools.checkDescription(actualQuery, expectedQuery);
	}

	@Test
	public void testYearMonthDefaultZone() throws Exception {

		Date expectedDate = getExpectedDate(2011, 4, 1, 0, 0, 0, 0, TimeZone.getTimeZone("GMT"));

		// @formatter:off
		SelectQuery expectedQuery = sq()
				.from(Person.class, "p")
				.where()
					.value(Integer.valueOf(1)).ne().value(expectedDate)
				.done();
		// @formatter:on

		String queryString = "select * from " + Person.class.getName() + " p where 1 != date(2011Y,5M)";

		ParsedQuery parsedQuery = QueryParser.parse(queryString);
		validatedParsedQuery(parsedQuery);
		Query actualQuery = parsedQuery.getQuery();
		GMCoreTools.checkDescription(actualQuery, expectedQuery);
	}

	@Test
	public void testYearMonthCETZone() throws Exception {

		Date expectedDate = getExpectedDate(2011, 4, 1, 0, 0, 0, 0, TimeZone.getTimeZone("CET"));

		// @formatter:off
		SelectQuery expectedQuery = sq()
				.from(Person.class, "p")
				.where()
					.value(Integer.valueOf(1)).ne().value(expectedDate)
				.done();
		// @formatter:on

		String queryString = "select * from " + Person.class.getName() + " p where 1 != date(2011Y,5M,+0200Z)";

		ParsedQuery parsedQuery = QueryParser.parse(queryString);
		validatedParsedQuery(parsedQuery);
		Query actualQuery = parsedQuery.getQuery();
		GMCoreTools.checkDescription(actualQuery, expectedQuery);
	}

	@Test
	public void testYearMonthDayDefaultZone() throws Exception {

		Date expectedDate = getExpectedDate(2011, 4, 20, 0, 0, 0, 0, TimeZone.getTimeZone("GMT"));

		// @formatter:off
		SelectQuery expectedQuery = sq()
				.from(Person.class, "p")
				.where()
					.value(Integer.valueOf(1)).ne().value(expectedDate)
				.done();
		// @formatter:on

		String queryString = "select * from " + Person.class.getName() + " p where 1 != date(2011Y,5M,20D)";

		ParsedQuery parsedQuery = QueryParser.parse(queryString);
		validatedParsedQuery(parsedQuery);
		Query actualQuery = parsedQuery.getQuery();
		GMCoreTools.checkDescription(actualQuery, expectedQuery);
	}

	@Test
	public void testYearMonthDayCETZone() throws Exception {

		Date expectedDate = getExpectedDate(2011, 4, 20, 0, 0, 0, 0, TimeZone.getTimeZone("CET"));

		// @formatter:off
		SelectQuery expectedQuery = sq()
				.from(Person.class, "p")
				.where()
					.value(Integer.valueOf(1)).ne().value(expectedDate)
				.done();
		// @formatter:on

		String queryString = "select * from " + Person.class.getName() + " p where 1 != date(2011Y,5M,20D,+0200Z)";

		ParsedQuery parsedQuery = QueryParser.parse(queryString);
		validatedParsedQuery(parsedQuery);
		Query actualQuery = parsedQuery.getQuery();
		GMCoreTools.checkDescription(actualQuery, expectedQuery);
	}

	@Test
	public void testYearMonthDayHourDefaultZone() throws Exception {

		Date expectedDate = getExpectedDate(2011, 4, 20, 13, 0, 0, 0, TimeZone.getTimeZone("GMT"));

		// @formatter:off
		SelectQuery expectedQuery = sq()
				.from(Person.class, "p")
				.where()
					.value(Integer.valueOf(1)).ne().value(expectedDate)
				.done();
		// @formatter:on

		String queryString = "select * from " + Person.class.getName() + " p where 1 != date(2011Y,5M,20D,13H)";

		ParsedQuery parsedQuery = QueryParser.parse(queryString);
		validatedParsedQuery(parsedQuery);
		Query actualQuery = parsedQuery.getQuery();
		GMCoreTools.checkDescription(actualQuery, expectedQuery);
	}

	@Test
	public void testYearMonthDayHourCETZone() throws Exception {

		Date expectedDate = getExpectedDate(2011, 4, 20, 13, 0, 0, 0, TimeZone.getTimeZone("CET"));

		// @formatter:off
		SelectQuery expectedQuery = sq()
				.from(Person.class, "p")
				.where()
					.value(Integer.valueOf(1)).ne().value(expectedDate)
				.done();
		// @formatter:on

		String queryString = "select * from " + Person.class.getName() + " p where 1 != date(2011Y,5M,20D,13H,+0200Z)";

		ParsedQuery parsedQuery = QueryParser.parse(queryString);
		validatedParsedQuery(parsedQuery);
		Query actualQuery = parsedQuery.getQuery();
		GMCoreTools.checkDescription(actualQuery, expectedQuery);
	}

	@Test
	public void testYearMonthDayHourMinuteDefaultZone() throws Exception {

		Date expectedDate = getExpectedDate(2011, 4, 20, 13, 2, 0, 0, TimeZone.getTimeZone("GMT"));

		// @formatter:off
		SelectQuery expectedQuery = sq()
				.from(Person.class, "p")
				.where()
					.value(Integer.valueOf(1)).ne().value(expectedDate)
				.done();
		// @formatter:on

		String queryString = "select * from " + Person.class.getName() + " p where 1 != date(2011Y,5M,20D,13H,2m)";

		ParsedQuery parsedQuery = QueryParser.parse(queryString);
		validatedParsedQuery(parsedQuery);
		Query actualQuery = parsedQuery.getQuery();
		GMCoreTools.checkDescription(actualQuery, expectedQuery);
	}

	@Test
	public void testYearMonthDayHourMinuteCETZone() throws Exception {

		Date expectedDate = getExpectedDate(2011, 4, 20, 13, 2, 0, 0, TimeZone.getTimeZone("CET"));

		// @formatter:off
		SelectQuery expectedQuery = sq()
				.from(Person.class, "p")
				.where()
					.value(Integer.valueOf(1)).ne().value(expectedDate)
				.done();
		// @formatter:on

		String queryString = "select * from " + Person.class.getName() + " p where 1 != date(2011Y,5M,20D,13H,2m,+0200Z)";

		ParsedQuery parsedQuery = QueryParser.parse(queryString);
		validatedParsedQuery(parsedQuery);
		Query actualQuery = parsedQuery.getQuery();
		GMCoreTools.checkDescription(actualQuery, expectedQuery);
	}

	@Test
	public void testYearMonthDayHourMinuteSecondDefaultZone() throws Exception {

		Date expectedDate = getExpectedDate(2011, 4, 20, 13, 2, 34, 0, TimeZone.getTimeZone("GMT"));

		// @formatter:off
		SelectQuery expectedQuery = sq()
				.from(Person.class, "p")
				.where()
					.value(Integer.valueOf(1)).ne().value(expectedDate)
				.done();
		// @formatter:on

		String queryString = "select * from " + Person.class.getName() + " p where 1 != date(2011Y,5M,20D,13H,2m,34S)";

		ParsedQuery parsedQuery = QueryParser.parse(queryString);
		validatedParsedQuery(parsedQuery);
		Query actualQuery = parsedQuery.getQuery();
		GMCoreTools.checkDescription(actualQuery, expectedQuery);
	}

	@Test
	public void testYearMonthDayHourMinuteSecondCETZone() throws Exception {

		Date expectedDate = getExpectedDate(2011, 4, 20, 13, 2, 34, 0, TimeZone.getTimeZone("CET"));

		// @formatter:off
		SelectQuery expectedQuery = sq()
				.from(Person.class, "p")
				.where()
					.value(Integer.valueOf(1)).ne().value(expectedDate)
				.done();
		// @formatter:on

		String queryString = "select * from " + Person.class.getName() + " p where 1 != date(2011Y,5M,20D,13H,2m,34S,+0200Z)";

		ParsedQuery parsedQuery = QueryParser.parse(queryString);
		validatedParsedQuery(parsedQuery);
		Query actualQuery = parsedQuery.getQuery();
		GMCoreTools.checkDescription(actualQuery, expectedQuery);
	}

	@Test
	public void testYearMonthDayHourMinuteSecondMilliDefaultZone() throws Exception {

		Date expectedDate = getExpectedDate(2011, 4, 20, 13, 2, 34, 123, TimeZone.getTimeZone("GMT"));

		// @formatter:off
		SelectQuery expectedQuery = sq()
				.from(Person.class, "p")
				.where()
					.value(Integer.valueOf(1)).ne().value(expectedDate)
				.done();
		// @formatter:on

		String queryString = "select * from " + Person.class.getName() + " p where 1 != date(2011Y,5M,20D,13H,2m,34S,123s)";

		ParsedQuery parsedQuery = QueryParser.parse(queryString);
		validatedParsedQuery(parsedQuery);
		Query actualQuery = parsedQuery.getQuery();
		GMCoreTools.checkDescription(actualQuery, expectedQuery);
	}

	@Test
	public void testYearMonthDayHourMinuteSecondMilliCETZone() throws Exception {

		Date expectedDate = getExpectedDate(2011, 4, 20, 13, 2, 34, 123, TimeZone.getTimeZone("CET"));

		// @formatter:off
		SelectQuery expectedQuery = sq()
				.from(Person.class, "p")
				.where()
					.value(Integer.valueOf(1)).ne().value(expectedDate)
				.done();
		// @formatter:on

		String queryString = "select * from " + Person.class.getName() + " p where 1 != date(2011Y,5M,20D,13H,2m,34S,123s,+0200Z)";

		ParsedQuery parsedQuery = QueryParser.parse(queryString);
		validatedParsedQuery(parsedQuery);
		Query actualQuery = parsedQuery.getQuery();
		GMCoreTools.checkDescription(actualQuery, expectedQuery);
	}

	@Test
	public void testYearMonthDayHourMinuteSecondMilliNegativeZone() throws Exception {

		Date expectedDate = getExpectedDate(2011, 4, 20, 13, 22, 34, 123, TimeZone.getTimeZone("GMT-04:00"));

		// @formatter:off
		SelectQuery expectedQuery = sq()
				.from(Person.class, "p")
				.where()
					.value(Integer.valueOf(1)).ne().value(expectedDate)
				.done();
		// @formatter:on

		String queryString = "select * from " + Person.class.getName() + " p where 1 != date(2011Y,5M,20D,13H,22m,34S,123s,-0400Z)";

		ParsedQuery parsedQuery = QueryParser.parse(queryString);
		validatedParsedQuery(parsedQuery);
		Query actualQuery = parsedQuery.getQuery();
		GMCoreTools.checkDescription(actualQuery, expectedQuery);
	}

	@Test
	public void testYearMonthDayHourMinuteSecondMilliPositiveZoneFail() throws Exception {

		String queryString = "select * from " + Person.class.getName() + " p where 1 != date(2011Y,5M,20D,13H,22m,34S,123s, 9999Z)";

		List<GmqlParsingError> expectedErrorList = getExpectedError("TimeZone could not be parsed");

		ParsedQuery parsedQuery = QueryParser.parse(queryString);
		validatedInvalidParsedQuery(parsedQuery, expectedErrorList);
	}

	private Date getExpectedDate(int year, int month, int day, int hour, int minute, int second, int millisecond, TimeZone zone) {
		Calendar cal = Calendar.getInstance();
		cal.clear();
		cal.setTimeZone(zone);
		cal.set(Calendar.YEAR, year);
		cal.set(Calendar.MONTH, month);
		cal.set(Calendar.DAY_OF_MONTH, day);
		cal.set(Calendar.HOUR_OF_DAY, hour);
		cal.set(Calendar.MINUTE, minute);
		cal.set(Calendar.SECOND, second);
		cal.set(Calendar.MILLISECOND, millisecond);
		return cal.getTime();
	}

}
