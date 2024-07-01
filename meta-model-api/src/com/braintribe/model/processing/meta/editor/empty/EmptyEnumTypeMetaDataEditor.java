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
package com.braintribe.model.processing.meta.editor.empty;

import java.util.function.Consumer;
import java.util.function.Predicate;

import com.braintribe.model.meta.GmEnumType;
import com.braintribe.model.meta.data.MetaData;
import com.braintribe.model.meta.info.GmEnumConstantInfo;
import com.braintribe.model.meta.info.GmEnumTypeInfo;
import com.braintribe.model.processing.meta.editor.EnumTypeMetaDataEditor;

/**
 * @author peter.gazdik
 */
public class EmptyEnumTypeMetaDataEditor implements EnumTypeMetaDataEditor {

	public static final EmptyEnumTypeMetaDataEditor INSTANCE = new EmptyEnumTypeMetaDataEditor();

	private EmptyEnumTypeMetaDataEditor() {
	}

	@Override
	public GmEnumType getEnumType() {
		throw new UnsupportedOperationException("Method 'EmptyEnumTypeMetaDataEditor.getEnumType' is not supported!");
	}

	@Override
	public EnumTypeMetaDataEditor configure(Consumer<GmEnumTypeInfo> consumer) {
		return this;
	}

	@Override
	public EnumTypeMetaDataEditor configure(String constant, Consumer<GmEnumConstantInfo> consumer) {
		return this;
	}

	@Override
	public EnumTypeMetaDataEditor configure(Enum<?> constant, Consumer<GmEnumConstantInfo> consumer) {
		return this;
	}

	@Override
	public EnumTypeMetaDataEditor configure(GmEnumConstantInfo constant, Consumer<GmEnumConstantInfo> consumer) {
		return this;
	}

	@Override
	public EnumTypeMetaDataEditor addMetaData(MetaData... mds) {
		return this;
	}

	@Override
	public EnumTypeMetaDataEditor addMetaData(Iterable<? extends MetaData> mds) {
		return this;
	}

	@Override
	public EnumTypeMetaDataEditor removeMetaData(Predicate<? super MetaData> filter) {
		return this;
	}

	@Override
	public EnumTypeMetaDataEditor addConstantMetaData(MetaData... mds) {
		return this;
	}

	@Override
	public EnumTypeMetaDataEditor addConstantMetaData(Iterable<? extends MetaData> mds) {
		return this;
	}

	@Override
	public EnumTypeMetaDataEditor removeConstantMetaData(Predicate<? super MetaData> filter) {
		return this;
	}

	@Override
	public EnumTypeMetaDataEditor addConstantMetaData(String constant, MetaData... mds) {
		return this;
	}

	@Override
	public EnumTypeMetaDataEditor addConstantMetaData(String constant, Iterable<? extends MetaData> mds) {
		return this;
	}

	@Override
	public EnumTypeMetaDataEditor removeConstantMetaData(String constant, Predicate<? super MetaData> filter) {
		return this;
	}

	@Override
	public EnumTypeMetaDataEditor addConstantMetaData(Enum<?> constant, MetaData... mds) {
		return this;
	}

	@Override
	public EnumTypeMetaDataEditor addConstantMetaData(Enum<?> constant, Iterable<? extends MetaData> mds) {
		return this;
	}

	@Override
	public EnumTypeMetaDataEditor removeConstantMetaData(Enum<?> constant, Predicate<? super MetaData> filter) {
		return this;
	}

	@Override
	public EnumTypeMetaDataEditor addConstantMetaData(GmEnumConstantInfo gmConstantInfo, MetaData... mds) {
		return this;
	}

	@Override
	public EnumTypeMetaDataEditor addConstantMetaData(GmEnumConstantInfo gmConstantInfo, Iterable<? extends MetaData> mds) {
		return this;
	}

	@Override
	public EnumTypeMetaDataEditor removeConstantMetaData(GmEnumConstantInfo gmConstantInfo, Predicate<? super MetaData> filter) {
		return this;
	}

}
