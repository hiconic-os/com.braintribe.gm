// ============================================================================
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
// ============================================================================
// Copyright BRAINTRIBE TECHNOLOGY GMBH, Austria, 2002-2022
// 
// This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
// 
// This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
// 
// You should have received a copy of the GNU Lesser General Public License along with this library; See http://www.gnu.org/licenses/.
// ============================================================================
package com.braintribe.codec.dom.genericmodel;

/**
 * Specifies how missing/unknown types, properties, etc. are handled during decoding process.
 * 
 * @author michael.lafite
 * @deprecated use {@link com.braintribe.codec.marshaller.api.DecodingLenience} instead
 */
@Deprecated
public class DecodingLenience extends com.braintribe.codec.marshaller.api.DecodingLenience {

	/**
	 * Creates a new <code>DecodingLenience</code> instance where all lenience propeerties are disabled.
	 */
	public DecodingLenience() {
		// nothing to do
	}

	/**
	 * Creates a new <code>DecodingLenience</code> instance and {@link #setLenient(boolean) sets the lenience} as
	 * specified.
	 */
	public DecodingLenience(boolean lenient) {
		setLenient(lenient);
	}


}
