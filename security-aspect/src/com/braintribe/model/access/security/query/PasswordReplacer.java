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
package com.braintribe.model.access.security.query;

import static com.braintribe.model.access.security.query.PasswordPropertyTools.getValueToReplacePassword;
import static com.braintribe.model.access.security.query.QueryOperandTools.resolveEntityProperty;

import java.util.ArrayList;
import java.util.List;

import com.braintribe.model.access.security.query.QueryOperandTools.EntityTypeProperty;
import com.braintribe.model.generic.GMF;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.GenericModelTypeReflection;
import com.braintribe.model.processing.meta.cmd.builders.ModelMdResolver;
import com.braintribe.model.query.EntityQuery;
import com.braintribe.model.query.PropertyOperand;
import com.braintribe.model.query.PropertyQuery;
import com.braintribe.model.query.PropertyQueryResult;
import com.braintribe.model.query.Query;
import com.braintribe.model.query.QueryResult;
import com.braintribe.model.query.SelectQuery;
import com.braintribe.model.query.SelectQueryResult;
import com.braintribe.model.record.ListRecord;
import com.braintribe.utils.lcd.CollectionTools;

/**
 * 
 * Removes any passwords from the query result, if it is there... TODO explain better
 * 
 * @author peter.gazdik
 */
class PasswordReplacer {

	private static final GenericModelTypeReflection typeReflection = GMF.getTypeReflection();

	public static void replacePasswords(QueryInterceptorContext<? extends Query, ?> qiContext, QueryResult result) {
		Query query = qiContext.getQueryToRead();

		if (query instanceof EntityQuery) {
			return;
		}

		ModelMdResolver mdResolver = qiContext.getMetaData();

		if (query instanceof PropertyQuery) {
			replacePasswords((PropertyQuery) query, (PropertyQueryResult) result, mdResolver);

		} else if (query instanceof SelectQuery) {
			replacePasswords((SelectQuery) query, (SelectQueryResult) result, mdResolver, qiContext.querySources);
		}
	}

	private static void replacePasswords(PropertyQuery query, PropertyQueryResult result, ModelMdResolver mdResolver) {
		EntityType<?> et = typeReflection.getEntityType(query.getEntityReference().getTypeSignature());
		String propertyName = query.getPropertyName();

		if (PasswordPropertyTools.isPasswordProperty(et, propertyName, mdResolver)) {
			result.setPropertyValue(getValueToReplacePassword(et.getProperty(propertyName)));
		}
	}

	private static void replacePasswords(SelectQuery query, SelectQueryResult result, ModelMdResolver mdResolver,
			SourcesDescriptor querySources) {

		List<Object> selections = query.getSelections();

		if (CollectionTools.isEmpty(selections)) {
			return;
		}

		List<Integer> passwordPositions = new ArrayList<>();
		int index = 0;
		for (Object selection: selections) {
			if (isPasswordProperty(selection, querySources, mdResolver)) {
				passwordPositions.add(index);
			}
			index++;
		}

		if (passwordPositions.isEmpty()) {
			return;
		}

		if (selections.size() <= 1) {
			Object valueToReplacePassword = findValueToReplacePassword(selections.get(0), querySources);

			/* this means we are only selecting the password property, thus we want to replace all the values with new password */
			List<Object> results = result.getResults();
			for (int i = 0; i < results.size(); i++) {
				results.set(i, valueToReplacePassword);
			}
			return;
		}

		for (Integer passPosition: passwordPositions) {
			Object valueToReplacePassword = findValueToReplacePassword(selections.get(passPosition), querySources);

			for (Object resultRow: result.getResults()) {
				ListRecord lr = (ListRecord) resultRow;
				lr.getValues().set(passPosition, valueToReplacePassword);
			}
		}
	}

	private static boolean isPasswordProperty(Object operand, SourcesDescriptor querySources, ModelMdResolver mdResolver) {
		if (!(operand instanceof PropertyOperand)) {
			return false;
		}

		PropertyOperand po = (PropertyOperand) operand;
		String propertyPath = po.getPropertyName();

		if (propertyPath == null) {
			return false;
		}

		EntityTypeProperty etp = resolveEntityProperty(po, querySources);

		return PasswordPropertyTools.isPasswordProperty(etp, mdResolver);
	}

	private static Object findValueToReplacePassword(Object selection, SourcesDescriptor querySources) {
		EntityTypeProperty etp = resolveEntityProperty((PropertyOperand) selection, querySources);

		return getValueToReplacePassword(etp.entityType.getProperty(etp.propertyName));
	}

}
