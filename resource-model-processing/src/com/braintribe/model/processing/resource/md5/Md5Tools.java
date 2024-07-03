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
package com.braintribe.model.processing.resource.md5;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.security.MessageDigest;

public class Md5Tools {
	public static Md5 getMd5(InputStream in) throws Exception {
		MessageDigest digest = MessageDigest.getInstance("MD5");
		byte md[] = new byte[8192];
		for (int n = 0; (n = in.read(md)) > -1;) {
			digest.update(md, 0, n);
		}
		return new Md5(digest.digest());
	}
	
	public static Md5 getMd5(File file) throws Exception {
		FileInputStream in = new FileInputStream(file);
		
		try {
			return getMd5(in);
		}
		finally {
			in.close();
		}
	}
	
	public static Md5 getMd5(URL url) throws Exception {
		InputStream in = url.openStream();
		
		try {
			return getMd5(in);
		}
		finally {
			in.close();
		}
	}
}
