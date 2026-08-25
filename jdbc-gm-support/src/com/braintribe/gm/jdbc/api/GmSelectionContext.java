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
package com.braintribe.gm.jdbc.api;

/**
 * @author peter.gazdik
 */
public interface GmSelectionContext {

	GmLobLoadingMode lobLoadingMode(GmColumn<?> column);

	/**
	 * Tells whether the values are only being resolved for the scope of a single row, i.e. while the underlying {@link java.sql.ResultSet} is still
	 * open and positioned on that row - see {@link GmSelectBuilder#forEachRow} and {@link GmSelectBuilder#mapRows}.
	 * <p>
	 * A column may use this to hand out a value which reads straight from the DB rather than one backed by a copy. It must then register the
	 * invalidation of that value with {@link #onRowScopeExit(Runnable)}.
	 */
	boolean isScopedRead();

	/**
	 * Registers an action to be run once the scope of the current row ends, i.e. before the {@link java.sql.ResultSet} is moved to the next row.
	 * <p>
	 * This is only supported while {@link #isScopedRead()}, and is meant for invalidating values which cannot be read any longer, so that using them
	 * later fails with a clear error rather than reading from a connection which was given back to the pool.
	 */
	void onRowScopeExit(Runnable action);

}
