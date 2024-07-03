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
package com.braintribe.model.workbench.instruction;

import java.util.Set;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.annotation.Initializer;
import com.braintribe.model.generic.annotation.meta.Mandatory;
import com.braintribe.model.generic.pr.criteria.TraversingCriterion;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.template.meta.TemplateMetaData;

public interface CreateTemplateBasedAction extends WorkbenchInstruction, HasPath {
	
	EntityType<CreateTemplateBasedAction> T = EntityTypes.T(CreateTemplateBasedAction.class);
	
	@Mandatory
	String getActionName();
	void setActionName(String actionName);

	@Mandatory
	GenericEntity getPrototype();
	void setPrototype(GenericEntity prototype);
	
	@Mandatory
	String getActionType();
	void setActionType(String actionType);
	
	Set<String> getIgnoreProperties();
	void setIgnoreProperties(Set<String> ignoreProperties);

	@Initializer("true")
	boolean getIgnoreStandardProperties();
	void setIgnoreStandardProperties(boolean ignoreStandardProperties);
	
	@Initializer("true")
	boolean getBeautifyVariableNames();
	void setBeautifyVariableNames(boolean beautifyVariableNames);
	
	TraversingCriterion getCriterion();
	void setCriterion(TraversingCriterion criterion);
	
	boolean getMultiSelectionSupport();
	void setMultiSelectionSupport(boolean multiSelectionSupport);	

	Set<TemplateMetaData> getTemplateMetaData();
	void setTemplateMetaData(Set<TemplateMetaData> templateMetaData);
	
}
