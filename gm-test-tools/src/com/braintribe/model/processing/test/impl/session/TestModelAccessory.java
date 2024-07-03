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
package com.braintribe.model.processing.test.impl.session;

import java.util.Set;
import java.util.UUID;
import java.util.function.Supplier;

import com.braintribe.model.generic.session.exception.GmSessionRuntimeException;
import com.braintribe.model.processing.meta.cmd.CmdResolver;
import com.braintribe.model.processing.meta.cmd.CmdResolverImpl;
import com.braintribe.model.processing.meta.cmd.ResolutionContextBuilder;
import com.braintribe.model.processing.meta.cmd.context.aspects.RoleAspect;
import com.braintribe.model.processing.meta.oracle.ModelOracle;
import com.braintribe.model.processing.session.api.managed.ManagedGmSession;
import com.braintribe.model.processing.session.impl.managed.AbstractModelAccessory;
import com.braintribe.model.processing.session.impl.managed.BasicManagedGmSession;

/**
 * 
 */
public class TestModelAccessory extends AbstractModelAccessory {

	private BasicManagedGmSession modelSession;
	private Supplier<Set<String>> userRolesProvider;
	private final Supplier<?> sessionProvider = () -> UUID.randomUUID().toString();

	public TestModelAccessory(ModelOracle modelOracle) {
		this.modelOracle = modelOracle;
	}

	/** @deprecated use {@link #TestModelAccessory(CmdResolver)} instead */
	@Deprecated
	public TestModelAccessory(ModelOracle modelOracle, CmdResolver cmdResolver) {
		this.modelOracle = modelOracle;
		this.cmdResolver = cmdResolver;
	}

	/** Please do not delete! */
	public TestModelAccessory(CmdResolver cmdResolver) {
		this.modelOracle = cmdResolver.getModelOracle();
		this.cmdResolver = cmdResolver;
	}

	@Override
	public ManagedGmSession getModelSession() {
		return modelSession;
	}

	public synchronized void build() {
		try {

			if (modelSession == null) {

				modelSession = new BasicManagedGmSession();
				// this just attaches the whole meta model to the session
				modelSession.merge().adoptUnexposed(true).doFor(modelOracle.getGmMetaModel());

				if (cmdResolver == null) {
					ResolutionContextBuilder contextBuilder = new ResolutionContextBuilder(modelOracle);
					contextBuilder.addDynamicAspectProvider(RoleAspect.class, userRolesProvider);
					contextBuilder.setSessionProvider(sessionProvider);

					cmdResolver = new CmdResolverImpl(contextBuilder.build());
				}

			}

		} catch (Exception e) {
			throw new GmSessionRuntimeException("Error while building model accessory. Error: " + e.getMessage(), e);
		}
	}

}
