// ============================================================================
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
// ============================================================================
// Copyright BRAINTRIBE TECHNOLOGY GMBH, Austria, 2002-2022
// 
// This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
// 
// This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
// 
// You should have received a copy of the GNU Lesser General Public License along with this library; See http://www.gnu.org/licenses/.
// ============================================================================
package com.braintribe.model.processing.meta.oracle;

import static com.braintribe.utils.lcd.CollectionTools2.nullSafe;

import java.util.function.Predicate;
import java.util.stream.Stream;

import com.braintribe.model.generic.reflection.Model;
import com.braintribe.model.meta.GmMetaModel;
import com.braintribe.model.processing.meta.oracle.flat.FlatModel;

/**
 * @author peter.gazdik
 */
public class BasicModelDependencies implements ModelDependencies {

	private final BasicModelOracle modelOracle;

	private boolean transitive;
	private boolean includeSelf;

	public BasicModelDependencies(BasicModelOracle modelOracle) {
		this.modelOracle = modelOracle;
	}

	@Override
	@Deprecated
	public ModelDependencies transitive(boolean includeSelf) {
		this.transitive = true;
		this.includeSelf = includeSelf;
		return this;
	}

	@Override
	public ModelDependencies transitive() {
		this.transitive = true;
		return this;
	}

	@Override
	public ModelDependencies includeSelf() {
		this.includeSelf = true;
		return this;
	}

	@Override
	public Stream<Model> asModels() {
		return asGmMetaModels().map(gmMetaModel -> BasicModelOracle.typeReflection.getModel(gmMetaModel.getName()));
	}

	@Override
	public Stream<ModelOracle> asModelOracles() {
		return asGmMetaModels().map(BasicModelOracle::new);
	}

	@Override
	public Stream<GmMetaModel> asGmMetaModels() {
		FlatModel flatModel = modelOracle.flatModel;

		Stream<GmMetaModel> result = transitive ? flatModel.allModels.stream() : nullSafe(flatModel.model.getDependencies()).stream();

		if (transitive && !includeSelf) {
			result = result.filter(new StartSkippingPredicate<>(1));
		}

		return result;
	}

	private static class StartSkippingPredicate<T> implements Predicate<T> {
		private int count;

		public StartSkippingPredicate(int count) {
			this.count = count;
		}

		@Override
		public boolean test(T t) {
			if (count > 0) {
				count--;
				return false;
			}

			return true;
		}
	}

}
