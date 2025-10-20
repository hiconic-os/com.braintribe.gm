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
package com.braintribe.model.generic;

import java.io.InputStream;
import java.util.Properties;

import com.braintribe.logging.Logger;
import com.braintribe.model.generic.reflection.GenericModelException;

/**
 * Provides the correct implementation of {@link GmPlatform}. This is the default JVM implementation.
 * <p>
 * As of 22.2.2013 (when this was created) the intended usage was that the {@link GMF} is provided with the right implementation of this interface via
 * {@linkplain GmPlatformProvider} at the startup automatically. For this there the static method {@link #provide()} should be used. As stated before,
 * this is the default JVM implementation for such provider and different platforms are expected to implement (emulate) their own version of this
 * class (i.e. such with exact same name and methods).
 */
public class GmPlatformProvider {

	private static final String GMF_PROPS_RESOURCE_NAME = "META-INF/gm.properties";
	private static final String PLATFORM_IMPL_PROPERTY = "platform.implementation";

	private static final Logger logger = Logger.getLogger(GmPlatformProvider.class);

	private static GmPlatform plattform;

	public static GmPlatform provide() {
		if (plattform == null) {

			try {
				String gmfDelegateClassName = null;

				if (System.getenv().containsKey(PLATFORM_IMPL_PROPERTY)) {
					gmfDelegateClassName = System.getenv().get(PLATFORM_IMPL_PROPERTY);
					logger.debug("Found " + PLATFORM_IMPL_PROPERTY + " setting in environment.");

				} else if (null != System.getProperty(PLATFORM_IMPL_PROPERTY)) {
					gmfDelegateClassName = System.getProperty(PLATFORM_IMPL_PROPERTY);
					logger.debug("Found " + PLATFORM_IMPL_PROPERTY + " setting in system properties.");

				} else {
					logger.debug("Try to load " + PLATFORM_IMPL_PROPERTY + " from META-INF.");

					InputStream is = GenericEntity.class.getClassLoader().getResourceAsStream(GMF_PROPS_RESOURCE_NAME);

					if (is == null)
						is = Thread.currentThread().getContextClassLoader().getResourceAsStream(GMF_PROPS_RESOURCE_NAME);

					if (is == null)
						throw new GenericModelException(
								"Cannot initialize GmPlatform - file not found 'gm.properties'. This usually happens when gm-core4-jvm is not on the classpath. "
										+ "If it shouldn't be on the classpath and you wanted to set the 'platform.implementation' property"
										+ " as environment variable or as a JVM argument, well, it didn't work.");

					try {
						Properties props = new Properties();
						props.load(is);

						gmfDelegateClassName = props.getProperty(PLATFORM_IMPL_PROPERTY);

					} catch (Exception e) {
						throw new RuntimeException("Error while loading properties", e);
					} finally {
						is.close();
					}
				}
				Class<?> delegateClass = Class.forName(gmfDelegateClassName);

				if (!GmPlatform.class.isAssignableFrom(delegateClass))
					throw new RuntimeException("Wrong GM configuration. Class [" + gmfDelegateClassName + "] defined by [" + PLATFORM_IMPL_PROPERTY
							+ "] property  and loaded by ClassLoader '" + delegateClass.getClassLoader() + "' must implement ["
							+ GmPlatform.class.getName() + "] loaded by ClassLoader [" + GmPlatform.class.getClassLoader() + "]");

				plattform = delegateClass.asSubclass(GmPlatform.class).getDeclaredConstructor().newInstance();

			} catch (Exception e) {
				throw new RuntimeException("Error while initializing GmPlatform", e);
			}
		}

		return plattform;
	}
}
