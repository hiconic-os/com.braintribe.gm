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
package com.braintribe.model.access.impl;

import java.net.URL;
import java.util.function.Supplier;

import org.w3c.dom.Document;

import com.braintribe.cfg.Required;
import com.braintribe.codec.dom.genericmodel.GenericModelRootDomCodec;
import com.braintribe.model.generic.GMF;

import com.braintribe.utils.xml.XmlTools;

public class XmlBasedModelDataProvider<T> implements Supplier<T> {
	private URL url;
	private GenericModelRootDomCodec<Object> codec;

	protected GenericModelRootDomCodec<Object> getCodec() {
		if (this.codec == null) {
			this.codec = new GenericModelRootDomCodec<Object>();
			this.codec.setType(GMF.getTypeReflection().getBaseType());
		}

		return this.codec;
	}

	@Required
	public void setUrl(final URL url) {
		this.url = url;
	}

	@Override
	public T get() throws RuntimeException {
		try {
			final Document document = XmlTools.loadXML(this.url);
			final Object data = getCodec().decode(document);
			@SuppressWarnings("unchecked")
			final T castedData = (T) data;
			return castedData;
		} catch (final Exception e) {
			throw new RuntimeException("error while loading or decoding document from file", e);
		}
	}
}
