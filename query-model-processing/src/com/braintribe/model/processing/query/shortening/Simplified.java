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
package com.braintribe.model.processing.query.shortening;

import com.braintribe.model.processing.query.api.shortening.QueryShorteningRuntimeException;
import com.braintribe.model.processing.query.api.shortening.SignatureExpert;

public class Simplified implements SignatureExpert {
	@Override
	public String shorten(final String typeSignature) {
		if (typeSignature != null) {
			// No package in typeSignature found!
			final int packageSplit = typeSignature.lastIndexOf(".");
			if (packageSplit < 0) {
				return typeSignature;
			}

			// Remove package from typeSignature
			return typeSignature.substring(packageSplit + 1);
		}

		return null;
	}

	@Override
	public String expand(final String shortenedTypeSignature) {
		throw new QueryShorteningRuntimeException("Method is not implemented.");
	}
}
