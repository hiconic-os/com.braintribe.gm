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
package com.braintribe.transport.http.util;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.entity.ContentType;
import org.apache.http.util.EntityUtils;

import com.braintribe.logging.Logger;

public class ErrorHelper {

	private static Logger logger = Logger.getLogger(ErrorHelper.class);
	
	public static IOException processErrorResponse(String url, String method, HttpResponse response, Exception reason) {

		StatusLine status = null;
		String responseBody = null; 
		
		if (response != null) {

			status = response.getStatusLine();

			HttpEntity responseEntity = response.getEntity();
			if (responseEntity != null) {

				ContentType contentType = ContentType.get(responseEntity);
				if (contentType != null) {
					String mimeType = contentType.getMimeType();
					if (mimeType != null && mimeType.toLowerCase().startsWith("text/")) {
						try {
							responseBody = EntityUtils.toString(responseEntity);
						} catch (Exception e) {
							logger.error("Could not get response body", e);
						}	
					}
				}

				HttpTools.consumeResponse(url, response);

			}
		}

		StringBuilder sb = new StringBuilder(method);
		sb.append(" request to [ ");
		sb.append(url);
		sb.append(" ] failed");
		if (status != null) {
			sb.append(": ");
			sb.append(status);
		}
		if (responseBody != null) {
			sb.append('\n');
			sb.append(responseBody);
		}
		if (reason == null) {
			return new IOException(sb.toString());	
		} else {
			return new IOException(sb.toString(), reason);
		}
		
	}

}
