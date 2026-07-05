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

import static com.braintribe.utils.lcd.CollectionTools2.first;
import static com.braintribe.utils.lcd.CollectionTools2.isEmpty;
import static com.braintribe.utils.lcd.CollectionTools2.size;

import java.util.Collections;
import java.util.Set;

import com.braintribe.model.access.security.cloning.IlsExpertsRegistry;
import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.path.PropertyRelatedModelPathElement;
import com.braintribe.model.generic.pr.criteria.CriterionType;
import com.braintribe.model.generic.pr.criteria.TraversingCriterion;
import com.braintribe.model.generic.pr.criteria.matching.Matcher;
import com.braintribe.model.generic.pr.criteria.matching.StandardMatcher;
import com.braintribe.model.generic.processing.pr.fluent.TC;
import com.braintribe.model.generic.reflection.TraversingContext;
import com.braintribe.model.processing.meta.cmd.builders.ModelMdResolver;
import com.braintribe.model.processing.security.query.context.EntityExpertContext;
import com.braintribe.model.processing.security.query.context.PropertyExpertContext;
import com.braintribe.model.processing.session.api.persistence.PersistenceGmSession;
import com.braintribe.model.processing.session.api.persistence.auth.SessionAuthorization;
import com.braintribe.model.query.Query;
import com.braintribe.model.query.SelectQuery;
import com.braintribe.model.security.acl.AclTools;

public class PostQueryExpertContextImpl implements EntityExpertContext, PropertyExpertContext, Matcher {

	private final PersistenceGmSession session;
	private final IlsExpertsRegistry ilsMatcherRegistry;
	private final Matcher tcMatcher;

	private GenericEntity entity;
	private PropertyRelatedModelPathElement propertyRelatedModelPathElement;
	private ModelMdResolver modelMdResolver;
	private final boolean needsHasAclChecks;

	public PostQueryExpertContextImpl(QueryInterceptorContext<?, ?> qiContext, IlsExpertsRegistry ilsMatcherRegistry) {
		this.ilsMatcherRegistry = ilsMatcherRegistry;
		this.session = qiContext.getSession();
		this.tcMatcher = tcMatcher(qiContext);
		this.needsHasAclChecks = AclTools.supportsAcl(session) && !AclTools.isHasAclAdministrable(session);
	}

	private static Matcher tcMatcher(QueryInterceptorContext<?, ?> qiContext) {
		TraversingCriterion tc = qiContext.getOriginalQuery().getTraversingCriterion();
		if (tc == null || TC.containsPlaceholder(tc))
			return null;

		tc = extendIfSelectQueryResult(tc, qiContext);

		StandardMatcher result = new StandardMatcher();
		result.setCriterion(tc);
		return result;
	}

	private static TraversingCriterion extendIfSelectQueryResult(TraversingCriterion tc, QueryInterceptorContext<?, ?> qiContext) {
		Query q = qiContext.getOriginalQuery();
		if (!(q instanceof SelectQuery))
			return tc;

		SelectQuery sq = (SelectQuery) q;
		if (hasOneDimensionalResult(sq))
			return tc;

		// @formatter:off
		return TC.create().conjunction() //
				.criterion(tc)
				.negation()
					.pattern()
						.root()
						.listElement()
						.entity() // ListRecord
						.property()
					.close()
			.close().done();
		// @formatter:on
	}

	private static boolean hasOneDimensionalResult(SelectQuery sq) {
		switch (size(sq.getSelections())) {
			case 0:
				return size(sq.getFroms()) == 1 && //
						isEmpty(first(sq.getFroms()).getJoins());
			case 1:
				return true;
			default:
				return false;
		}
	}

	@Override
	public boolean matches(TraversingContext tc) {
		// If we are at an absent property, match would mean null is set as property value
		// Null is also handled this way -> it makes no sense matching anyway and null check is simpler
		if (isNullOrAbsentProperty(tc))
			return false;

		return (tcMatcher != null && tcMatcher.matches(tc)) //
				|| ilsMatcherRegistry.matches(this, tc);
	}

	private boolean isNullOrAbsentProperty(TraversingContext tc) {
		if (tc.getCurrentCriterionType() != CriterionType.PROPERTY)
			return false;

		return tc.getObjectStack().peek() == null;
	}

	@Override
	public PersistenceGmSession getSession() {
		return session;
	}

	@Override
	public ModelMdResolver getMetaData() {
		if (modelMdResolver == null)
			modelMdResolver = session.getModelAccessory().getMetaData();

		return modelMdResolver;
	}

	public boolean needsHasAclChecks() {
		return needsHasAclChecks;
	}

	@Override
	public Set<String> getRoles() {
		SessionAuthorization sessionAuthorization = session.getSessionAuthorization();

		return sessionAuthorization == null ? Collections.<String> emptySet() : sessionAuthorization.getUserRoles();
	}

	@Override
	public GenericEntity getEntity() {
		return entity;
	}

	public void setEntity(GenericEntity entity) {
		this.entity = entity;
	}

	@Override
	public PropertyRelatedModelPathElement getPropertyRelatedModelPathElement() {
		return propertyRelatedModelPathElement;
	}

	public void setPropertyRelatedModelPathElement(PropertyRelatedModelPathElement propertyRelatedModelPathElement) {
		this.propertyRelatedModelPathElement = propertyRelatedModelPathElement;
	}

}
