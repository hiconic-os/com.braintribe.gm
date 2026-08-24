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
package com.braintribe.gm.jdbc.test;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.sql.ResultSet;
import java.util.List;

import javax.sql.DataSource;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import com.braintribe.common.db.BasicDbTestSession;
import com.braintribe.exception.Exceptions;
import com.braintribe.gm.jdbc.api.GmColumn;
import com.braintribe.gm.jdbc.api.GmDb;
import com.braintribe.gm.jdbc.api.GmRow;
import com.braintribe.gm.jdbc.api.GmTable;
import com.braintribe.model.resource.Resource;
import com.braintribe.testing.category.SpecialEnvironment;
import com.braintribe.util.jdbc.JdbcTools;
import com.braintribe.utils.IOTools;
import com.braintribe.utils.RandomTools;
import com.braintribe.utils.stream.api.StreamPipes;

/**
 * Tests that the Large Objects behind a {@link GmDb#resource(String)} column are deleted together with their row.
 * <p>
 * This is PostgreSQL only, as it's the only DB where a BLOB is a separate entity rather than a value inside the row.
 *
 * @author peter.gazdik
 */
@Category(SpecialEnvironment.class)
public class PgBlobCleanup_Tests {

	private static final int POSTGRES_PORT = 65432;

	/** Well beyond ResourceColumn's limit for storing a value as a String, so it is guaranteed to become a Large Object. */
	private static final int BIG = 200_000;

	private static BasicDbTestSession dbSession;
	private static DataSource dataSource;
	private static GmDb gmDb;

	@BeforeClass
	public static void beforeClass() {
		dbSession = BasicDbTestSession.startDbTest();
		dataSource = dbSession.contract.postgres(POSTGRES_PORT);

		gmDb = GmDb.newDb(dataSource) //
				.withStreamPipeFactory(StreamPipes.simpleFactory()) //
				// .withBlobCleanup(false) // Uncomment to see tests fail
				.done();
	}

	@AfterClass
	public static void afterClass() {
		gmDb.preDestroy();
		dbSession.shutdownDbTest();
	}

	private GmColumn<Long> colId;
	private GmColumn<Resource> colPayload;
	private GmTable table;
	private String tableName;

	@Before
	public void before() {
		// A fresh table per test, so tests cannot see each other's Large Objects
		tableName = "gmlo_test_" + RandomTools.timeStamp() + "_" + Math.abs(RandomTools.newStandardUuid().hashCode());

		colId = gmDb.autoIncrementPrimaryKeyCol("id");
		colPayload = gmDb.resource("payload").done();

		table = gmDb.newTable(tableName) //
				.withColumns(colId, colPayload) //
				.done();

		table.ensure();
	}

	@After
	public void after() {
		// Drops the row trigger and the table, the cleanup function stays behind but is harmless
		JdbcTools.withStatement(dataSource, () -> "Dropping test table", s -> s.execute("drop table if exists " + tableName));
	}

	// ###############################################
	// ## . . . . . . . . . Tests . . . . . . . . . ##
	// ###############################################

	@Test
	public void bigResourceRoundTripsAsLargeObject() {
		Resource expected = binaryResource(BIG);
		table.insert(colPayload, expected);

		Long id = onlyId();

		// It really is a Large Object, i.e. we are testing what we think we are testing
		assertThat(largeObjectIdOf(id)).isNotNull();

		Resource actual = table.select(colPayload).whereColumn(colId, id).rows().get(0).getValue(colPayload);
		assertThat(bytesOf(actual)).containsExactly(bytesOf(expected));
	}

	@Test
	public void deletingRowDeletesLargeObject() {
		table.insert(colPayload, binaryResource(BIG));

		Long id = onlyId();
		Long oid = largeObjectIdOf(id);
		assertThat(largeObjectExists(oid)).isTrue();

		table.delete().whereColumn(colId, id);

		assertThat(largeObjectExists(oid)) //
				.describedAs("Large Object " + oid + " was not deleted together with its row.") //
				.isFalse();
	}

