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
package com.braintribe.model.processing.vde.clone.async;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.collection.LinearCollectionBase;
import com.braintribe.model.generic.collection.ListBase;
import com.braintribe.model.generic.collection.MapBase;
import com.braintribe.model.generic.collection.SetBase;
import com.braintribe.model.generic.reflection.GenericModelType;
import com.braintribe.model.generic.reflection.LinearCollectionType;
import com.braintribe.model.generic.reflection.ListType;
import com.braintribe.model.generic.reflection.MapType;
import com.braintribe.model.generic.reflection.SetType;
import com.braintribe.processing.async.api.AsyncCallback;

public interface AsyncCloning {
	<T> void cloneCollection(Collection<T> collection, AsyncCallback<? super LinearCollectionBase<T>> callback);

	<T> void cloneCollection(Collection<T> collection, LinearCollectionType collectionType, AsyncCallback<? super LinearCollectionBase<T>> callback);

	<T> void cloneCollection(LinearCollectionBase<T> collection, AsyncCallback<? super LinearCollectionBase<T>> callback);

	<T> void cloneList(List<T> list, AsyncCallback<? super ListBase<T>> callback);

	<T> void cloneList(List<T> list, ListType listType, AsyncCallback<? super ListBase<T>> callback);

	<T> void cloneList(ListBase<T> list, AsyncCallback<? super ListBase<T>> callback);

	<T> void cloneSet(Set<T> set, AsyncCallback<? super SetBase<T>> callback);

	<T> void cloneSet(Set<T> set, SetType setType, AsyncCallback<? super SetBase<T>> callback);

	<T> void cloneSet(SetBase<T> set, AsyncCallback<? super SetBase<T>> callback);

	<K, V> void cloneMap(Map<K, V> map, AsyncCallback<? super MapBase<K, V>> callback);

	<K, V> void cloneMap(Map<K, V> map, MapType mapType, AsyncCallback<? super MapBase<K, V>> callback);

	<K, V> void cloneMap(MapBase<K, V> map, AsyncCallback<? super MapBase<K, V>> callback);

	<T extends GenericEntity> void cloneEntity(T entity, AsyncCallback<? super T> callback);

	<T> void cloneValue(Object value, AsyncCallback<? super T> callback);

	<T> void cloneValue(Object value, GenericModelType type, AsyncCallback<? super T> callback);

}