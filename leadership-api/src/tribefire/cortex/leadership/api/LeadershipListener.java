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

public interface LeadershipListener {

	/**
	 * Sends a signal to the listener that it was given leadership.
	 * <p>
	 * Note the implementation should really handle it as a signal and return quickly.
	 * <p>
	 * Note also all listeners for the same domainId on a single machine (or, more precisely, registered on the same {@link LeadershipManager}
	 * instance) will be granted leadership at the same time.
	 */
	void onLeadershipGranted(LeadershipContext context);

	/**
	 * Sends a signal to the listener to stop executing its current task and give up leadership.
	 * <p>
	 * This is CURRENTLY NOT USED.
	 */
	void surrenderLeadership(LeadershipContext context);

}
