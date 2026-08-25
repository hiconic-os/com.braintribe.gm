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
package com.braintribe.gm.jdbc.test;

import static com.braintribe.utils.lcd.CollectionTools2.newList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.ByteArrayInputStream;
import java.util.List;

import org.junit.Test;

import com.braintribe.common.db.DbVendor;
import com.braintribe.gm.jdbc.api.GmColumn;
import com.braintribe.gm.jdbc.api.GmSelectBuilder;
import com.braintribe.gm.jdbc.api.GmTable;
import com.braintribe.model.resource.Resource;

/**
 * Tests {@link GmSelectBuilder#forEachRow} and {@link GmSelectBuilder#mapRows}, which hand rows over while the query is still open, so that a
 * {@link Resource} can be streamed straight from the DB rather than copied into a buffer first.
 *
 * @author peter.gazdik
 */
public class GmDb_ScopedRead_Tests extends AbstractGmDbTestBase {

	public GmDb_ScopedRead_Tests(DbVendor vendor) {
		super(vendor);
	}

	/** Far beyond ResourceColumn's limit for storing a value as a String, so it is stored as a BLOB and thus really streamed. */
	private static final int BIG = 200_000;

	private final GmColumn<String> colIdStr = gmDb.shortString255("id").primaryKey().notNull().done();
	private final GmColumn<Resource> colPayload = gmDb.resource("payload").done();

	// ###############################################
	// ## . . . . . . . . . Tests . . . . . . . . . ##
	// ###############################################

	@Test
	public void mapRowsReturnsMappedValues() {
		GmTable table = ensureTable("scoped_map" + tmSfx);
		insert(table, "one");
		insert(table, "two");

		List<String> ids = table.select(colIdStr).orderBy(colIdStr.getSingleSqlColumn()).mapRows(row -> row.getValue(colIdStr));

		assertThat(ids).containsExactly("one", "two");
	}

	@Test
	public void forEachRowVisitsEveryRow() {
		GmTable table = ensureTable("scoped_each" + tmSfx);
		insert(table, "one");
		insert(table, "two");

		List<String> ids = newList();
		table.select(colIdStr).orderBy(colIdStr.getSingleSqlColumn()).forEachRow(row -> ids.add(row.getValue(colIdStr)));

		assertThat(ids).containsExactlyInAnyOrder("one", "two");
	}

	/** The point of the whole thing - the resource is readable while its row is being processed, without a copy in between. */
	@Test
	public void resourceIsReadableWithinScope() {
		GmTable table = ensureTable("scoped_read" + tmSfx);

		Resource expected = binaryResource();
		insert(table, "one", expected);

		List<byte[]> contents = table.select(colPayload).mapRows(row -> toBytes(row.getValue(colPayload)));

		assertThat(contents).hasSize(1);
		assertThat(contents.get(0)).containsExactly(toBytes(expected));
	}

	/**
	 * A resource kept beyond its row must fail loudly. Otherwise it would read from a connection which was long given back to the pool.
	 */
	@Test
	public void resourceIsNotReadableAfterScope() {
		GmTable table = ensureTable("scoped_esc" + tmSfx);
		insert(table, "one", binaryResource());

		List<Resource> escaped = table.select(colPayload).mapRows(row -> row.getValue(colPayload));
		assertThat(escaped).hasSize(1);

		assertThatThrownBy(() -> escaped.get(0).openStream()) //
				.describedAs("A resource kept beyond the scoped read was still readable.") //
				.isInstanceOf(IllegalStateException.class);
	}

	/** Ordering and pagination are built separately for a scoped read, so they are worth checking here rather than only for rows(). */
	@Test
	public void mapRowsHonorsOrderByAndLimit() {
		GmTable table = ensureTable("scoped_page" + tmSfx);
		insert(table, "a");
		insert(table, "b");
		insert(table, "c");

		List<String> ids = table.select(colIdStr) //
				.orderBy(colIdStr.getSingleSqlColumn()) //
				.limitAndOffset(2, 1) //
				.mapRows(row -> row.getValue(colIdStr));

		assertThat(ids).containsExactly("b", "c");
	}

	@Test
	public void mapRowsHonorsWhere() {
		GmTable table = ensureTable("scoped_where" + tmSfx);
		insert(table, "one");
		insert(table, "two");

		List<String> ids = table.select(colIdStr).whereColumn(colIdStr, "two").mapRows(row -> row.getValue(colIdStr));

		assertThat(ids).containsExactly("two");
	}

	@Test
	public void scopedReadOfEmptyResultIsEmpty() {
		GmTable table = ensureTable("scoped_empty" + tmSfx);

		assertThat(table.select(colIdStr).mapRows(row -> row.getValue(colIdStr))).isEmpty();
	}

	// ###############################################
	// ## . . . . . . . . Helpers . . . . . . . . . ##
	// ###############################################

	private GmTable ensureTable(String tableName) {
		if (tableName.length() > 30)
			throw new IllegalArgumentException("Table name too long, Oracle only supports length <= 30. Table name: " + tableName);

		GmTable result = gmDb.newTable(tableName) //
				.withColumns(colIdStr, colPayload) //
				.done();

		result.ensure();

		return result;
	}

	private void insert(GmTable table, String id) {
		insert(table, id, null);
	}

	private void insert(GmTable table, String id, Resource payload) {
		table.insert(colIdStr, id, colPayload, payload);
	}

	private Resource binaryResource() {
		byte[] bytes = new byte[BIG];
		for (int i = 0; i < BIG; i++)
			bytes[i] = (byte) i;

		Resource result = Resource.createTransient(() -> new ByteArrayInputStream(bytes));
		result.setMimeType("application/octet-stream");
		result.setFileSize((long) BIG);

		return result;
	}

}
