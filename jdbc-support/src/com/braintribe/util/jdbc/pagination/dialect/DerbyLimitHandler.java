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

public class DerbyLimitHandler extends AbstractLimitHandler {

	public static final DerbyLimitHandler INSTANCE = new DerbyLimitHandler();

	/**
	 * {@inheritDoc}
	 * <p/>
	 * From Derby 10.5 Docs:
	 * <pre>
	 * Query
	 * [ORDER BY clause]
	 * [result offset clause]
	 * [fetch first clause]
	 * [FOR UPDATE clause]
	 * [WITH {RR|RS|CS|UR}]
	 * </pre>
	 */
	@Override
	public String processSql(String sql, RowSelection selection) {
		final StringBuilder sb = new StringBuilder( sql.length() + 50 );
		final String normalizedSelect = sql.toLowerCase(Locale.ROOT).trim();
		final int forUpdateIndex = normalizedSelect.lastIndexOf( "for update" );

		if (hasForUpdateClause( forUpdateIndex )) {
			sb.append( sql.substring( 0, forUpdateIndex - 1 ) );
		}
		else if (hasWithClause( normalizedSelect )) {
			sb.append( sql.substring( 0, getWithIndex( sql ) - 1 ) );
		}
		else {
			sb.append( sql );
		}

		if (LimitHelper.hasFirstRow( selection )) {
			sb.append( " offset " ).append( selection.getFirstRow() ).append( " rows fetch next " );
		}
		else {
			sb.append( " fetch first " );
		}

		sb.append( getMaxOrLimit( selection ) ).append(" rows only" );

		if (hasForUpdateClause( forUpdateIndex )) {
			sb.append( ' ' );
			sb.append( sql.substring( forUpdateIndex ) );
		}
		else if (hasWithClause( normalizedSelect )) {
			sb.append( ' ' ).append( sql.substring( getWithIndex( sql ) ) );
		}
		return sb.toString();
	}

	@Override
	public boolean supportsLimit() {
		return isTenPointFiveReleaseOrNewer();
	}

	@Override
	public boolean supportsLimitOffset() {
		return isTenPointFiveReleaseOrNewer();
	}

	private boolean isTenPointFiveReleaseOrNewer() {
		// TODO implement based on dialect if needed. For now, I don't really care about older versions of Derby.
		return true;
	}

	@Override
	public boolean supportsVariableLimit() {
		return false;
	}

	private boolean hasForUpdateClause(int forUpdateIndex) {
		return forUpdateIndex >= 0;
	}

	private boolean hasWithClause(String normalizedSelect){
		return normalizedSelect.startsWith( "with ", normalizedSelect.length() - 7 );
	}

	private int getWithIndex(String querySelect) {
		int i = querySelect.lastIndexOf( "with " );
		if ( i < 0 ) {
			i = querySelect.lastIndexOf( "WITH " );
		}
		return i;
	}

}
