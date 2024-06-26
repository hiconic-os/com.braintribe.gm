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
package com.braintribe.model.processing.query.parser.impl.listener;

import java.util.HashSet;
import java.util.Set;

import com.braintribe.model.processing.query.parser.api.GmqlQueryParserException;
import com.braintribe.model.processing.query.parser.impl.autogenerated.GmqlParser.FullJoinContext;
import com.braintribe.model.processing.query.parser.impl.autogenerated.GmqlParser.InnerJoinContext;
import com.braintribe.model.processing.query.parser.impl.autogenerated.GmqlParser.JoinContext;
import com.braintribe.model.processing.query.parser.impl.autogenerated.GmqlParser.JoinTypeContext;
import com.braintribe.model.processing.query.parser.impl.autogenerated.GmqlParser.LeftJoinContext;
import com.braintribe.model.processing.query.parser.impl.autogenerated.GmqlParser.RightJoinContext;
import com.braintribe.model.processing.query.parser.impl.context.JoinTypeCustomContext;
import com.braintribe.model.processing.query.parser.impl.context.basetype.DefaultCustomContext;
import com.braintribe.model.query.Join;
import com.braintribe.model.query.JoinType;
import com.braintribe.model.query.Source;

public abstract class GmqlJoinParserListener extends GmqlPropertyQueryParserListener {

	@Override
	public void exitJoin(JoinContext ctx) {
		String signature = ((DefaultCustomContext) takeValue(ctx.identifier()).cast()).getReturnValue();
		JoinType joinType = ((JoinTypeCustomContext) takeValue(ctx.joinType()).cast()).getReturnValue();

		String alias = null;
		if (ctx.alias() != null) {
			alias = ((DefaultCustomContext) takeValue(ctx.alias()).cast()).getReturnValue();
		} else {
			if (signature.contains(".")) {
				alias = getImplicitAlias(signature);
			} else {
				alias = signature;
			}
		}

		String sourceAlias = null;
		String propertyName = null;
		if (signature.contains(".")) {
			sourceAlias = signature.substring(0, signature.indexOf("."));
			propertyName = signature.substring(signature.indexOf(".") + 1);
		} else {
			setCustomParsingExcpetion(new GmqlQueryParserException("Join provided with no defined sourceAlias, propertyName " + signature));
			throw new RuntimeException();
		}

		Source source = acquireSource(sourceAlias);
		Join join = $.join(source, propertyName, joinType);
		Set<Join> joins = source.getJoins();
		if (joins == null) {
			joins = new HashSet<Join>();
			source.setJoins(joins);
		}
		joins.add(join);
		registerSource(alias, join);
		addToObjectsWithSourcesList(join);
		// to avoid default rule handling to fire
		setValue(ctx, new DefaultCustomContext(""));
	}

	@Override
	public void exitJoinType(JoinTypeContext ctx) {
		propagateChildResult(ctx);
	}

	@Override
	public void exitInnerJoin(InnerJoinContext ctx) {
		setValue(ctx, new JoinTypeCustomContext(JoinType.inner));
	}

	@Override
	public void exitFullJoin(FullJoinContext ctx) {
		setValue(ctx, new JoinTypeCustomContext(JoinType.full));
	}

	@Override
	public void exitRightJoin(RightJoinContext ctx) {
		setValue(ctx, new JoinTypeCustomContext(JoinType.right));
	}

	@Override
	public void exitLeftJoin(LeftJoinContext ctx) {
		setValue(ctx, new JoinTypeCustomContext(JoinType.left));
	}
}
