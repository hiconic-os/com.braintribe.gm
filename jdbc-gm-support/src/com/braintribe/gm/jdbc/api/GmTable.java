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

import static com.braintribe.utils.lcd.CollectionTools2.asMap;
import static com.braintribe.utils.lcd.CollectionTools2.asSet;
import static com.braintribe.utils.lcd.CollectionTools2.newSet;

import java.sql.Connection;
import java.util.Map;
import java.util.Set;

/**
 * @see GmDb
 * 
 * @author peter.gazdik
 */
public interface GmTable {

	String getName();

	/** In order they were provided to the {@link GmTableBuilder}. */
	Set<GmColumn<?>> getColumns();

	/** In order they were provided to the {@link GmTableBuilder}. */
	Set<GmIndex> getIndices();

	void ensure();

	/** Converts given list of columns and values to a Map and calls {@code insert(null, columnToValueMap)}. */
	default void insert(Object... columnsAndValues) {
		insert(null, asMap(columnsAndValues));
	}

	/** Equivalent to {@code insert(null, columnsToValues)}. */
	default void insert(Map<GmColumn<?>, Object> columnsToValues) {
		insert(null, columnsToValues);
	}

	/** Converts given list of columns and values to a map and calls {@link #insert(Connection, Map)}. */
	default void insert(Connection c, Object... columnsAndValues) {
		insert(c, asMap(columnsAndValues));
	}

	/**
	 * Inserts given vales for given columns in the table.
	 * <p>
	 * NOTE that the connection can be {@code null}, in which case the table will acquire it's own connection. Such an insert would thus be a single
	 * transaction. Pass a connection if you want to do more than just a single insert as a single transaction.
	 */
	void insert(Connection c, Map<GmColumn<?>, Object> columnsToValues);

	default GmSelectBuilder select() {
		return select(newSet(getColumns()));
	}

	default GmSelectBuilder select(GmColumn<?>... columns) {
		return select(asSet(columns));
	}

	GmSelectBuilder select(Set<GmColumn<?>> columns);

	/** Converts given list of columns and values to a Map and calls {@link #update(Map)}. */
	default GmUpdateBuilder update(Object... columnsAndValues) {
		return update(asMap(columnsAndValues));
	}

	GmUpdateBuilder update(Map<GmColumn<?>, Object> columnsToValues);

	GmUpdateBuilder delete();

}
