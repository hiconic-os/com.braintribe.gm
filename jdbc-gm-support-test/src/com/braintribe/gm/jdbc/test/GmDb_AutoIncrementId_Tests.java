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

import static com.braintribe.utils.SysPrint.spOut;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;
import java.util.stream.Collectors;

import org.junit.Test;

import com.braintribe.common.db.DbVendor;
import com.braintribe.gm.jdbc.api.GmColumn;
import com.braintribe.gm.jdbc.api.GmTable;

/**
 * @author peter.gazdik
 */
public class GmDb_AutoIncrementId_Tests extends AbstractGmDbTestBase {

	public GmDb_AutoIncrementId_Tests(DbVendor vendor) {
		super(vendor);
	}

	// ## Column definitions

	private final GmColumn<Long> colIdLong = gmDb.autoIncrementPrimaryKeyCol("id");
	private final GmColumn<String> colString255 = gmDb.shortString255("str255").done();

	// ##################################################
	// ## . . . . . . . . Actual Tests . . . . . . . . ##
	// ##################################################

	@Test
	public void testCreateAndRead() {
		if (vendor ==DbVendor.oracle) {
			spOut("Skipping test for Oracle. Auto-increment doesn't work for v11 and there is no ");
			return;
		}
		
		final String TABLE_NAME = "auto_incr_id" + tmSfx;
		GmTable table = ensureTable(TABLE_NAME);

		doStandardInsert(table, "V1"); // 1, V1
		doStandardInsert(table, "V2"); // 2, V2
		doStandardInsert(table, "V3"); // 3, V3

		collectResult(table.select().rows());

		// ## Assertions

		assertResultSize(3);

		Set<String> results = queryResult.stream() //
				.map(row -> row.getValue(colIdLong) + "#" + row.getValue(colString255)) //
				.collect(Collectors.toSet());

		assertThat(results).contains("1#V1", "2#V2", "3#V3");
	}

	private GmTable ensureTable(final String TABLE_NAME) {
		GmTable table = newGmTable(TABLE_NAME);
		table.ensure();

		return table;
	}

	private GmTable newGmTable(final String TABLE_NAME) {
		if (TABLE_NAME.length() > 30)
			throw new IllegalArgumentException("Table name too long, Oracle only supports length <= 30. Table name: " + TABLE_NAME);

		return gmDb.newTable(TABLE_NAME) //
				.withColumns( //
						colIdLong, //
						colString255 //
				).done();
	}

	private void doStandardInsert(GmTable table, String str) {
		table.insert( //
				colString255, str //
		);
	}

}
