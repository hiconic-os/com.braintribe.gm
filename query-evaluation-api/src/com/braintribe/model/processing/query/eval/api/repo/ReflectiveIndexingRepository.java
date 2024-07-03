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
package com.braintribe.model.processing.query.eval.api.repo;

/**
 * Originally, method added in this interface was part of {@link IndexingRepository}, however, it was never actually
 * used by anyone. But since it is already supported by the smood 2.0, I did not want to completely remove it, so the
 * original interface was split.
 * 
 * @author peter.gazdik
 */
public interface ReflectiveIndexingRepository extends IndexingRepository {

	RepositoryInfo provideRepositoryInfo();

}
