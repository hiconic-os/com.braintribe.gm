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
package com.braintribe.model.dbs;

import com.braintribe.model.generic.annotation.SelectiveInformation;
import com.braintribe.model.generic.annotation.meta.Mandatory;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

@SelectiveInformation("${name}")
public interface DbColumn extends IdentifiableDbEntity {

	EntityType<DbColumn> T = EntityTypes.T(DbColumn.class);

	// @formatter:off
	DbTable getOwner();
	void setOwner(DbTable owner);

	@Mandatory
	String getName();
	void setName(String name);

	/** {@link java.sql.Types} code */
	int getDataType();
	void setDataType(int dataType);

	/** Not a value retrieved via jdbc, but just the name of the constant from {@link java.sql.Types}. */
	String getDataTypeName();
	void setDataTypeName(String dataTypeName);

	/** This is the actual TYPE_NAME retrieved via jdbc. */
	String getTypeName();
	void setTypeName(String typeName);

	Boolean getNullable();
	void setNullable(Boolean nullable);

	int getOrdinalPosition();
	void setOrdinalPosition(int ordinalPosition);

	int getColumnSize();
	void setColumnSize(int columnSize);

	String getRemarks();
	void setRemarks(String remarks);

	boolean getIsPrimaryKey();
	void setIsPrimaryKey(boolean isPrimaryKey);

	DbTable getReferencedTable();
	void setReferencedTable(DbTable primaryKeyTable);
	// @formatter:on

}
