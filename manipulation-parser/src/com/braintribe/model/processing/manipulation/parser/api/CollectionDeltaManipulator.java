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
package com.braintribe.model.processing.manipulation.parser.api;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.braintribe.model.generic.reflection.ListType;
import com.braintribe.model.generic.reflection.MapType;
import com.braintribe.model.generic.reflection.SetType;

/**
 * @author peter.gazdik
 */
public interface CollectionDeltaManipulator extends GmmlConstants {

	void addToList(List<Object> list, ListType type);

	void addToSet(Set<Object> set, SetType type);

	void addToMap(Map<Object, Object> map, MapType type);

	void removeFromList(List<Object> list, ListType type);

	void removeFromSet(Set<Object> set, SetType type);

	void removeFromMap(Map<Object, Object> map, MapType type);

}
