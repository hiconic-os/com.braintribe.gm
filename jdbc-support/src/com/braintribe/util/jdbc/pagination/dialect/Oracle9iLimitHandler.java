// ============================================================================
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
// ============================================================================
// Copyright BRAINTRIBE TECHNOLOGY GMBH, Austria, 2002-2022
// 
// This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
// 
// This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
// 
// You should have received a copy of the GNU Lesser General Public License along with this library; See http://www.gnu.org/licenses/.
// ============================================================================
package com.braintribe.util.jdbc.pagination.dialect;

import java.util.Locale;

import com.braintribe.util.jdbc.pagination.AbstractLimitHandler;
import com.braintribe.util.jdbc.pagination.LimitHelper;
import com.braintribe.util.jdbc.pagination.RowSelection;

public class Oracle9iLimitHandler extends AbstractLimitHandler {
	
	public static final Oracle9iLimitHandler INSTANCE = new Oracle9iLimitHandler();

	@Override
	public String processSql(String sql, RowSelection selection) {
		final boolean hasOffset = LimitHelper.hasFirstRow( selection );
		sql = sql.trim();
		String forUpdateClause = null;
		boolean isForUpdate = false;
		final int forUpdateIndex = sql.toLowerCase(Locale.ROOT).lastIndexOf( "for update" );
		if (forUpdateIndex > -1) {
			// save 'for update ...' and then remove it
			forUpdateClause = sql.substring( forUpdateIndex );
			sql = sql.substring( 0, forUpdateIndex - 1 );
			isForUpdate = true;
		}

		final StringBuilder pagingSelect = new StringBuilder( sql.length() + 100 );
		if (hasOffset) {
		pagingSelect.append( "select * from ( select row_.*, rownum rownum_ from ( " );
		}
		else {
			pagingSelect.append( "select * from ( " );
		}
		pagingSelect.append( sql );
		if (hasOffset) {
			pagingSelect.append( " ) row_ where rownum <= ?) where rownum_ > ?" );
		}
		else {
			pagingSelect.append( " ) where rownum <= ?" );
		}

		if (isForUpdate) {
			pagingSelect.append( " " );
			pagingSelect.append( forUpdateClause );
		}

		return pagingSelect.toString();
	}

	@Override
	public boolean supportsLimit() {
		return true;
	}

	@Override
	public boolean bindLimitParametersInReverseOrder() {
		return true;
	}

	@Override
	public boolean useMaxForLimit() {
		return true;
	}

}
