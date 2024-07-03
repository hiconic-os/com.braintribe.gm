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
package com.braintribe.codec.marshaller.jse;

import com.braintribe.codec.marshaller.api.MarshallException;
import com.braintribe.codec.marshaller.jse.tree.JsePoolAddress;
import com.braintribe.model.generic.reflection.Property;

public class JsePropertyPreparation {
	public Property property;
	private JsePoolAddress poolAddress;
	private JsePoolAddress ownerPoolAddress;
	private JseEncodingContext encodingContext;
	
	public JsePropertyPreparation(JseEncodingContext encodingContext, JsePoolAddress ownerPoolAddress, Property property) {
		super();
		this.encodingContext = encodingContext;
		this.ownerPoolAddress = ownerPoolAddress;
		this.property = property;
	}

	public JsePoolAddress getPoolAddress() throws MarshallException {
		if (poolAddress == null) {
			poolAddress = encodingContext.aquirePropertyAddress(ownerPoolAddress, property);	
		}
		return poolAddress;
	}
}
