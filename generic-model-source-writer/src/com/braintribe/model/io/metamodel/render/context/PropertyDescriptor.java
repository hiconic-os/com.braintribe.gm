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
package com.braintribe.model.io.metamodel.render.context;

import java.util.List;

import com.braintribe.model.meta.GmProperty;
import com.braintribe.utils.lcd.StringTools;

/**
 * 
 */
public class PropertyDescriptor implements Comparable<PropertyDescriptor> {
	public final String name;
	public final JavaType type;

	public final boolean isInherited;
	public final List<String> annotations;

	public PropertyDescriptor(GmProperty gmProperty, JavaType type, boolean isInherited, List<String> annotations) {
		this.name = gmProperty.getName();
		this.type = type;
		this.isInherited = isInherited;
		this.annotations = annotations;
	}

	public String getNameStartingWithUpperCase() {
		return StringTools.capitalize(name);
	}

	@Override
	public int compareTo(PropertyDescriptor other) {
		return name.compareTo(other.name);
	}

}
