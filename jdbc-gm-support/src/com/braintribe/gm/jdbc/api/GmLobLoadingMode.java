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
package com.braintribe.gm.jdbc.api;

/**
 * For {@link GmColumn}s backed by 2 actual DB columns with one of them being BLOB or CLOB we specify
 * ({@link GmSelectBuilder#lobLoading(GmColumn, GmLobLoadingMode) per query}) which underlying columns we want to load. This can be used to
 * optimize querying to, for example, only load the non-LOB query and let the LOB one be loaded on demand only.
 * 
 * @author peter.gazdik
 */
public enum GmLobLoadingMode {

	ALL,
	NO_LOB,
	ONLY_LOB,

}
