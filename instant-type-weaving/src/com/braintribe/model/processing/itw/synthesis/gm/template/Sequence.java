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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.braintribe.asm.MethodVisitor;

public class Sequence implements TemplateNode {
	private final List<TemplateNode> nodes = new ArrayList<TemplateNode>();

	public void add(TemplateNode templateNode) {
		nodes.add(templateNode);
	}

	@Override
	public void merge(MethodVisitor mv, VariableResolver variableResolver) {
		for (TemplateNode node: nodes)
			node.merge(mv, variableResolver);
	}

	@Override
	public void collectVariables(Set<Variable> variables) {
		for (TemplateNode node: nodes)
			node.collectVariables(variables);
	}

}
