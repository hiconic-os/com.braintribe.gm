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

import java.net.URL;
import java.util.function.Supplier;

import org.w3c.dom.Document;

import com.braintribe.model.generic.GMF;
import com.braintribe.model.generic.reflection.GenericModelType;

import com.braintribe.utils.xml.XmlTools;

/**
 * This provider gets an XML (via URL) as input, decodes the XML using {@link GenericModelRootDomCodec} and provides the
 * result.
 * 
 * @author michael.lafite
 * 
 * @param <T>
 *            the type of the provided value.
 */
public class GmXmlResourceProvider<T> implements Supplier<T> {

	private URL resource;

	protected GenericModelRootDomCodec<T> codec;

	public GmXmlResourceProvider() {
		this.codec = new GenericModelRootDomCodec<>();
		GenericModelType type = GMF.getTypeReflection().getBaseType();
		this.codec.setType(type);
	}

	public URL getResource() {
		URL result = this.resource;
		if (result != null) {
			return result;

		}
		throw new IllegalStateException("No ressource set! " + GmXmlResourceProvider.class.getSimpleName()
				+ " not initialized properly (yet)!");
	}

	public void setResource(URL resource) {
		this.resource = resource;
	}

	@Override
	public T get() throws RuntimeException {
		try {
			Document document = XmlTools.loadXML(getResource());
			T result = this.codec.decode(document);
			return result;
		} catch (Exception e) {
			throw new RuntimeException("Error while decoding DOM!", e);
		}
	}

}
