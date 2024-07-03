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
package com.braintribe.model.generic.reflection;

import com.braintribe.model.generic.annotation.GmSystemInterface;
import com.braintribe.model.generic.session.GmSession;
import com.braintribe.model.generic.value.ValueDescriptor;

/**
 * @author peter.gazdik
 */
@GmSystemInterface
public abstract class GmtsPlainEntityStub extends GmtsEntityStub {

	@Override
	public final boolean isEnhanced() {
		return false;
	}

	@Override
	public final void write(Property p, Object value) {
		p.setDirectUnsafe(this, value);
	}

	@Override
	public final Object read(Property p) {
		return p.getDirectUnsafe(this);
	}

	@Override
	public void writeVd(Property p, ValueDescriptor value) {
		p.setVdDirect(this, value);
	}

	@Override
	public ValueDescriptor readVd(Property p) {
		return p.getVdDirect(this);
	}
	
	@Override
	public final GmSession session() {
		return null;
	}

	@Override
	public final void attach(GmSession session) {
		throw new UnsupportedOperationException("Cannot attach session to a plain entity.");
	}

	@Override
	public final GmSession detach() {
		throw new UnsupportedOperationException("Cannot detach session from a plain entity.");
	}

}
