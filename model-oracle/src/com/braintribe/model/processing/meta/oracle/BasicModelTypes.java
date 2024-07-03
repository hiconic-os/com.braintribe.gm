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
package com.braintribe.model.processing.meta.oracle;

import java.util.Collection;
import java.util.function.Predicate;
import java.util.stream.Stream;

import com.braintribe.model.generic.reflection.CustomType;
import com.braintribe.model.meta.GmCustomType;
import com.braintribe.model.meta.GmMetaModel;
import com.braintribe.model.meta.GmType;
import com.braintribe.model.processing.meta.oracle.flat.FlatModel;

/**
 * @author peter.gazdik
 */
public class BasicModelTypes implements ModelTypes {

	private final BasicModelOracle modelOracle;

	private Predicate<? super GmCustomType> filter;
	private TypeSource typeSource = TypeSource.all;
	private TypeType typeType = TypeType.all;

	// I know...
	static enum TypeType {
		entities(GmType::isGmEntity),
		enums(GmType::isGmEnum),
		all(null);

		public Predicate<GmType> typeFilter;

		private TypeType(Predicate<GmType> typeFilter) {
			this.typeFilter = typeFilter;
		}
	}

	public BasicModelTypes(BasicModelOracle modelOracle) {
		this.modelOracle = modelOracle;
	}

	@Override
	public ModelTypes filter(Predicate<? super GmCustomType> filter) {
		this.filter = filter;
		return this;
	}

	@Override
	public ModelTypes onlyDeclared() {
		typeSource = TypeSource.declared;
		return this;
	}

	@Override
	public ModelTypes onlyInherited() {
		typeSource = TypeSource.inherited;
		return this;
	}

	@Override
	public ModelTypes onlyEnums() {
		typeType = TypeType.enums;
		return this;
	}

	@Override
	public ModelTypes onlyEntities() {
		typeType = TypeType.entities;
		return this;
	}

	@Override
	public <T extends CustomType> Stream<T> asTypes() {
		return asGmTypes().map(gmType -> BasicModelOracle.typeReflection.<T> getType(gmType.getTypeSignature()));
	}

	@Override
	public <T extends TypeOracle> Stream<T> asTypeOracles() {
		return asGmTypes().map(gmType -> modelOracle.getTypeOracle(gmType));
	}

	@Override
	public <T extends GmCustomType> Stream<T> asGmTypes() {
		Stream<T> result = getAllOrJustDeclaredTypes();

		if (typeSource == TypeSource.inherited) {
			GmMetaModel model = modelOracle.flatModel.model;
			result = result.filter(gmType -> gmType.getDeclaringModel() != model);
		}

		if (typeType != TypeType.all) {
			result = result.filter(typeType.typeFilter);
		}

		if (filter != null) {
			result = result.filter(filter);
		}

		return result;
	}

	/**
	 * This is the basic set of types we want to return - either just declared, or all types. Also, in case we
	 */
	private <T extends GmCustomType> Stream<T> getAllOrJustDeclaredTypes() {
		FlatModel flatModel = modelOracle.flatModel;
		Collection<GmType> gmTypes = typeSource == TypeSource.declared ? flatModel.model.getTypes() : flatModel.allTypes.values();

		Stream<GmType> result = gmTypes.stream();

		if (typeType == TypeType.all) {
			// if typeType is not all, we apply a type filter anyway, for only entity of only enum, so we don't need it here
			result = result.filter(gmType -> gmType.isGmEntity() || gmType.isGmEnum());
		}

		return (Stream<T>) (Stream<?>) result; // eclipse wouldn't need the first cast, but javac yes
	}

}
