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
package com.braintribe.model.processing.core.commons;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.EntityType;

public class IdentificationBuilders {

	@SuppressWarnings("rawtypes")
	public static IdentificationBuilder<?> id(Object id) {
		return new IdentificationBuilderImpl(id);
	}
	
	public static IdentificationBuilderFromGenericEntity<?> fromInstance(GenericEntity instance) {
		return new IdentificationBuilderFromGenericEntityImpl(instance);
	}
	
	
	private static class IdentificationBuilderImpl<I extends IdentificationBuilderImpl<I>> implements IdentificationBuilder<I> {
		protected String outerNamespace = "";
		protected String namespace = "";
		protected Object id;
		protected I self;
		
		public IdentificationBuilderImpl(Object id) {
			this.id = id;
			this.self = (I)this;
		}
		
		public IdentificationBuilderImpl() {
			this.self = (I)this;
		}

		@Override
		public I outerNamespace(String outerNamespace) {
			this.outerNamespace = outerNamespace;
			return self;
		}

		@Override
		public I namespace(String namespace) {
			this.namespace = namespace;
			return self;
		}

		@Override
		public I namespace(Class<? extends GenericEntity> typeSignature) {
			this.namespace = typeSignature.getName();
			return self;
		}

		@Override
		public I namespace(EntityType<?> typeSignature) {
			this.namespace = typeSignature.getTypeSignature();
			return self;
		}
		
		@Override
		public String build() {
			return escape(outerNamespace) + ":" + escape(namespace) + ":" + escape(id.toString());
		}
		
		
	}
	
	private static class IdentificationBuilderFromGenericEntityImpl 
		extends IdentificationBuilderImpl<IdentificationBuilderFromGenericEntityImpl> 
		implements IdentificationBuilderFromGenericEntity<IdentificationBuilderFromGenericEntityImpl> {
		
		private final String typeSignature;
		
		private IdentificationBuilderFromGenericEntityImpl(GenericEntity entity) {
			this.typeSignature = entity.entityType().getTypeSignature(); 
			this.id = entity.getId();
		}

		@Override
		public IdentificationBuilderFromGenericEntityImpl namespaceFromType() {
			namespace = typeSignature;
			return self;
		}
	}

	protected static String escape(String s) {
		return s.replace("&", "&amp;").replace(":", "&#58;");
	}
	
	protected static String unescape(String s) {
		return s.replace("&#58;", ":").replace("&amp;", "&");
	}

}
