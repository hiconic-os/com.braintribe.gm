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
package com.braintribe.model.processing.itw.synthesis.gm.template;

import java.util.List;
import java.util.Set;

import com.braintribe.asm.MethodVisitor;
import com.braintribe.model.weaving.ProtoGmProperty;

public class Variable implements TemplateNode {

	private final String variableName;
	private final List<ProtoGmProperty> nonOverlayProperties;

	public Variable(String variableName, List<ProtoGmProperty> nonOverlayProperties) {
		this.variableName = variableName;
		this.nonOverlayProperties = nonOverlayProperties;
	}

	@Override
	public void merge(MethodVisitor mv, VariableResolver variableResolver) {
		variableResolver.mergeVariable(variableName, nonOverlayProperties, mv);
	}

	@Override
	public void collectVariables(Set<Variable> variables) {
		variables.add(this);
	}

}
