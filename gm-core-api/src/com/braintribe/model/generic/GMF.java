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

import java.util.function.Supplier;

import com.braintribe.model.generic.pr.AbsenceInformation;
import com.braintribe.model.generic.reflection.EssentialTypes;
import com.braintribe.model.generic.reflection.GenericModelTypeReflection;

import jsinterop.annotations.JsMethod;

/**
 * GenericModelFramework - provides some convenience functions, most notably {@link #getTypeReflection()}.
 * <p>
 * This is the main access point to platform-specific (java vs GWT) implementations of {@link GenericModelTypeReflection} or {@link GmPlatform} (via
 * {@link #platform()}).
 * 
 * @see GmPlatformProvider
 */
@SuppressWarnings("unusable-by-js")
public class GMF implements EssentialTypes {

	private static final GenericModelTypeReflection typeReflection;
	private static final GmPlatform platform;

	private static Supplier<String> localeProvider;

	private static AbsenceInformation absenceInformation;

	static {
		platform = GmPlatformProvider.provide();
		typeReflection = platform.getTypeReflection();

		platform.initialize();
	}

	/** @return {@link GmPlatform}, which solely depends on {@link GmPlatformProvider} */
	public static GmPlatform platform() {
		return platform;
	}

	@JsMethod(namespace = GmCoreApiInteropNamespaces.gm)
	public static <T extends GenericModelTypeReflection> T getTypeReflection() {
		return (T) typeReflection;
	}

	public static void setLocaleProvider(Supplier<String> localeProvider) {
		GMF.localeProvider = localeProvider;
	}

	public static Supplier<String> getLocaleProvider() {
		return localeProvider;
	}

	@JsMethod(namespace = GmCoreApiInteropNamespaces.gm)
	public static String getLocale() {
		if (localeProvider != null)
			return localeProvider.get();
		else
			return "default";
	}

	/**
	 * @return instance of {@link AbsenceInformation} which can be used as a standard value (in case no special information needs to be stored, nor
	 *         does this have to be attached to a session).
	 */
	@JsMethod(namespace = GmCoreApiInteropNamespaces.gm)
	public static AbsenceInformation absenceInformation() {
		if (absenceInformation == null)
			absenceInformation = typeReflection.<AbsenceInformation> getEntityType(AbsenceInformation.class).createPlain();

		return absenceInformation;
	}

}
