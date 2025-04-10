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

import java.io.InputStream;
import java.io.StringReader;
import java.util.List;
import java.util.stream.Collectors;

import javax.sql.DataSource;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.braintribe.codec.marshaller.api.GmCodec;
import com.braintribe.codec.marshaller.json.JsonStreamMarshaller;
import com.braintribe.common.db.DbVendor;
import com.braintribe.common.db.DerbySupportingDbTestSession;
import com.braintribe.exception.Exceptions;
import com.braintribe.gm.jdbc.api.GmColumn;
import com.braintribe.gm.jdbc.api.GmDb;
import com.braintribe.gm.jdbc.api.GmRow;
import com.braintribe.model.resource.Resource;
import com.braintribe.utils.IOTools;
import com.braintribe.utils.RandomTools;
import com.braintribe.utils.stream.ReaderInputStream;
import com.braintribe.utils.stream.api.StreamPipes;

/**
 * @author peter.gazdik
 */
@RunWith(Parameterized.class)
public abstract class AbstractGmDbTestBase {

	// ###############################################
	// ## . . . . . . . . Static . . . . . . . . . .##
	// ###############################################

	private static DerbySupportingDbTestSession dbTestSession;

	// We user timestamp suffix for table/index names so we can run tests multiple times without re-deploying docker containers
	protected static final String tmSfx = "_" + RandomTools.timeStamp();

	@BeforeClass
	public static void beforeClass() throws Exception {
		dbTestSession = DerbySupportingDbTestSession.startDbTest();
	}

	@AfterClass
	public static void afterClass() throws Exception {
		dbTestSession.shutdownDbTest();
	}

	// ###############################################
	// ## . . . . . . . . . Tests . . . . . . . . . ##
	// ###############################################

	@Parameters
	public static Object[][] params() {
		return new Object[][] { //
				{ DbVendor.h2 }, //
				// { DbVendor.derby }, //
				// { DbVendor.postgres }, //
				// { DbVendor.mysql }, //
				// { DbVendor.mssql }, //
				// { DbVendor.oracle }, //
		};
	}

	private static final GmCodec<Object, String> jsonCodec = new JsonStreamMarshaller();

	protected final DbVendor vendor;
	protected final DataSource dataSource;

	protected final GmDb gmDb;

	public AbstractGmDbTestBase(DbVendor vendor) {
		this.vendor = vendor;
		this.dataSource = dbTestSession.contract.dataSource(vendor);

		this.gmDb = GmDb.newDb(dataSource) //
				.withDefaultCodec(jsonCodec) //
				.withStreamPipeFactory(StreamPipes.simpleFactory()) //
				.done();
	}

	// ###############################################
	// ## . . . . . . . . Strings . . . . . . . . . ##
	// ###############################################

	protected String str64K() {
		return doubleNTimes(str1K(), 6);
	}

	protected String str4K() {
		return doubleNTimes(str1K(), 2);
	}

	protected String str1K() {
		String s = str8Chars();
		s = doubleNTimes(s, 7);
		return s.substring(0, 1000);
	}

	protected String str8Chars() {
		return "HÆllo!!!";
	}

	protected String doubleNTimes(String s, int n) {
		while (n-- > 0)
			s += s;
		return s;
	}

	private static final String TEXT_PLAIN_MIME_TYPE = "text/plain";

	protected Resource textResource(String s) {
		Resource result = Resource.createTransient(() -> new ReaderInputStream(new StringReader(s)));
		result.setMimeType(TEXT_PLAIN_MIME_TYPE);
		result.setFileSize(2L * s.length());
		return result;
	}

	// ###############################################
	// ## . . . . . Query Result Asserts . . . . . .##
	// ###############################################

	protected List<GmRow> queryResult;

	protected void collectResult(Iterable<GmRow> rows) {
		queryResult = newList();

		for (GmRow gmRow : rows)
			queryResult.add(gmRow);
	}

	protected void assertResultSize(int expected) {
		assertThat(queryResult).hasSize(expected);
	}

	protected <T> List<T> resultValues(GmColumn<T> column) {
		return queryResult.stream() //
				.map(row -> row.getValue(column)) //
				.collect(Collectors.toList());
	}

	protected void assertSameResource(Resource actual, Resource expected) {
		assertThat(actual).isNotNull();

		byte[] actualBytes = toBytes(actual);
		byte[] expectedBytes = toBytes(expected);

		assertThat(actualBytes).containsExactly(expectedBytes);
	}

	protected byte[] toBytes(Resource resource) {
		try (InputStream inputStream = resource.openStream()) {
			return IOTools.slurpBytes(inputStream);
		} catch (Exception e) {
			throw Exceptions.unchecked(e);
		}
	}

}
