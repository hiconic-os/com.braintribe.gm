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
package com.braintribe.model.processing.mp.builder.api;

import com.braintribe.model.generic.path.ModelPathElement;
import com.braintribe.model.generic.path.api.IModelPathElement;

/**
 * This is the builder for all the different {@link ModelPathElement}
 * 
 */
public interface MpBuilder {

	//EntryPoint, ListItem, MapKey, MapValue, Property, Root, SetItem;
	
	MpBuilder root(Object root);
	
	MpBuilder listItem(int listIndex);
	//TODO check with Dirk, in order to add the missing parts of the builder
	//MpBuilder setItem(?);
	//MpBuilder mapKey(?);
	//MpBuilder mapValue(?);
	// ignore Entry point ???
	MpBuilder property(String propertyName);
	
	IModelPathElement build();

}
