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
package com.braintribe.util.jdbc;

public class DatabaseTypes {

	private String dbName;
	private String clobType;
	private String blobType;
	private String timestampType;

	public DatabaseTypes(String dbName, String clobType, String blobType, String timestampType) {
		super();
		this.dbName = dbName;
		this.clobType = clobType;
		this.blobType = blobType;
		this.timestampType = timestampType;
	}

	public String getDbName() {
		return dbName;
	}

	public String getClobType() {
		return clobType;
	}

	public String getBlobType() {
		return blobType;
	}

	public String getTimestampType() {
		return timestampType;
	}

	@Override
	public String toString() {
		return "DB: "+dbName+", CLOB: "+clobType+", BLOB: "+blobType+", TIMESTAMP: "+timestampType;
	}
	
}
