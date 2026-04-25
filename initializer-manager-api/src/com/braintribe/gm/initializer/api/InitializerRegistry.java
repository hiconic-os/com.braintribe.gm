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
package com.braintribe.gm.initializer.api;

/**
 * Registry for {@link InitializerTask}s, which are tasks that are meant to be run once, regardless of how many times the application is started, as
 * long as they would produce the same effect. In order to tell the framework whether or not the result would be the same an
 * {@link InitializerFingerprintResolver} is registered, which produces a "fingerprint" (e.g. a version number, a hash, etc.), and the framework runs
 * the task iff the fingerprint has changed (or the task with given name has not been run before).
 * 
 * @author peter.gazdik
 */
public interface InitializerRegistry {

	void registerInitializer(String initializerName, InitializerFingerprintResolver fingerprintResolver, InitializerTask task);

	default //
	void registerInitializer(InitializerSymbol symbol, InitializerFingerprintResolver fingerprintResolver, InitializerTask task) {
		registerInitializer(symbol.name(), fingerprintResolver, task);
	}

	void ensureOrder(String runsFirstName, String runsLaterNamer);

	default //
	void ensureOrder(InitializerSymbol runsFirst, InitializerSymbol runsLater) {
		ensureOrder(runsFirst.name(), runsLater.name());
	}

}