	@Test
	public void updatingResourceDeletesPreviousLargeObject() {
		table.insert(colPayload, binaryResource(BIG));

		Long id = onlyId();
		Long oldOid = largeObjectIdOf(id);

		table.update(colPayload, binaryResource(BIG)).whereColumn(colId, id);

		Long newOid = largeObjectIdOf(id);
		assertThat(newOid).isNotEqualTo(oldOid);

		assertThat(largeObjectExists(oldOid)) //
				.describedAs("Large Object " + oldOid + " was not deleted when the row started referencing another one.") //
				.isFalse();
		assertThat(largeObjectExists(newOid)).isTrue();
	}

	/**
	 * A small text resource is stored directly as a String, so there is no Large Object at all - see ResourceColumn. The trigger must cope with that
	 * rather than fail, otherwise such a row could not be deleted.
	 */
	@Test
	public void rowWithoutLargeObjectCanBeDeleted() {
		Resource small = Resource.createTransient(() -> new ByteArrayInputStream("Hello".getBytes()));
		small.setMimeType("text/plain");
		small.setFileSize(5L);

		table.insert(colPayload, small);

		Long id = onlyId();
		assertThat(largeObjectIdOf(id)).isNull();

		table.delete().whereColumn(colId, id);

		assertThat(table.select(colId).rows()).isEmpty();
	}

	/**
	 * The BLOB cleanup function is only supposed to be written when it actually changes, so that a restart does no DDL at all. We detect a rewrite
	 * via xmin, which is the id of the transaction which last wrote the pg_proc row.
	 */
	@Test
	public void repeatedEnsureDoesNotRewriteTheCleanupFunction() {
		String xminAfterFirst = cleanupFunctionXmin();
		assertThat(xminAfterFirst).describedAs("No BLOB cleanup function was created for table " + tableName).isNotNull();

		table.ensure();

		assertThat(cleanupFunctionXmin()) //
				.describedAs("The BLOB cleanup function was written again although nothing changed.") //
				.isEqualTo(xminAfterFirst);
	}

	// ###############################################
	// ## . . . . . . . . Helpers . . . . . . . . . ##
	// ###############################################

	private Resource binaryResource(int size) {
		byte[] bytes = new byte[size];
		for (int i = 0; i < size; i++)
			bytes[i] = (byte) i;

		Resource result = Resource.createTransient(() -> new ByteArrayInputStream(bytes));
		result.setMimeType("application/octet-stream");
		result.setFileSize((long) size);

		return result;
	}

	private Long onlyId() {
		List<GmRow> rows = table.select(colId).rows();
		assertThat(rows).hasSize(1);

		return rows.get(0).getValue(colId);
	}

	/** @return the oid stored in the BLOB column of given row, or <tt>null</tt> if the value is not stored as a Large Object. */
	private Long largeObjectIdOf(Long id) {
		// "payload_blob" is how ResourceColumn names the BLOB column backing the "payload" column
		return queryLong("select payload_blob from " + tableName + " where id = " + id);
	}

	private boolean largeObjectExists(Long oid) {
		return queryLong("select oid from pg_largeobject_metadata where oid = " + oid) != null;
	}

	private String cleanupFunctionXmin() {
		Object[] result = { null };

		JdbcTools.withStatement(dataSource, () -> "Resolving BLOB cleanup function", s -> {
			try (ResultSet rs = s.executeQuery("select xmin::text from pg_proc where proname like '" + tableName + "_gmlo_fn_%'")) {
				if (rs.next())
					result[0] = rs.getString(1);
			}
		});

		return (String) result[0];
	}

	private Long queryLong(String sql) {
		Object[] result = { null };

		JdbcTools.withStatement(dataSource, () -> "Querying: " + sql, s -> {
			try (ResultSet rs = s.executeQuery(sql)) {
				if (rs.next()) {
					long value = rs.getLong(1);
					if (!rs.wasNull())
						result[0] = value;
				}
			}
		});

		return (Long) result[0];
	}

	private byte[] bytesOf(Resource resource) {
		try (InputStream in = resource.openStream()) {
			return IOTools.slurpBytes(in);
		} catch (Exception e) {
			throw Exceptions.unchecked(e);
		}
	}

}
