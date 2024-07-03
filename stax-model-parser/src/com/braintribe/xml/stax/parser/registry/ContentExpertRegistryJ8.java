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
package com.braintribe.xml.stax.parser.registry;

import java.util.function.Supplier;

import com.braintribe.xml.stax.parser.experts.ContentExpert;

/**
 * a {@link ContentExpertRegistryJ8} is used to link tags in the xml to experts that can handle the data contained. 
 * @author Pit
 *
 */
public interface ContentExpertRegistryJ8{
	/**
	 * add a tag factory that is able to deliver a new instance of an expert for the tag
	 * @param tag - the key 
	 * @param factory - the factory
	 */
	void addExpertFactory( String tag, Supplier<ContentExpert> supplier);
	ContentExpert getExpertForTag( String tag);
}
