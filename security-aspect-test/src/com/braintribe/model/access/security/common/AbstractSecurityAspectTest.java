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
package com.braintribe.model.access.security.common;

import static com.braintribe.model.generic.typecondition.TypeConditions.isKind;
import static com.braintribe.utils.lcd.CollectionTools2.asMap;
import static com.braintribe.utils.lcd.CollectionTools2.asSet;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.junit.Before;

import com.braintribe.model.access.IncrementalAccess;
import com.braintribe.model.access.impl.aop.AopAccess;
import com.braintribe.model.access.security.SecurityAspect;
import com.braintribe.model.access.security.testdata.query.SecurityAspectQueryTestModel;
import com.braintribe.model.access.smood.basic.SmoodAccess;
import com.braintribe.model.acl.Acl;
import com.braintribe.model.acl.AclEntry;
import com.braintribe.model.acl.HasAcl;
import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.pr.criteria.TraversingCriterion;
import com.braintribe.model.generic.processing.pr.fluent.TC;
import com.braintribe.model.generic.session.exception.GmSessionException;
import com.braintribe.model.generic.typecondition.TypeConditions;
import com.braintribe.model.generic.typecondition.basic.TypeKind;
import com.braintribe.model.meta.GmMetaModel;
import com.braintribe.model.processing.aop.api.aspect.AccessAspect;
import com.braintribe.model.processing.meta.cmd.CmdResolver;
import com.braintribe.model.processing.meta.cmd.CmdResolverImpl;
import com.braintribe.model.processing.meta.cmd.context.aspects.RoleAspect;
import com.braintribe.model.processing.meta.oracle.BasicModelOracle;
import com.braintribe.model.processing.meta.oracle.ModelOracle;
import com.braintribe.model.processing.security.query.PostQueryExpertConfiguration;
import com.braintribe.model.processing.session.api.persistence.PersistenceGmSession;
import com.braintribe.model.processing.session.api.persistence.PersistenceGmSessionFactory;
import com.braintribe.model.processing.session.api.persistence.auth.SessionAuthorization;
import com.braintribe.model.processing.session.impl.persistence.BasicPersistenceGmSession;
import com.braintribe.model.processing.session.impl.persistence.auth.BasicSessionAuthorization;
import com.braintribe.model.processing.test.impl.session.TestModelAccessory;
import com.braintribe.provider.Holder;
import com.braintribe.testing.tools.gm.GmTestTools;

/**
 * 
 * @author peter.gazdik
 */
public abstract class AbstractSecurityAspectTest {

	public static final String MODEL_IGNORER = "modelIgnorer";

	public static final String ADMIN_ROLE = "admin";
	public static final String ADMINISTERING_ACL_ROLE = "AdminAcl";
	public static final String ADMINISTERING_HAS_ACL_ROLE = "AdminHasAcl";
	public static final String VOLUNTEER_ROLE = "volunteer";

	protected static final GmMetaModel metaModel = SecurityAspectQueryTestModel.enriched();

	protected AopAccess aopAccess;
	protected IncrementalAccess delegateAccess;
	protected SecurityAspect securityAspect;
	protected PersistenceGmSession delegateSession;
	protected PersistenceGmSession aopSession;

	protected Set<String> userRoles = Collections.emptySet();
	protected String userName = "default";

	@Before
	public void setUp() throws Exception {
		delegateAccess = delegateAccess();

		aopAccess(delegateAccess);

		refreshDelegateSession();
		refreshAopSession();

		prepareData();
		commit();
	}

	private SmoodAccess delegateAccess() {
		SmoodAccess smoodAccess = GmTestTools.newSmoodAccessMemoryOnly("testedAccess", metaModel());
		smoodAccess.setDefaultTraversingCriteria(defaultTc());
		return smoodAccess;
	}

	private void aopAccess(IncrementalAccess delegateAccess2) {
		aopAccess = new AopAccess();
		aopAccess.setDelegate(delegateAccess2);
		aopAccess.setAspects(Arrays.asList(securityAspect()));
		aopAccess.setUserSessionFactory(new TestGmSessionFactory());
		aopAccess.setSystemSessionFactory(new TestGmSessionFactory());
	}

	protected PersistenceGmSession refreshDelegateSession() {
		return delegateSession = new BasicPersistenceGmSession(delegateAccess);
	}

	protected PersistenceGmSession refreshAopSession() {
		return aopSession = new BasicPersistenceGmSession(aopAccess);
	}

	private class TestGmSessionFactory implements PersistenceGmSessionFactory {
		@Override
		public PersistenceGmSession newSession(String accessId) throws GmSessionException {
			CmdResolver cmdResolver = cmdResolver();

			BasicPersistenceGmSession session = new BasicPersistenceGmSession(delegateAccess);
			session.setModelAccessory(new TestModelAccessory(cmdResolver));
			session.setSessionAuthorization(sessionAuthorization());
			return session;
		}

		private CmdResolver cmdResolver() {
			return CmdResolverImpl.create(modelOracle()) //
					.setSessionProvider(Holder.of(new Object())) //
					.addDynamicAspectProvider(RoleAspect.class, () -> userRoles) //
					.done();
		}

		private SessionAuthorization sessionAuthorization() {
			BasicSessionAuthorization bsa = new BasicSessionAuthorization();
			bsa.setUserRoles(userRoles);
			bsa.setUserName(userName);

			return bsa;
		}
	}

	protected void prepareData() {
		// intentionally left blank, may be overridden by sub-classes
	}

	protected void commit() {
		delegateSession.commit();
	}

	protected void setUserRoles(String... roles) {
		userRoles = asSet(roles);
	}

	protected void setUserName(String name) {
		userName = name;
	}

	private AccessAspect securityAspect() {
		securityAspect = new SecurityAspect();

		// I will not test Manipulation like this (for now at least)
		securityAspect.setPostQueryExpertConfigurations(expertConfigurations());

		return securityAspect;
	}

	protected Collection<PostQueryExpertConfiguration> expertConfigurations() {
		return Collections.emptySet();
	}

	protected BasicPersistenceGmSession newAopSession() {
		BasicPersistenceGmSession aopSession = new BasicPersistenceGmSession(aopAccess);

		return aopSession;
	}

	protected ModelOracle modelOracle() {
		return new BasicModelOracle(metaModel());
	}

	protected GmMetaModel metaModel() {
		return metaModel;
	}

	protected Map<Class<? extends GenericEntity>, TraversingCriterion> defaultTc() {
		return null;
	}

	protected final Map<Class<? extends GenericEntity>, TraversingCriterion> allTopLevelPropsTcMap() {
		return asMap(GenericEntity.class, allTopLevelPropsTc);
	}

	private static final TraversingCriterion allTopLevelPropsTc = allTopLevelPropsTc();

	private static TraversingCriterion allTopLevelPropsTc() {
		// @formatter:off
		return TC.create()
					.negation()
						.disjunction()
							.property(GenericEntity.id)
							.typeCondition(isKind(TypeKind.scalarType))
							.pattern()
								  .root()
								  .listElement()
								  .entity()
								  .property()
							 .close()
							// TODO make sure the tests work even without this
							.pattern()
								.disjunction()
									.typeCondition(TypeConditions.isAssignableTo(HasAcl.T))
									.typeCondition(TypeConditions.isAssignableTo(Acl.T))
									.typeCondition(TypeConditions.isAssignableTo(AclEntry.T))
								.close()
								.property()
							.close()
						.close()
				.done();
		// @formatter:on
	}

}
