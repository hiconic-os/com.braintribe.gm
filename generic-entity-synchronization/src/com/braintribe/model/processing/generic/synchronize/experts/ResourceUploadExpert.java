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
package com.braintribe.model.processing.generic.synchronize.experts;

import java.io.File;
import java.io.FileInputStream;
import java.net.URL;

import com.braintribe.model.generic.session.InputStreamProvider;
import com.braintribe.model.processing.generic.synchronize.GenericEntitySynchronizationException;
import com.braintribe.model.resource.source.FileUploadSource;
import com.braintribe.model.resource.source.UploadSource;
import com.braintribe.model.resource.source.UrlUploadSource;

public interface ResourceUploadExpert<T extends UploadSource> {

	public static final ResourceUploadExpert<FileUploadSource> fileUploadExpert = new ResourceUploadExpert<FileUploadSource>() {
		@Override
		public UploadInfo getUploadInfo(FileUploadSource uploadSource) {
			File localFile = new File(uploadSource.getLocalFilePath());
			return new UploadInfo() {
				
				@Override
				public InputStreamProvider getInputStreamProvider() {
					if (!localFile.exists()) {
						throw new GenericEntitySynchronizationException("Local file: "+localFile.getAbsolutePath()+" does not exist.");
					}
					return () -> new FileInputStream(localFile);
				}
				
				@Override
				public String getDefaultResourceName() {
					return localFile.getName();
				}
			};
		}
	};
	
	public static final ResourceUploadExpert<UrlUploadSource> urlUploadExpert = new ResourceUploadExpert<UrlUploadSource>() {
		@Override
		public UploadInfo getUploadInfo(UrlUploadSource source) {
			return new UploadInfo() {
				
				@Override
				public InputStreamProvider getInputStreamProvider() {
					return () -> new URL(source.getUrl()).openStream();
				}
				
				@Override
				public String getDefaultResourceName() {
					return null;
				}
			};
		}
	};
	

	UploadInfo getUploadInfo(T source);
	
	public interface UploadInfo {
		InputStreamProvider getInputStreamProvider();
		String getDefaultResourceName();
	}
	
	
}
