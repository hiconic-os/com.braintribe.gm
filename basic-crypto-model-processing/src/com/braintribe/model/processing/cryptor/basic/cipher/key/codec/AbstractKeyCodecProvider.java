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
package com.braintribe.model.processing.cryptor.basic.cipher.key.codec;

import java.security.Key;
import java.util.HashMap;
import java.util.Map;

import com.braintribe.codec.Codec;
import com.braintribe.codec.CodecException;
import com.braintribe.model.processing.crypto.token.KeyCodecProvider;
import com.braintribe.model.processing.crypto.token.loader.EncryptionTokenLoaderException;

/**
 * <p>
 * Provides common functionalities to {@link KeyCodecProvider} implementations.
 * 
 */
public abstract class AbstractKeyCodecProvider<T extends Key> implements KeyCodecProvider<T> {

	private Map<String, Codec<T, byte[]>> cachedKeyCodecs = new HashMap<>();
	
	protected abstract Codec<T, byte[]> createKeyCodec(String algorithm) throws EncryptionTokenLoaderException;
	
	protected Codec<T, byte[]> getKeyCodec(Class<T> type, String algorithm) throws EncryptionTokenLoaderException {
		
		String i = cachedKeyCodecId(type, algorithm);
		
		Codec<T, byte[]> codec = cachedKeyCodecs.get(i);
		
		if (codec != null) {
			return codec;
		}
		
		codec = createKeyCodec(algorithm);
		
		cachedKeyCodecs.put(i, codec);
		
		return codec;
		
	}
	
	private String cachedKeyCodecId(Class<T> type, String algorithm) {
		return type.getSimpleName()+"."+algorithm;
	}
	
	public static CodecException asCodecException(String message, Exception e) {
		if (e.getMessage() != null) {
			message = ": "+e.getMessage();
		}
		return new CodecException(message, e);
	}
	
}
