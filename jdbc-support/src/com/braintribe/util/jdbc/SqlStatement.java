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
// ============================================================================
// Copyright BRAINTRIBE TECHNOLOGY GMBH, Austria, 2002-2022
// 
// This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
// 
// This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
// 
// You should have received a copy of the GNU Lesser General Public License along with this library; See http://www.gnu.org/licenses/.
// ============================================================================
package com.braintribe.util.jdbc;

import static com.braintribe.utils.lcd.CollectionTools2.newList;

import java.util.List;

/**
 * @author peter.gazdik
 */
public class SqlStatement {

	public String sql;
	public List<Object> parameters;

	public SqlStatement() {
		this(null);
	}

	public SqlStatement(String sql) {
		this(sql, newList());
	}

	public SqlStatement(String sql, List<Object> parameters) {
		this.sql = sql;
		this.parameters = parameters;
	}

	@Override
	public SqlStatement clone() {
		return new SqlStatement(this.sql, newList(this.parameters));
	}

	public static class SqlStatementBuilder {
		public StringBuilder sb = new StringBuilder();
		public List<Object> parameters = newList();

		public SqlStatement build() {
			return new SqlStatement(sb.toString(), parameters);
		}
	}

}
