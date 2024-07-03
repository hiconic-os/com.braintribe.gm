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
package com.braintribe.model.processing.resource.cache;

import com.braintribe.model.processing.resource.streaming.ResourceStreamException;
import com.braintribe.model.resource.source.ResourceSource;

/**
 * The interface for implementations of Resource Cache.
 * 
 * @author gunther.schenk
 *
 */
public interface ResourceCache {

	/**
	 * Returns an already existing {@link ResourceCacheEntry} or creates a new one. <br>
	 * Keeps track on locking of the entry creation. If the creation is already in progress this method blocks until the
	 * creation is done.
	 */
	ResourceCacheEntry acquireCacheEntry(ResourceSource representation) throws ResourceStreamException;

	/**
	 * Same as {@link #acquireCacheEntry(ResourceSource)} but with a custom cacheKey and cache domain.
	 */
	ResourceCacheEntry acquireCacheEntry(String cacheKey, String domain) throws ResourceStreamException;

	/**
	 * Returns the cache entry for this source without locking and respecting creation locks.
	 */
	ResourceCacheEntry getCacheEntry(ResourceSource source) throws ResourceStreamException;

	/**
	 * Same as {@link #getCacheEntry(ResourceSource)} but with a custom cacheKey and cache domain.
	 */
	ResourceCacheEntry getCacheEntry(String cacheKey, String domain) throws ResourceStreamException;

}
