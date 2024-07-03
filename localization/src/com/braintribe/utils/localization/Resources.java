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

import java.awt.Image;
import java.awt.Toolkit;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.MissingResourceException;

import javax.swing.Icon;
import javax.swing.ImageIcon;

public abstract class Resources {
	private static final Map<String, Object> cache = new HashMap<String, Object>();
	private static final Object MISSING = new Object();
	
	/**
	 * Returns an icon created from an image found in the classpath in the same place as this class 
	 * - that is, using Messages.class.getResource(name). Icons that where created once are cached.
	 * If the icon cannot be found, a MissingResourceException is raised.
	 */
	public final synchronized Icon getIcon(String name) {
		String k = "icon:"+name;
		
		Object obj = cache.get(k);
		
		//check for negative cache entry
		if (obj==MISSING) 
			throw new MissingResourceException("image not found in classloader: "+name, getClass().getName(), name);
		
		Icon icon = (Icon)obj;
		
		if (icon==null) {
			URL url = getClass().getResource(name);
			
			if (url==null) {
				cache.put(k, MISSING);
				throw new MissingResourceException("image not found in classloader: "+name, getClass().getName(), name);
			}
			
			icon = new ImageIcon(url); 
			cache.put(k, icon);
		}

		return icon;
	}
	
	/**
	 * Returns an image found in the classpath in the same place as this class 
	 * - that is, using Messages.class.getResource(name). Images that where created once are cached.
	 * If the image cannot be found, a MissingResourceException is raised.
	 */
	public final synchronized Image getImage(String name) {
		String k = "image:"+name;
		
		Object obj = cache.get(k);
		
		//check for negative cache entry
		if (obj==MISSING) 
			throw new MissingResourceException("image not found in classloader: "+name, getClass().getName(), name);
		
		Image image = (Image)obj;
		
		if (image==null) {
			URL url = getClass().getResource(name);
			
			if (url==null) {
				cache.put(k, MISSING);
				throw new MissingResourceException("image not found in classloader: "+name, getClass().getName(), name);
			}
			
			image = Toolkit.getDefaultToolkit().getImage(url); 
			cache.put(k, image);
		}

		return image;
	}
}
