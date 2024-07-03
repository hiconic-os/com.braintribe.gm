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

import java.util.function.Function;
import java.util.function.Predicate;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.session.GmSession;
import com.braintribe.model.meta.GmMetaModel;
import com.braintribe.model.meta.GmModelElement;
import com.braintribe.model.processing.meta.editor.leaf.LeafModel;

/**
 * @author peter.gazdik
 */
public class BasicMdEditorBuilder implements MetaDataEditorBuilder {

	/* package */ final GmMetaModel model;
	/* package */ Function<EntityType<?>, GenericEntity> entityFactory = EntityType::create;
	/* package */ Predicate<? super GmModelElement> wasEntityUninstantiated = entity -> false;
	/* package */ GlobalIdFactory globalIdFactory = null;
	/* package */ boolean appendToDeclaration;
	/* package */ boolean typeLenient;

	public BasicMdEditorBuilder(GmMetaModel model) {
		this(model, false);
	}

	public BasicMdEditorBuilder(GmMetaModel model, boolean useModelSessionIfAttached) {
		this.model = model;

		if (useModelSessionIfAttached && model.session() != null)
			withSession(model.session());
	}

	/** {@inheritDoc} */
	@Override
	public BasicMdEditorBuilder withEntityFactory(Function<EntityType<?>, GenericEntity> entityFactory) {
		this.entityFactory = entityFactory;
		return this;
	}

	/** {@inheritDoc} */
	@Override
	public BasicMdEditorBuilder withWasEntityUninstantiatedTest(Predicate<? super GmModelElement> wasEntityUninstantiated) {
		this.wasEntityUninstantiated = wasEntityUninstantiated;
		return this;
	}

	/** {@inheritDoc} */
	@Override
	public BasicMdEditorBuilder withSession(GmSession session) {
		return withEntityFactory(session::create).withWasEntityUninstantiatedTest(entity -> entity.session() != session);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see LeafModel#deriveGlobalId
	 */
	@Override
	public BasicMdEditorBuilder withGlobalIdFactory(GlobalIdFactory globalIdFactory) {
		this.globalIdFactory = globalIdFactory;
		return this;
	}

	/** {@inheritDoc} */
	@Override
	public BasicMdEditorBuilder setAppendToDeclaration(boolean appendToDeclaration) {
		this.appendToDeclaration = appendToDeclaration;
		return this;
	}

	/** {@inheritDoc} */
	@Override
	public BasicMdEditorBuilder typeLenient(boolean typeLenient) {
		this.typeLenient = typeLenient;
		return this;
	}

	@Override
	public BasicModelMetaDataEditor done() {
		return new BasicModelMetaDataEditor(this);
	}

}
