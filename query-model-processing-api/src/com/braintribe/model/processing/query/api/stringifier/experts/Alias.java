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
package com.braintribe.model.processing.query.api.stringifier.experts;

import com.braintribe.model.query.Source;

public class Alias {
	private final String name;
	private Boolean inQuery = false;
	private Source source;

	public Alias(final String name) {
		this.name = name;
		this.inQuery = false;
	}

	public Alias(final String name, Source source) {
		this(name);
		this.source = source;
	}

	public String getName() {
		return this.name;
	}

	public Source getSource() {
		return this.source;
	}

	public Boolean isInQuery() {
		return this.inQuery;
	}

	public void setInQuery(final Boolean inQuery) {
		this.inQuery = inQuery;
	}
}
