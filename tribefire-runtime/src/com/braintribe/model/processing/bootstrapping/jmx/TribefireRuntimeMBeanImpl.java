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
package com.braintribe.model.processing.bootstrapping.jmx;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.management.NotCompliantMBeanException;
import javax.management.StandardMBean;
import javax.management.openmbean.CompositeDataSupport;
import javax.management.openmbean.CompositeType;
import javax.management.openmbean.OpenType;
import javax.management.openmbean.SimpleType;
import javax.management.openmbean.TabularData;
import javax.management.openmbean.TabularDataSupport;
import javax.management.openmbean.TabularType;

import com.braintribe.logging.Logger;
import com.braintribe.model.processing.bootstrapping.TribefireRuntime;

public class TribefireRuntimeMBeanImpl extends StandardMBean implements TribefireRuntimeMBean {

	private static Logger logger = Logger.getLogger(TribefireRuntimeMBeanImpl.class);
	
	public TribefireRuntimeMBeanImpl() throws NotCompliantMBeanException {
		super(TribefireRuntimeMBean.class);
	}

	@Override
	public Set<String> getPropertyNames() {
		return TribefireRuntime.getPropertyNames();
	}

	@Override
	public void setProperty(String key, String value) {
		TribefireRuntime.setProperty(key, value);
	}

	@Override
	public String getProperty(String key) {
		return TribefireRuntime.getProperty(key);
	}

	@Override
	public TabularData getPropertiesTable() {

		try {
			CompositeType ct = new CompositeType("TFRuntimeProperties", 
					"Tribefire Runtime Properties", 
					new String[] {"Name", "Value"}, 
					new String[] {"Name", "Value"}, 
					new OpenType[] {SimpleType.STRING, SimpleType.STRING});

			TabularType tt = new TabularType("TribefireRuntimeProperties", "The TribefireRuntime Properties", ct, new String[]{"Name", "Value"});
			TabularData data = new TabularDataSupport(tt);

			Set<String> propertyNames = TribefireRuntime.getPropertyNames();

			if (propertyNames != null) {
				for (String propName : propertyNames) {
					String value = TribefireRuntime.getProperty(propName);
					data.put(new CompositeDataSupport(ct, new String[] {"Name", "Value"}, new String[] {propName, value}));
				}
			}
			return data;
		} catch(Exception e) {
			logger.error("Could not compile tabular data of TribefireRuntime properties.", e);
			return null;
		}
	}

	@Override
	public List<String> getPropertiesFlat() {
		
		List<String> result = new ArrayList<String>();
		
		Set<String> propertyNames = TribefireRuntime.getPropertyNames();

		if (propertyNames != null) {
			for (String propName : propertyNames) {
				String value = TribefireRuntime.getProperty(propName);
				result.add(propName+"="+value);
			}
		}
		
		return result;
	}

}
