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
package com.braintribe.model.generic.annotation.meta.api.synthesis;

import java.lang.annotation.Annotation;
import java.util.function.Consumer;

/**
 * @author peter.gazdik
 */
public abstract class AnnotationDescriptor implements Comparable<AnnotationDescriptor> {

	protected final Class<? extends Annotation> annotationClass;

	protected AnnotationDescriptor(Class<? extends Annotation> annotationClass) {
		this.annotationClass = annotationClass;
	}

	public Class<? extends Annotation> getAnnotationClass() {
		return annotationClass;
	}

	@Override
	public int compareTo(AnnotationDescriptor o) {
		return this.annotationClass.getName().compareTo(o.annotationClass.getName());
	}

	public abstract void withSourceCode(Consumer<String> sourceCodeConsumer);

}
