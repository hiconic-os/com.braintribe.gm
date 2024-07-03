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
package com.braintribe.model.execution.persistence;

import java.util.Date;

import com.braintribe.model.generic.StandardStringIdentifiable;
import com.braintribe.model.generic.annotation.Abstract;
import com.braintribe.model.generic.annotation.meta.Description;
import com.braintribe.model.generic.annotation.meta.Name;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

@Abstract
public interface HasTimeToLive extends StandardStringIdentifiable {

	EntityType<HasTimeToLive> T = EntityTypes.T(HasTimeToLive.class);

	String successCleanupDate = "successCleanupDate";
	String errorCleanupDate = "errorCleanupDate";
	String generalCleanupDate = "generalCleanupDate";

	void setSuccessCleanupDate(Date successCleanupDate);
	@Name("Cleanup Date Success")
	@Description("The date/time when this entity should be automatically removed if it has a done state.")
	Date getSuccessCleanupDate();

	void setErrorCleanupDate(Date errorCleanupDate);
	@Name("Cleanup Date Error")
	@Description("The date/time when this entity should be automatically removed if it has an panic state.")
	Date getErrorCleanupDate();
	
	void setGeneralCleanupDate(Date generalCleanupDate);
	@Name("Cleanup Date Other")
	@Description("The date/time when this entity should be automatically removed regardless of the state.")
	Date getGeneralCleanupDate();
}
