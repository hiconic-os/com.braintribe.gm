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

import java.io.File;
import java.net.URL;
import java.util.function.Consumer;

import org.w3c.dom.Document;

import com.braintribe.model.generic.GMF;
import com.braintribe.model.generic.reflection.GenericModelType;
import com.braintribe.utils.xml.XmlTools;

/**
 * a counter part for the GmXmlProvider.. just the other way round 
 * 
 * @author pit
 */
public class GmXmlResourceReceiver<T> implements Consumer<T>{
	
	private URL resource;
	protected GenericModelRootDomCodec<T> codec;
	
	public GmXmlResourceReceiver() {
		this.codec = new GenericModelRootDomCodec<>();
		GenericModelType type = GMF.getTypeReflection().getBaseType();
		this.codec.setType(type);	
	}
	
	public URL getResource() {
		return resource;
	}

	public void setResource(URL resource) {
		this.resource = resource;
	}

	
	@Override
	public void accept(T object) throws RuntimeException {
		try {
			Document document = codec.encode( object);
			URL resource = getResource();
			if (resource == null) {
				throw new RuntimeException( "resource may not be null at this point");
			}
			XmlTools.writeXml(document, new File( resource.toURI()), "UTF-8");
		} catch (Exception e) {
			throw new RuntimeException( "Error while encoding DOM!");
		}		
	}
	
	

}
