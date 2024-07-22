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
package com.braintribe.model.style;

import com.braintribe.model.generic.base.EnumBase;
import com.braintribe.model.generic.reflection.EnumType;
import com.braintribe.model.generic.reflection.EnumTypes;

public enum FontStretch implements EnumBase<FontStretch> {
	normal("normal"), condensed("condensed"), ultraCondensed("ultra-condensed"), extraCondensed("extra-condensed"), 
	semiCondensed("semi-condensed"), expanded("expanded"), semiExpanded("semi-expanded"), extraExpanded("extra-expanded"), ultraExpanded("ultra-expanded");
    
    public static final EnumType<FontStretch> T = EnumTypes.T(FontStretch.class);
	
	@Override
	public EnumType<FontStretch> type() {
		return T;
	}
    
	private final String text;

    private FontStretch(final String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }	
}
