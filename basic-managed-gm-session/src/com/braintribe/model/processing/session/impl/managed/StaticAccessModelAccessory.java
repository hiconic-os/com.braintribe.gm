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
package com.braintribe.model.processing.session.impl.managed;

import com.braintribe.logging.Logger;
import com.braintribe.model.generic.session.exception.GmSessionRuntimeException;
import com.braintribe.model.meta.GmMetaModel;
import com.braintribe.model.processing.meta.cmd.CmdResolver;
import com.braintribe.model.processing.meta.cmd.CmdResolverImpl;
import com.braintribe.model.processing.meta.cmd.ResolutionContextBuilder;
import com.braintribe.model.processing.meta.cmd.context.aspects.AccessAspect;
import com.braintribe.model.processing.meta.oracle.BasicModelOracle;
import com.braintribe.model.processing.meta.oracle.ModelOracle;
import com.braintribe.model.processing.session.api.managed.ManagedGmSession;
import com.braintribe.model.processing.session.api.managed.ModelAccessory;

public class StaticAccessModelAccessory implements ModelAccessory {

	private static final Logger log = Logger.getLogger(StaticAccessModelAccessory.class);

	protected final ManagedGmSession modelSession;
	protected final ModelOracle modelOracle;
	protected final CmdResolver cmdResolver;

	private String toStringValue;

	public StaticAccessModelAccessory(GmMetaModel metaModel, String accessId) {
		try {

			modelSession = new BasicManagedGmSession();
			metaModel = modelSession.merge().adoptUnexposed(false).doFor(metaModel);

			modelOracle = new BasicModelOracle(metaModel);

			ResolutionContextBuilder rcb = new ResolutionContextBuilder(modelOracle);
			rcb.setSessionProvider(() -> this);
			rcb.addStaticAspect(AccessAspect.class, accessId);
			cmdResolver = new CmdResolverImpl(rcb.build());

			toStringValue = getClass().getSimpleName() + "@" + Integer.toHexString(hashCode()) + " [modelName='" + metaModel.getName() + "']";

			log.debug("Built " + this);

		} catch (Exception e) {
			throw new GmSessionRuntimeException("Error while building model accessory. Error: " + e.getMessage(), e);
		}
	}

	@Override
	public CmdResolver getCmdResolver() {
		return cmdResolver;
	}

	@Override
	public ManagedGmSession getModelSession() {
		return modelSession;
	}

	@Override
	public GmMetaModel getModel() {
		return modelOracle.getGmMetaModel();
	}

	@Override
	public ModelOracle getOracle() {
		return modelOracle;
	}

	@Override
	public String toString() {
		return toStringValue;
	}

}
