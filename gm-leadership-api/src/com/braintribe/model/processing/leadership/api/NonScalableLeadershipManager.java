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
package com.braintribe.model.processing.leadership.api;

public class NonScalableLeadershipManager implements LeadershipManager {
	@Override
	public void addLeadershipListener(final String domainId, String candidateId, LeadershipListener listener) {
		listener.onLeadershipGranted(new LeadershipHandle() {
			@Override
			public void release() {
				// to be ignored in this implementation
			}

			@Override
			public String getIdentification() {
				return domainId;
			}
		});
	}

	@Override
	public void removeLeadershipListener(String domainId, String candidateId) {
		// will never happen in this implementation
	}

	@Override
	public String description() {
		return "Local Leadership Manager";
	}
}
