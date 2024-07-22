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
package com.braintribe.model.management;

import com.braintribe.model.generic.base.EnumBase;
import com.braintribe.model.generic.reflection.EnumType;
import com.braintribe.model.generic.reflection.EnumTypes;

public enum MetaModelValidationViolationType implements EnumBase<MetaModelValidationViolationType> {
	BASIC_METAMODEL_NULL, 
	BASIC_METAMODEL_NAME_INVALID, 
	BASIC_METAMODEL_VERSION_INVALID, 
	BASIC_DECLARINGMODEL_NULL, 
	BASIC_BASETYPE_NULL, 
	BASIC_BASETYPE_INVALID, 
	BASIC_SIMPLETYPES_NULL, 
	BASIC_SIMPLETYPES_CONTAIN_NULL,
	BASIC_SIMPLETYPE_MISSING, 
	BASIC_SIMPLETYPE_UNEXPECTED,  
	BASIC_TYPES_CONTAIN_NULL, 
	BASIC_TYPESIGNATURE_INVALID, 
	BASIC_TYPESIGNATURE_NOT_UNIQUE,
	BASIC_ENTITYTYPE_SUPERTYPES_CONTAIN_NULL, 
	BASIC_ENTITYTYPE_PROPERTIES_CONTAIN_NULL, 
	BASIC_ENTITYTYPE_PROPERTY_NAME_INVALID, 
	BASIC_ENTITYTYPE_PROPERTY_NAME_NOT_UNIQUE, 
	BASIC_ENTITYTYPE_PROPERTY_BACKREFERENCE_NULL, 
	BASIC_ENTITYTYPE_PROPERTY_BACKREFERENCE_NO_MATCH,
	BASIC_ENUMTYPES_CONTAIN_NULL, 
	BASIC_ENUMTYPE_CONSTANT_NAME_INVALID, 
	BASIC_ENUMTYPE_CONSTANT_NAME_NOT_UNIQUE, 
	BASIC_ENUMTYPE_CONSTANTS_CONTAIN_NULL, 
	BASIC_ENUMTYPE_CONSTANT_BACKREFERENCE_NULL,
	BASIC_ENUMTYPE_CONSTANT_BACKREFERENCE_NO_MATCH,
	
	TYPE_NULL_HAS_REFERENCES, 
	TYPE_NOT_DECLARED_HAS_REFERENCES, 
	TYPE_HIERARCHY_LOOP, 
	TYPE_NOT_PLAIN_TYPE_WITH_PLAIN_SUPERTYPE, 
	TYPE_PLAIN_TYPE_WITH_MORE_THAN_ONE_PLAIN_SUPERTYPE,
	
	PROPERTY_TYPE_COLLECTION_ELEMENT_TYPE_NULL,
	PROPERTY_TYPE_COLLECTION_ELEMENT_TYPE_COLLECTION,
	PROPERTY_OVERLAY_NO_SUPERPROPERTY,
	PROPERTY_CONFLICT_SUPERPROPERTY, 
	PROPERTY_INHERITED_FROM_DIFFERRENT_SUPERTYPES_CLASH_BY_TYPE,
	
	PERSISTENCE_INVALID_COUNT_OF_ID_PROPERTIES,
	PERSISTENCE_ID_PROPERTY_NOT_NULLABLE;

	public static final EnumType<MetaModelValidationViolationType> T = EnumTypes.T(MetaModelValidationViolationType.class);
	
	@Override
	public EnumType<MetaModelValidationViolationType> type() {
		return T;
	}

}
