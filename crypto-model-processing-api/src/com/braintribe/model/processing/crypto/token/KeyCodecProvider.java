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
package com.braintribe.model.processing.crypto.token;

import java.security.Key;

import com.braintribe.codec.Codec;
import com.braintribe.model.processing.crypto.token.loader.EncryptionTokenLoaderException;

/**
 * <p>
 * Provides {@link com.braintribe.codec.Codec}(s) capable of importing/exporting {@link Key}(s) from/to byte arrays.
 * 
 * <p>
 * The provided codec shall import and export keys in accordance with the key algorithm passed as argument to the
 * {@link #getKeyCodec(String)} method.
 * 
 */
public interface KeyCodecProvider<T extends Key> {

	/**
	 * <p>
	 * Returns a {@link com.braintribe.codec.Codec} capable of converting {@link Key}(s) of the given
	 * {@code keyAlgorithm} to and from byte arrays.
	 * 
	 * @param keyAlgorithm
	 *            The algorithm of the {@link Key} type to be encoded/decoded.
	 * @return A {@link com.braintribe.codec.Codec} capable of converting {@link Key}(s) of the given
	 *         {@code keyAlgorithm} to and from byte arrays.
	 * @throws EncryptionTokenLoaderException
	 *             If any error occur while obtaining the codec.
	 */
	Codec<T, byte[]> getKeyCodec(String keyAlgorithm) throws EncryptionTokenLoaderException;

}
