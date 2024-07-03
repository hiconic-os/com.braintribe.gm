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
package com.braintribe.model.generic.annotation.meta.handlers;

import com.braintribe.model.generic.annotation.meta.api.synthesis.MdaSynthesisContext;
import com.braintribe.model.generic.annotation.meta.api.synthesis.SingleAnnotationDescriptor;
import com.braintribe.model.generic.annotation.meta.base.BasicMdaHandler;

/**
 * Deprecated annotation has a special handler because we do not want to write the globalId as a property of the annotation.  
 * 
 * @author peter.gazdik
 */
public class DeprecatedMdaHandler extends BasicMdaHandler<Deprecated, com.braintribe.model.meta.data.prompt.Deprecated> {

	public DeprecatedMdaHandler() {
		super(Deprecated.class, com.braintribe.model.meta.data.prompt.Deprecated.class, deprecated -> null);
	}

	@Override
	public void buildAnnotation(MdaSynthesisContext context, com.braintribe.model.meta.data.prompt.Deprecated metaData) {
		// We cannot call context.newDescriptor as that would automatically copy globalId to the descriptor
		SingleAnnotationDescriptor result = new SingleAnnotationDescriptor(Deprecated.class);
		context.setCurrentDescriptor(result);
	}

}
