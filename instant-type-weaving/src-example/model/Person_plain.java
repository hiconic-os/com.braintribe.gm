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
package model;

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.GmtsPlainEntityStub;

public class Person_plain extends GmtsPlainEntityStub implements Person, Person_weak {
	private String name;
	private long count;
	private String transientName;

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String value) {
		this.name = value;
	}

	@Override
	public Object readName() {
		return name;
	}

	@Override
	public void writeName(Object value) {
		this.name = (String) value;
	}

	@Override
	public long getCount() {
		return count;
	}

	@Override
	public void setCount(long value) {
		this.count = value;
	}

	@Override
	public Object readCount() {
		return count;
	}

	@Override
	public void writeCount(Object value) {
		this.count = (Long) value;
	}

	@Override
	public Person getFather() {
		throw new UnsupportedOperationException("Like other methods!");
	}

	@Override
	public void setFather(Person value) {
		throw new UnsupportedOperationException("Like other methods!");
	}

	@Override
	public String getTransientName() {
		return transientName;
	}

	@Override
	public void setTransientName(String transientName) {
		this.transientName = transientName;
	}

	@Override
	public EntityType<?> type() {
		return Person_EntityType.INSTANCE;
	}

	// ###########################################
	// ## . . To avoid compilation problems . . ##
	// ###########################################

	@Override
	public <T> T getId() {
		return null;
	}

	@Override
	public void setId(Object id) {
		// ignore
	}

	@Override
	public String getPartition() {
		return null;
	}

	@Override
	public void setPartition(String partition) {
		// ignore
	}

	@Override
	public String getGlobalId() {
		return null;
	}

	@Override
	public void setGlobalId(String globalId) {
		// ignore
	}
}