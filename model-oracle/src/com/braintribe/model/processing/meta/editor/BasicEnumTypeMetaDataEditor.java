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
package com.braintribe.model.processing.meta.editor;

import static com.braintribe.model.processing.meta.editor.BasicModelMetaDataEditor.add;
import static com.braintribe.model.processing.meta.editor.BasicModelMetaDataEditor.remove;

import java.util.function.Consumer;
import java.util.function.Predicate;

import com.braintribe.model.generic.reflection.GenericModelException;
import com.braintribe.model.meta.GmEnumType;
import com.braintribe.model.meta.data.MetaData;
import com.braintribe.model.meta.info.GmEnumConstantInfo;
import com.braintribe.model.meta.info.GmEnumTypeInfo;
import com.braintribe.model.processing.meta.oracle.flat.FlatEnumConstant;
import com.braintribe.model.processing.meta.oracle.flat.FlatEnumType;

/**
 * @author peter.gazdik
 */
public class BasicEnumTypeMetaDataEditor implements EnumTypeMetaDataEditor {

	private final BasicModelMetaDataEditor modelMdEditor;
	private final FlatEnumType flatEnumType;

	public BasicEnumTypeMetaDataEditor(BasicModelMetaDataEditor modelMdEditor, FlatEnumType flatEnumType) {
		this.modelMdEditor = modelMdEditor;
		this.flatEnumType = flatEnumType;
	}

	@Override
	public GmEnumType getEnumType() {
		return flatEnumType.type;
	}

	@Override
	public EnumTypeMetaDataEditor configure(Consumer<GmEnumTypeInfo> consumer) {
		consumer.accept(acquireGmEnumTypeInfo());
		return this;
	}

	@Override
	public EnumTypeMetaDataEditor configure(String constantName, Consumer<GmEnumConstantInfo> consumer) {
		consumer.accept(acquireGmEnumConstantInfo(constantName));
		return this;
	}

	@Override
	public EnumTypeMetaDataEditor configure(Enum<?> constant, Consumer<GmEnumConstantInfo> consumer) {
		configure(constant.name(), consumer);
		return this;
	}
	@Override
	public EnumTypeMetaDataEditor configure(GmEnumConstantInfo constant, Consumer<GmEnumConstantInfo> consumer) {
		configure(constant.relatedConstant().getName(), consumer);
		return this;
	}

	@Override
	public EnumTypeMetaDataEditor addMetaData(MetaData... mds) {
		add(acquireGmEnumTypeInfo().getMetaData(), mds);
		return this;
	}

	@Override
	public EnumTypeMetaDataEditor addMetaData(Iterable<? extends MetaData> mds) {
		add(acquireGmEnumTypeInfo().getMetaData(), mds);
		return this;
	}

	@Override
	public EnumTypeMetaDataEditor removeMetaData(Predicate<? super MetaData> filter) {
		remove(acquireGmEnumTypeInfo().getMetaData(), filter);
		return this;
	}

	@Override
	public EnumTypeMetaDataEditor addConstantMetaData(MetaData... mds) {
		add(acquireGmEnumTypeInfo().getEnumConstantMetaData(), mds);
		return this;
	}

	@Override
	public EnumTypeMetaDataEditor addConstantMetaData(Iterable<? extends MetaData> mds) {
		add(acquireGmEnumTypeInfo().getEnumConstantMetaData(), mds);
		return this;
	}

	@Override
	public EnumTypeMetaDataEditor removeConstantMetaData(Predicate<? super MetaData> filter) {
		remove(acquireGmEnumTypeInfo().getEnumConstantMetaData(), filter);
		return this;
	}

	@Override
	public EnumTypeMetaDataEditor addConstantMetaData(String constant, MetaData... mds) {
		add(acquireGmEnumConstantInfo(constant).getMetaData(), mds);
		return this;
	}

	@Override
	public EnumTypeMetaDataEditor addConstantMetaData(String constant, Iterable<? extends MetaData> mds) {
		add(acquireGmEnumConstantInfo(constant).getMetaData(), mds);
		return this;
	}

	@Override
	public EnumTypeMetaDataEditor removeConstantMetaData(String constant, Predicate<? super MetaData> filter) {
		remove(acquireGmEnumConstantInfo(constant).getMetaData(), filter);
		return this;
	}

	@Override
	public EnumTypeMetaDataEditor addConstantMetaData(Enum<?> constant, MetaData... mds) {
		addConstantMetaData(constant.name(), mds);
		return this;
	}

	@Override
	public EnumTypeMetaDataEditor addConstantMetaData(Enum<?> constant, Iterable<? extends MetaData> mds) {
		addConstantMetaData(constant.name(), mds);
		return this;
	}

	@Override
	public EnumTypeMetaDataEditor removeConstantMetaData(Enum<?> constant, Predicate<? super MetaData> filter) {
		removeConstantMetaData(constant.name(), filter);
		return this;
	}

	@Override
	public EnumTypeMetaDataEditor addConstantMetaData(GmEnumConstantInfo gmConstantInfo, MetaData... mds) {
		addConstantMetaData(gmConstantInfo.relatedConstant().getName(), mds);
		return this;
	}

	@Override
	public EnumTypeMetaDataEditor addConstantMetaData(GmEnumConstantInfo gmConstantInfo, Iterable<? extends MetaData> mds) {
		addConstantMetaData(gmConstantInfo.relatedConstant().getName(), mds);
		return this;
	}

	@Override
	public EnumTypeMetaDataEditor removeConstantMetaData(GmEnumConstantInfo gmConstantInfo, Predicate<? super MetaData> filter) {
		removeConstantMetaData(gmConstantInfo.relatedConstant().getName(), filter);
		return this;
	}

	protected GmEnumTypeInfo acquireGmEnumTypeInfo() {
		if (modelMdEditor.appendToDeclaration) {
			return flatEnumType.type;
		} else {
			return modelMdEditor.leafModel.acquireGmEnumTypeInfo(flatEnumType.type);
		}
	}

	protected GmEnumConstantInfo acquireGmEnumConstantInfo(String constantName) {
		FlatEnumConstant flatEnumConstant = flatEnumType.acquireFlatEnumConstants().get(constantName);
		if (flatEnumConstant == null) {
			throw new GenericModelException("Constant '" + constantName + "' not found for enum type: " + flatEnumType.type.getTypeSignature());
		}

		if (modelMdEditor.appendToDeclaration) {
			return flatEnumConstant.gmEnumConstant;
		} else {
			return modelMdEditor.leafModel.acquireGmConstantInfo(flatEnumType.type, flatEnumConstant.gmEnumConstant);
		}
	}

}
