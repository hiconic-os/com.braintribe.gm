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
package com.braintribe.model.processing.query.parser.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.braintribe.model.processing.query.parser.api.GmqlQueryParserException;
import com.braintribe.model.processing.query.parser.api.SourceRegistry;
import com.braintribe.model.query.Join;
import com.braintribe.model.query.Source;

public class RegistryManager implements SourceRegistry {

	private Map<String, Source> sources = new HashMap<String, Source>();

	@Override
	public void registerSource(String alias, Source source) {
		Source existingSource = getSourcesRegistry().get(alias);

		if (existingSource != null) {
			if (existingSource instanceof SourceLink) {
				SourceLink sourceLink = (SourceLink) existingSource;
				if (sourceLink.getSource() != null)
					throw new GmqlQueryParserException("The alias " + alias + " is already defined.");
				source.setName(alias);
				sourceLink.setSource(source);
			} else {
				throw new GmqlQueryParserException("The alias " + alias + " is already defined.");
			}
		} else {
			source.setName(alias);
			getSourcesRegistry().put(alias, source);
		}
	}

	@Override
	public Source acquireSource(String alias) {
		if (alias == null)
			return null;

		Source source = getSourcesRegistry().get(alias);
		if (source == null) {
			source = SourceLink.T.create();
			source.setName(alias);
			getSourcesRegistry().put(alias, source);
		}

		return source;
	}

	@Override
	public Join acquireJoin(String alias) {
		Source source = getSourcesRegistry().get(alias);
		if (source == null) {
			source = SourceLink.T.create();
			source.setName(alias);
			getSourcesRegistry().put(alias, source);
		}

		return (Join) source;
	}

	@Override
	public boolean validateIfSourceExists(String alias) {
		if (getSourcesRegistry().get(alias) != null)
			return true;
		return false;
	}

	@Override
	public void validateRegistry(boolean strictEvaluation) {
		Collection<Source> sourcesCollection = getSourcesRegistry().values();
		Source defaultSource = null;
		List<Source> invalidSources = new ArrayList<Source>();
		for (Source source : sourcesCollection) {
			if (source instanceof SourceLink) {
				SourceLink sourceLink = (SourceLink) source;
				boolean invalidSource = false;
				if (sourceLink.getSource() == null) {
					if (strictEvaluation) {
						throw new GmqlQueryParserException("Unresolved source link: " + sourceLink + ", with alias: " + source.getName());
					} else {
						invalidSources.add(source);
						invalidSource = true;
					}
				}
				if (!invalidSource) {
					sourceLink.getSource().setJoins(sourceLink.getJoins());
				}
			} else if (source instanceof DefaultSource) {
				// this is the alias used for entity and property queries
				defaultSource = source;
			}
		}
		if (defaultSource != null) {
			getSourcesRegistry().remove(defaultSource.getName());
		}
		// delete invalid sources in case of NOT strictEvaluation
		if (!strictEvaluation) {
			for (Source source : invalidSources) {
				if (source.getName() != null) {
					getSourcesRegistry().remove(source.getName());
				}
			}
		}
	}

	@Override
	public Map<String, Source> getSourcesRegistry() {
		return sources;
	}

}
