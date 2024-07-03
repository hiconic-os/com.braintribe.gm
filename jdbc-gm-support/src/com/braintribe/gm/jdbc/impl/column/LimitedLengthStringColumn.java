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
package com.braintribe.gm.jdbc.impl.column;

import com.braintribe.gm.jdbc.impl.column.SimpleSingularGmColumn.StringColumn;

/**
 * @author peter.gazdik
 */
public class LimitedLengthStringColumn extends StringColumn {

	private final int size;

	public LimitedLengthStringColumn(String name, String sqlType, int size) {
		super(name, sqlType);
		this.size = size;
	}

	@Override
	protected String sqlType() {
		return super.sqlType() + "(" + size + ")";
	}

}
