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
package com.braintribe.codec.dom.genericmodel;

import java.util.Date;

import com.braintribe.codec.Codec;
import com.braintribe.codec.CodecException;
import com.braintribe.codec.context.CodingContext;
import com.braintribe.codec.string.DateCodec;

public class GenericModelDateCodec implements Codec<Date, String> {
	private static DateCodec dateCodec1 = new DateCodec("yyyy.MM.dd HH:mm:ss");
	private static DateCodec dateCodec2 = new DateCodec("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

	@Override
	public String encode(Date value) throws CodecException {
		EncodingContext context = CodingContext.get();
		
		switch(context.getVersion()) {
		case 1:
			return dateCodec1.encode(value);
		default:
			return dateCodec2.encode(value);
		}
	}
	
	@Override
	public Date decode(String encodedValue) throws CodecException {
		DecodingContext context = CodingContext.get();
		
		switch(context.getVersion()) {
		case 1:
			return dateCodec1.decode(encodedValue);
		default:
			return dateCodec2.decode(encodedValue);
		}
	}
	
	@Override
	public Class<Date> getValueClass() {
		return Date.class;
	}

}
