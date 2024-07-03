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
package com.braintribe.model.processing.traversing.engine.impl.clone.legacy;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.pr.criteria.TraversingCriterion;
import com.braintribe.model.generic.reflection.CloningContext;
import com.braintribe.model.processing.traversing.engine.GMT;

/**
 * Marks a CloningContext to be compatible with gm-traversing. This means the {@link GMT} based cloning can use the
 * {@link CloningContextBasedClonerCustomization}.
 * <p>
 * Not every {@link CloningContext} implementation can be used in GMT. ClongingContext typically has access to a stack of values and
 * {@link TraversingCriterion}s which are not accessible in the GMT context. So any Cc implementation that accesses them is not GMT compatible. The
 * only exception is to check the peek TraversingCriterion, which is set in case of a property (as that is the only way to find out which property is being cloned). 
 * 
 * Also, the {@link CloningContext#preProcessInstanceToBeCloned(GenericEntity)} must return the same instance it gets as an argument. 
 */
public interface GmtCompatibleCloningContext extends CloningContext {
	// empty
}
