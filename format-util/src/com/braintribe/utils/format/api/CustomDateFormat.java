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
package com.braintribe.utils.format.api;

import java.util.Date;
import java.util.List;

import com.braintribe.model.time.CalendarOffset;
import com.braintribe.model.time.DateOffset;
import com.braintribe.model.time.TimeZoneOffset;

// All dates are handled for UTC timezone
public interface CustomDateFormat {

	String formatDate(Date date, String pattern);
	
	Date parseDate(String dateString, String pattern);
	
	int getYear(Date date);
	
	int getMonth(Date date);
	
	int getDay(Date date);
	
	int getHour(Date date);
	
	int getMinute(Date date);
	
	int getSecond(Date date);
	
	int getMilliSecond(Date date);
	
	int getDayMax(Date date);
	
	Date setYear(Date date, int value);
	
	Date setMonth(Date date, int value);
	
	Date setDay(Date date, int value);
	
	Date setHour(Date date, int value);
	
	Date setMinute(Date date, int value);
	
	Date setSecond(Date date, int value);
	
	Date setMilliSecond(Date date, int value);
	
	Date addYear(Date date, int value);
	
	Date addMonth(Date date, int value);
	
	Date addDay(Date date, int value);
	
	Date addHour(Date date, int value);
	
	Date addMinute(Date date, int value);
	
	Date addSecond(Date date, int value);
	
	Date addMilliSecond(Date date, int value);
	
	Date addMicroSecond(Date date, int value);
	
	Date addNanoSecond(Date date, int value);
	
	Date shiftForTimeZone(Date date, int value);
	
	Date createDateFromOffsetList(List<CalendarOffset> offsetList);
	
	Date addCalendarOffsetToDate(Date date, CalendarOffset offset);
	
	Date addDateOffsetToDate(Date date, DateOffset offset);
	
	Date addTimeZoneOffsetToDate(Date date, TimeZoneOffset offset);
	
}
