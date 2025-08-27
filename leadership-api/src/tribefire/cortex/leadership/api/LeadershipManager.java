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
package tribefire.cortex.leadership.api;

public interface LeadershipManager {

	/**
	 * Adds a {@link LeadershipListener} for given "domainId".
	 * <p>
	 * Given "listenerId" is used for removal of the listener.
	 * <p>
	 * * IMPORTANT: One can only {@link #removeLeadershipListener(String, LeadershipListener) remove a listener} after the
	 * {@link #addLeadershipListener} method has returned. Otherwise there is no guarantee on the behavior.
	 */
	void addLeadershipListener(String domainId, LeadershipListener listener);

	/** Removes a {@link LeadershipListener} for given "domainId" using the "listenerId" used when adding the listener. */
	void removeLeadershipListener(String domainId, LeadershipListener listener);

	/** Helpful description used for logging. */
	String description();

}

