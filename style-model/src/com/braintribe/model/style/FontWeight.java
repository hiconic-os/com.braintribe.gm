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

public enum FontWeight implements EnumBase {
	normal("normal"), bold("bold"), weight100("100"), weight200("200"), weight300("300"), weight400("400"), weight500("500"),
	weight600("600"), weight700("700"), weight800("800"), weight900("900");

    public static final EnumType T = EnumTypes.T(FontWeight.class);
	
	@Override
	public EnumType type() {
		return T;
	}

	private final String text;

    private FontWeight(final String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }	
}
