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
package com.braintribe.model.processing.query.autocompletion.impl;

import com.braintribe.model.processing.query.autocompletion.api.QueryAutoCompletionResult;
import com.braintribe.model.processing.query.autocompletion.api.QueryLexerResult;
import com.braintribe.model.processing.query.autocompletion.impl.lexer.QueryLexer;
import com.braintribe.model.processing.query.autocompletion.impl.lexer.container.AliasInput;
import com.braintribe.model.processing.query.autocompletion.impl.lexer.container.AliasType;
import com.braintribe.model.processing.query.autocompletion.impl.lexer.container.AliasType.CheckType;
import com.braintribe.model.processing.query.autocompletion.impl.lexer.container.QueryLexerToken;

/**
 * Helper to create default results.
 *
 */
public abstract class ContainerFactory {

	public static QueryAutoCompletionResult createQueryAutoCompletionResult() {
		final QueryAutoCompletionResult queryAutoCompletionResult = QueryAutoCompletionResult.T.create();

		// Set default values
		queryAutoCompletionResult.setPossibleHints(null);

		// Done
		return queryAutoCompletionResult;
	}

	public static QueryLexerResult createQueryLexerResult() {
		final QueryLexerResult queryLexerResult = QueryLexerResult.T.create();

		// Set default values (component, filter & alias)
		queryLexerResult.setQueryToken(QueryLexerToken.Unknown);
		queryLexerResult.setFilterString(QueryLexer.emptyString);
		queryLexerResult.setAliasType(null);

		// Set default values (closable)
		queryLexerResult.setInEscapeKeyword(false);
		queryLexerResult.setInString(false);
		queryLexerResult.setBracketScope(0);

		// Done
		return queryLexerResult;
	}

	public static AliasInput createAliasInput() {
		final AliasInput aliasInput = AliasInput.T.create();

		// Set default values
		aliasInput.setAliasMap(null);
		aliasInput.setKeywords(null);

		// Done
		return aliasInput;
	}

	public static AliasType createAliasType() {
		final AliasType aliasType = AliasType.T.create();

		// Set default values
		aliasType.setEntityType(null);
		aliasType.setHasProperties(false);
		aliasType.setPropertyFound(false);
		aliasType.setCheckType(CheckType.None);
		aliasType.setLastAliasPart(QueryLexer.emptyString);

		// Done
		return aliasType;
	}
}
