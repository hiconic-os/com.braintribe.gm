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
package com.braintribe.cartridge.common.processing;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

import com.braintribe.cfg.Configurable;
import com.braintribe.cfg.Required;
import com.braintribe.exception.Exceptions;
import com.braintribe.logging.Logger;
import com.braintribe.model.access.AccessService;
import com.braintribe.model.access.AccessServiceException;
import com.braintribe.model.generic.GMF;
import com.braintribe.model.generic.reflection.GenericModelTypeReflection;
import com.braintribe.model.meta.GmMetaModel;
import com.braintribe.utils.lcd.StopWatch;

/**
 * This {@link Consumer} implementation will ensure the types passed to the receive method
 * as a set of type signatures by getting a type skeleton meta model from a configured {@link AccessService}
 * which is then used to call the instant type weaving (ITW).
 * @author dirk.scheffler
 *
 */
public class RequiredTypeEnsurer implements Consumer<Set<String>> {
	private static final Logger logger = Logger.getLogger(RequiredTypeEnsurer.class);
	private AccessService accessService;
	private final GenericModelTypeReflection typeReflection = GMF.getTypeReflection();
	private boolean errorIsFatal = true;
	
	@Required @Configurable
	public void setAccessService(AccessService accessService) {
		this.accessService = accessService;
	}
	
	@Configurable
	public void setErrorIsFatal(boolean errorIsFatal) {
		this.errorIsFatal = errorIsFatal;
	}
	
	
	@Override
	public void accept(Set<String> requiredTypes) throws RuntimeException {
		// shortcut if no elements are in the collection
		if (requiredTypes.isEmpty())
			return;
		
		boolean trace = logger.isTraceEnabled();
		StopWatch stopWatch = new StopWatch();
		
		// first filter types for those not yet known
		Set<String> yetUnknownTypes = filterNotYetKnownTypes(requiredTypes);
		
		stopWatch.intermediate("Filter");
		
		if (!yetUnknownTypes.isEmpty()) {
			GmMetaModel model = null;
			try {
				// get some complete description of the yet unkown types
				if (trace) logger.trace("retrieving model for missing types: " + yetUnknownTypes);
				
				model = accessService.getMetaModelForTypes(yetUnknownTypes);
				
				stopWatch.intermediate("Get Types");
				
			} catch (AccessServiceException e) {
				if (errorIsFatal) {
					throw new RuntimeException("error while retrieving a model for unkown types from configured AccessService: " + yetUnknownTypes,e);
				} else {
					logger.warn("error while retrieving a model for unkown types from configured AccessService: " + yetUnknownTypes);
				}
			} catch (Exception e) {
				throw Exceptions.unchecked(e, "error while retrieving a model for unkown types from configured AccessService: " + yetUnknownTypes);
			}
			if (model != null) {
				try {
					// weave the missing types
					if (trace) logger.trace("weaving model classes for missing types: " + yetUnknownTypes);
					model.deploy();
					stopWatch.intermediate("Deploy");
				} catch (Exception e) {
					if (errorIsFatal) {
						throw new RuntimeException("error while weaving types for a model for unkown types: " + yetUnknownTypes,e);
					} else {
						logger.warn("error while weaving types for a model for unkown types: " + yetUnknownTypes);
					}
				}
			} else {
				if (trace) logger.trace("No model acquired for types: "+yetUnknownTypes);
			}
			if (trace) logger.trace("weaving types from model for missing types: " + yetUnknownTypes);
		}
		logger.trace(() -> "RequiredTypeEnsurer: "+stopWatch+". "+requiredTypes);
	}


	private Set<String> filterNotYetKnownTypes(Set<String> requiredTypes) {
		Set<String> filteredTypes = new HashSet<String>();
		
		for (String requiredType: requiredTypes) {
			if (typeReflection.findType(requiredType) == null) {
				filteredTypes.add(requiredType);
			}
		}
		
		return filteredTypes;
	}
}
