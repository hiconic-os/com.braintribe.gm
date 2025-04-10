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
package com.braintribe.gm.jdbc.impl;

import static com.braintribe.testing.junit.assertions.assertj.core.api.Assertions.assertThat;
import static com.braintribe.utils.lcd.CollectionTools2.asList;
import static org.assertj.core.api.Assertions.assertThat;

import java.sql.Connection;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.Test;

import com.braintribe.common.db.DbVendor;
import com.braintribe.gm.jdbc.api.GmColumn;
import com.braintribe.gm.jdbc.api.GmIndex;
import com.braintribe.gm.jdbc.api.GmTable;
import com.braintribe.gm.jdbc.test.AbstractGmDbTestBase;
import com.braintribe.gm.jdbc.test.GmDb_Ddl_Tests;
import com.braintribe.util.jdbc.JdbcTools;

/**
 * This tests adding columns/indices directly, to test a situation where during parallel execution some columns/indices are first missing but then
 * when an attempt to add them is made within {@link GmTable#ensure()}.
 * 
 * @see GmDb_Ddl_Tests
 * 
 * @author peter.gazdik
 */
public class GmDb_Ddl_Concurrent_Tests extends AbstractGmDbTestBase {

	public GmDb_Ddl_Concurrent_Tests(DbVendor vendor) {
		super(vendor);
	}

	private final GmColumn<String> colIdStr = gmDb.shortString255("id").primaryKey().notNull().done();
	private final GmColumn<String> colStr1 = gmDb.shortString255("str1").done();
	private final GmColumn<String> colStr2 = gmDb.shortString255("str2").done();
	private final GmColumn<String> colStr3 = gmDb.shortString255("str3").done();

	private final GmIndex idxStr1 = gmDb.index("i1", colStr1);
	private final GmIndex idxStr2 = gmDb.index("i2", colStr2);
	private final GmIndex idxStr3 = gmDb.index("i3", colStr3);

	private String tableName;
	private GmTableImpl table;
	private Set<String> columnNames;
	private Set<String> indexNames;

	@Test
	public void testAddingColumnsAndIndices() {
		final String TABLE_NAME = "extended_table" + tmSfx;

		table = (GmTableImpl) gmDb.newTable(TABLE_NAME) //
				.withColumns(colIdStr, colStr1) //
				.withIndices(idxStr1) //
				.done();

		table.ensure();
		assertTableOk(table);

		JdbcTools.withManualCommitConnection(dataSource, () -> "Testing adding columns", this::testAddingColumns);
		JdbcTools.withManualCommitConnection(dataSource, () -> "Testing adding columns", this::testAddingIndices);
	}

	private void assertTableOk(GmTable table) {
		final Set<String> _columnNames = table.getColumns().stream().map(GmColumn::getGmName).collect(Collectors.toSet());
		final Set<String> _indexNames = table.getIndices().stream().map(GmIndex::getName).collect(Collectors.toSet());

		JdbcTools.withConnection(dataSource, false, () -> "Asserting creation of table: " + table.getName(), c -> {
			tableName = JdbcTools.tableExists(c, table.getName());
			if (tableName != null) {
				columnNames = JdbcTools.columnsExist(c, tableName, _columnNames);
				indexNames = JdbcTools.indicesExist(c, tableName, _indexNames);
			}
		});

		assertThat(tableName).isNotNull();
		assertThat(columnNames).containsExactlyElementsOf(_columnNames);
		assertThat(indexNames).containsExactlyElementsOf(_indexNames);
	}

	private void testAddingColumns(Connection c) {
		String sqlTableName = JdbcTools.tableExists(c, tableName);

		Set<GmColumn<?>> addedColumns;

		addedColumns = table.addColumns(c, sqlTableName, asList(colStr1));
		assertColumns(addedColumns /* , nothing */);

		addedColumns = table.addColumns(c, sqlTableName, asList(colStr1, colStr2, colStr3));
		assertColumns(addedColumns, colStr2, colStr3);
	}

	private void assertColumns(Set<GmColumn<?>> columns, GmColumn<?>... expectedColumns) {
		assertThat(columns).containsExactlyInAnyOrder(expectedColumns);
	}

	private void testAddingIndices(Connection c) {
		String sqlTableName = JdbcTools.tableExists(c, tableName);

		Set<GmIndex> addedIndices;

		addedIndices = table.addIndices(c, sqlTableName, asList(idxStr1));
		assertIndices(addedIndices /* , nothing */);

		addedIndices = table.addIndices(c, sqlTableName, asList(idxStr1, idxStr2, idxStr3));
		assertIndices(addedIndices, idxStr2, idxStr3);
	}

	private void assertIndices(Set<GmIndex> indices, GmIndex... expectedIndices) {
		assertThat(indices).containsExactlyInAnyOrder(expectedIndices);
	}

}
