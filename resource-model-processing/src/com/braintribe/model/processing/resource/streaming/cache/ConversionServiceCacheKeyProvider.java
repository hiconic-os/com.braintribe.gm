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
package com.braintribe.model.processing.resource.streaming.cache;

import java.util.Map;
import java.util.function.Function;

import com.braintribe.codec.Codec;
import com.braintribe.codec.string.MapCodec;
import com.braintribe.codec.string.UrlEscapeCodec;
import com.braintribe.logging.Logger;
import com.braintribe.model.resource.source.ConversionServiceSource;


/**
 * Provides an encoded string representation of the given {@link ConversionServiceSource}.
 * Uses a {@link Codec} to encode the parameters of the {@link ConversionServiceSource}.
 * @author gunther.schenk
 *
 */
public class ConversionServiceCacheKeyProvider implements Function<ConversionServiceSource, String> {
	/**
	 * The logger.
	 */
	protected static Logger logger = Logger.getLogger(ConversionServiceCacheKeyProvider.class);

	private Codec<Map<String, String>, String> mapCodec;

	
	// **************************************************************************
	// Constructor
	// **************************************************************************

	/**
	 * Default constructor
	 */
	public ConversionServiceCacheKeyProvider() {
	}

	// **************************************************************************
	// Getter/Setter
	// **************************************************************************

	public void setMapCodec(Codec<Map<String, String>, String> mapCodec) {
		this.mapCodec = mapCodec;
	}

	/**
	 * @return the mapCodec
	 */
	public Codec<Map<String, String>, String> getMapCodec() {
		if (mapCodec == null) {
			MapCodec<String, String> mapCodec = new MapCodec<String, String>();
			mapCodec.setEscapeCodec(new UrlEscapeCodec());
			mapCodec.setDelimiter(";");
			this.mapCodec = mapCodec;
		}
		return mapCodec;
	}

	// **************************************************************************
	// Interface implementations
	// **************************************************************************

	@Override
	public String apply(ConversionServiceSource source) throws RuntimeException {
		try {
			StringBuilder encodedSource = new StringBuilder();
//			encodedSource.append(source.getPrecursor().getId());
			encodedSource.append(".");
//			encodedSource.append(source.getConverterName());

//			if (source.getParameters() != null && source.getParameters().size() > 0) {
//				encodedSource.append(".");
//				SortedMap<String, String> sortedParameterMap = new TreeMap<String, String>(source.getParameters());
//				encodedSource.append(this.getMapCodec().encode(sortedParameterMap));
//			}

			return encodedSource.toString();
		} catch (Exception e) {
			throw new RuntimeException("Could not encode source parameters.", e);
		}
	}

	public static String getBuildVersion() {
		return "$Build_Version$ $Id: ConversionServiceCacheKeyProvider.java 102880 2018-01-18 11:36:53Z roman.kurmanowytsch $";
	}
}
