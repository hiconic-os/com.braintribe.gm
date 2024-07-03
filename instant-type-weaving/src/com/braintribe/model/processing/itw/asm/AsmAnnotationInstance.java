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
package com.braintribe.model.processing.itw.asm;

import static com.braintribe.utils.lcd.CollectionTools2.asTreeMap;

import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author peter.gazdik
 */
public class AsmAnnotationInstance {

	private final AsmClass annotationClass;
	private Map<String, Object> annotationValues;

	public AsmAnnotationInstance(AsmClass annotationClass, Object... values) {
		this(annotationClass, asTreeMap(values));
	}

	public AsmAnnotationInstance(AsmClass annotationClass, Map<String, Object> annotationValues) {
		this.annotationClass = annotationClass;
		this.annotationValues = annotationValues;
	}

	public AsmClass getAnnotationClass() {
		return annotationClass;
	}

	public void addAnnotationValue(String name, Object value) {
		if (annotationValues == null) {
			annotationValues = new TreeMap<>();
		}

		annotationValues.put(name, value);
	}

	public Map<String, Object> getAnnotationValues() {
		return annotationValues != null ? annotationValues : Collections.<String, Object> emptyMap();
	}

}
