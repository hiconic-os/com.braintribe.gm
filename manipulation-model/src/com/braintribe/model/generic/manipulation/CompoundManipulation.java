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
package com.braintribe.model.generic.manipulation;

import java.util.List;
import java.util.stream.Stream;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

public interface CompoundManipulation extends Manipulation {

	EntityType<CompoundManipulation> T = EntityTypes.T(CompoundManipulation.class);

	String compoundManipulationList = "compoundManipulationList";

	List<Manipulation> getCompoundManipulationList();
	void setCompoundManipulationList(List<Manipulation> compoundManipulationList);

	@Override
	default boolean isRemote() {
		List<Manipulation> list = getCompoundManipulationList();
		return list != null && !list.isEmpty() && list.get(0).isRemote();
	}

	@Override
	@SuppressWarnings("unusable-by-js")
	default Stream<AtomicManipulation> stream() {
		List<Manipulation> list = getCompoundManipulationList();
		return list == null ? Stream.empty() : list.stream().flatMap(Manipulation::stream);
	}

	@Override
	@SuppressWarnings("unusable-by-js")
	default Stream<GenericEntity> touchedEntities() {
		List<Manipulation> list = getCompoundManipulationList();
		return list == null ? Stream.empty() : list.stream().flatMap(Manipulation::touchedEntities);
	}

	@Override
	default ManipulationType manipulationType() {
		return ManipulationType.COMPOUND;
	}

	static CompoundManipulation create(List<? extends Manipulation> list) {
		CompoundManipulation result = CompoundManipulation.T.create();
		result.setCompoundManipulationList((List<Manipulation>) (List<?>) list);

		return result;
	}

}
