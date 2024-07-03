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
package com.braintribe.model.processing.query;

import java.util.Map;

import com.braintribe.model.generic.reflection.GenericModelType;
import com.braintribe.model.generic.reflection.StandardCloningContext;
import com.braintribe.model.generic.value.Variable;

public class VariableReplacingEvolutionContext extends StandardCloningContext{
	
	private boolean treatUnknownVariablesAsNull = false;
	private Map<String, Object> values;
	
	public void setValues(Map<String, Object> values) {
		this.values = values;
	}
	
	public void setTreatUnknownVariablesAsNull(boolean treatUnknownVariablesAsNull) {
		this.treatUnknownVariablesAsNull = treatUnknownVariablesAsNull;
	}
	
	@Override
	public Object postProcessCloneValue(GenericModelType propertyType,Object clonedValue) {			
		if(clonedValue instanceof Variable){
			Variable variable = (Variable) clonedValue;
			Object value = values.get(variable.getName());
			if (value == null && !treatUnknownVariablesAsNull) {
				throw new TemplateEvaluationException("no value found for variable name '" + variable.getName() + "'");
			}
			return value;
		}
		return clonedValue;
	}
}
