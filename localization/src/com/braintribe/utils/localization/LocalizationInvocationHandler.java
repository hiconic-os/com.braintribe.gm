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
package com.braintribe.utils.localization;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class LocalizationInvocationHandler<T extends Localizable> implements InvocationHandler {
  
	private ResourceBundle resourceBundle;
	private Class<T> interfaceClass;
	
	public LocalizationInvocationHandler(Class<T> interfaceClass) {
		this.interfaceClass = interfaceClass;
	}
	
	public ResourceBundle getResourceBundle() {
		if (resourceBundle == null) {
			Locale locale = Locale.getDefault();
			resourceBundle = Utf8ResourceBundle.getBundle(interfaceClass.getName(), locale, interfaceClass.getClassLoader());
		}

		return resourceBundle;
	}
	
	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		
		String key = method.getName();
		String value = null; 
		
		try {
			value = getResourceBundle().getString(key); 
		} catch (MissingResourceException e) {
			Default def =  method.getAnnotation(Default.class);
			if (def != null) {
				value = def.value();
			}
			else {
				value = key;
			}
		}

		if (args != null && args.length != 0) {
			value = MessageFormat.format(value, args);
		}
		
		return value;
	}
}
