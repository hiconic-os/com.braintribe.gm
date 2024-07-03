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
package com.braintribe.model.processing.query.expander;

import com.braintribe.model.processing.query.api.expander.QuerySignatureExpander;
import com.braintribe.model.processing.query.api.shortening.SignatureExpert;
import com.braintribe.model.query.Query;

public class QueryTypeSignatureExpanderBuilder {
	private final QuerySignatureExpander signatureExpander;

	/***************** Static Methods ******************/

	public static QueryTypeSignatureExpanderBuilder create() {
		return new QueryTypeSignatureExpanderBuilder();
	}

	public static QueryTypeSignatureExpanderBuilder create(final Query query, final SignatureExpert expandMode) {
		return new QueryTypeSignatureExpanderBuilder().query(query).mode(expandMode);
	}

	/**************** Instanced Methods ****************/

	private QueryTypeSignatureExpanderBuilder() {
		this.signatureExpander = new QueryTypeSignatureExpanderImpl();
	}

	public QueryTypeSignatureExpanderBuilder query(final Query shortenedQuery) {
		this.signatureExpander.setQuery(shortenedQuery);
		return this;
	}

	public QueryTypeSignatureExpanderBuilder mode(final SignatureExpert expandMode) {
		this.signatureExpander.setMode(expandMode);
		return this;
	}

	public Query done() {
		return this.signatureExpander.expandTypeSignature();
	}
}
