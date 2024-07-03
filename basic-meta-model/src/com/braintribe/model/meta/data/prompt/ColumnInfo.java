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
package com.braintribe.model.meta.data.prompt;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.i18n.LocalizedString;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

public interface ColumnInfo extends GenericEntity {
	
	EntityType<ColumnInfo> T = EntityTypes.T(ColumnInfo.class);
	
	void setPath(String path);
	String getPath();
	
	void setDeclaringTypeSignature(String declaringTypeSignature);
	String getDeclaringTypeSignature();
	
	void setWidth(int width);
	int getWidth();
	
	void setTitle(LocalizedString title);
	LocalizedString getTitle();
	
	void setAutoExpand(boolean autoExpand);
	boolean getAutoExpand();

}
