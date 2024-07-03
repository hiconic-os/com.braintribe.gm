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
package com.braintribe.codec.marshaller.stabilization;

import com.braintribe.utils.lcd.NullSafe;

public class QualifiedPersistenceId implements Comparable<QualifiedPersistenceId> {
	private String partition;
	private Comparable<Object> id;
	
	public QualifiedPersistenceId(String partition, Comparable<Object> id) {
		super();
		this.partition = partition;
		this.id = id;
	}
	
	@Override
	public int compareTo(QualifiedPersistenceId o) {
		int res = NullSafe.cmp(partition, o.partition);
		
		if (res != 0)
			return res;
		
		return id.compareTo(o.id);
	}
	
	@Override
	public String toString() {
		if (partition == null) {
			return id.toString();
		}
		else {
			return partition + ':' + id;
		}
	}
	
	public boolean hasPartition() {
		return partition != null;
	}
}
