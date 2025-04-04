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
package com.braintribe.model.cortexapi.access.collaboration;

import com.braintribe.model.generic.annotation.meta.Description;
import com.braintribe.model.generic.annotation.meta.Mandatory;
import com.braintribe.model.generic.eval.EvalContext;
import com.braintribe.model.generic.eval.Evaluator;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.service.api.ServiceRequest;

/**
 * Assuming that given stages are both manipulation stages, this request merges all the manipulations from
 * {@link #getSource() source} to {@link #getTarget() target}.
 */
@Description("Merges all the current manipulations from on stage to another.")
public interface MergeCollaborativeStage extends CollaborativePersistenceRequest {

	EntityType<MergeCollaborativeStage> T = EntityTypes.T(MergeCollaborativeStage.class);

	@Mandatory
	@Description("The name of the source stage.")
	String getSource();
	void setSource(String source);

	@Mandatory
	@Description("The name of the target stage.")
	String getTarget();
	void setTarget(String target);

	@Override
	EvalContext<Boolean> eval(Evaluator<ServiceRequest> evaluator);

	@Override
	default CollaborativePersistenceRequestType collaborativeRequestType() {
		return CollaborativePersistenceRequestType.MergeStage;
	}

}
