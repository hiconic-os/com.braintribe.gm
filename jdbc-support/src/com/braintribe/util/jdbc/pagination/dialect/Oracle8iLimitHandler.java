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
package com.braintribe.util.jdbc.pagination.dialect;

import java.util.Locale;

import com.braintribe.util.jdbc.pagination.AbstractLimitHandler;
import com.braintribe.util.jdbc.pagination.LimitHelper;
import com.braintribe.util.jdbc.pagination.RowSelection;

public class Oracle8iLimitHandler extends AbstractLimitHandler {

	public static final Oracle8iLimitHandler INSTANCE = new Oracle8iLimitHandler();

	@Override
	public String processSql(String sql, RowSelection selection) {
		final boolean hasOffset = LimitHelper.hasFirstRow( selection );
		sql = sql.trim();
		boolean isForUpdate = false;
		if (sql.toLowerCase(Locale.ROOT).endsWith( " for update" )) {
			sql = sql.substring( 0, sql.length() - 11 );
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
			pagingSelect.append( " ) row_ ) where rownum_ <= ? and rownum_ > ?" );
		}
		else {
			pagingSelect.append( " ) where rownum <= ?" );
		}

		if (isForUpdate) {
			pagingSelect.append( " for update" );
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
