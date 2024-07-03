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

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;

import org.apache.http.impl.client.CloseableHttpClient;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import com.braintribe.common.lcd.Pair;
import com.braintribe.testing.category.SpecialEnvironment;
import com.braintribe.transport.http.DefaultHttpClientProvider;

public class HttpToolsTest {

	@Test
	public void testMimeTypes() {
		
		assertThat(HttpTools.getMimeTypeFromContentType("text/html; charset=utf-8", false)).isEqualTo("text/html");
		assertThat(HttpTools.getMimeTypeFromContentType("text/html", false)).isEqualTo("text/html");
		assertThat(HttpTools.getMimeTypeFromContentType("", false)).isNull();
		assertThat(HttpTools.getMimeTypeFromContentType(null, false)).isNull();
		
	}
	
	@Test
	public void testContentDispositionParsing() {
		
		assertThat(HttpTools.getFilenameFromContentDisposition("form-data; name=\"fieldName\"; filename=\"filename.jpg\"")).isEqualTo("filename.jpg");
		assertThat(HttpTools.getFilenameFromContentDisposition("form-data; name=\"fieldName\"; filename*=UTF-8''filename.jpg")).isEqualTo("filename.jpg");
		assertThat(HttpTools.getFilenameFromContentDisposition("form-data; name=\"fieldName\"")).isNull();
		assertThat(HttpTools.getFilenameFromContentDisposition("form-data")).isNull();
		assertThat(HttpTools.getFilenameFromContentDisposition("")).isNull();
		assertThat(HttpTools.getFilenameFromContentDisposition(null)).isNull();
		
	}
	
	@Test
	@Category(SpecialEnvironment.class)
	public void testDownload() throws Exception {
		
		DefaultHttpClientProvider provider = new DefaultHttpClientProvider();
		CloseableHttpClient httpClient = provider.provideHttpClient();
		Pair<File, String> pair = HttpTools.downloadFromUrl("https://12r9gfubiw93zb4r366chx69-wpengine.netdna-ssl.com/wp-content/uploads/2018/02/MainBannerVision2.png", httpClient);
		System.out.println("Downloaded: "+pair.first().getAbsolutePath()+" with name "+pair.second());
		pair.first().delete();
	}
}
