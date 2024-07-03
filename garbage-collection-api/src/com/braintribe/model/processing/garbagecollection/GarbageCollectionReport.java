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
package com.braintribe.model.processing.garbagecollection;

/**
 * Holds data about a GC run (which entities have been deleted, how many per type, etc.) and can
 * {@link #createReport(GarbageCollectionReportSettings) print a report}.
 *
 * @author michael.lafite
 */
public interface GarbageCollectionReport {

	/**
	 * Creates a report containing information such as the number of deleted entities (per type), etc. What information
	 * exactly will be included depends on the implementation and also on the specified <code>settings</code>.
	 */
	public String createReport(GarbageCollectionReportSettings settings);

	/**
	 * Specifies settings for {@link GarbageCollectionReport#createReport(GarbageCollectionReportSettings)}.
	 */
	public static class GarbageCollectionReportSettings {
		private boolean listIndividualEntities = true;

		public GarbageCollectionReportSettings() {
			// nothing to do
		}

		public boolean isListIndividualEntities() {
			return this.listIndividualEntities;
		}

		/**
		 * Whether or not list individual entities.
		 */
		public void setListIndividualEntities(final boolean listIndividualEntities) {
			this.listIndividualEntities = listIndividualEntities;
		}
	}

}
