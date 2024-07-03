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

/**
 * 
 */
public interface MdaSynthesisContext {

	/**
	 * Creates a new {@link SingleAnnotationDescriptor} and makes sure the inner globalId is set properly. If second parameter is true, the newly
	 * created descriptor is also set as the current descriptor.
	 * <p>
	 * <h3>When should we avoid setting it as current descriptor.</h3>
	 * 
	 * In case of repeatable meta-data, the current descriptor should be a {@link RepeatedAnnotationDescriptor}, which consists of multiple
	 * {@link SingleAnnotationDescriptor}s. When we are creating new single descriptor (other than very first one), setting it as current would
	 * overwrite the previous descriptor. Instead, we create it without setting as current and then use
	 * {@link #setCurrentDescriptorMulti(SingleAnnotationDescriptor, Class)} to set it.
	 */
	SingleAnnotationDescriptor newDescriptor(Class<? extends Annotation> annotationClass);

	/**
	 * associates given {@link AnnotationDescriptor} with the type of currently processed meta-data
	 * 
	 * @see #setCurrentDescriptorMulti(SingleAnnotationDescriptor, Class)
	 */
	void setCurrentDescriptor(AnnotationDescriptor descriptor);

	/**
	 * Associates given {@link AnnotationDescriptor} with the type of currently processed meta-data. In case there already is a descriptor associated,
	 * it creates a new or extends existing {@link RepeatedAnnotationDescriptor} which corresponds to given {@link SingleAnnotationDescriptor}.
	 */
	void setCurrentDescriptorMulti(SingleAnnotationDescriptor descriptor, Class<? extends Annotation> repeatabeAnnoClass);

}
