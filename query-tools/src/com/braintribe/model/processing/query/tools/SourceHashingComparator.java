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
package com.braintribe.model.processing.query.tools;

import com.braintribe.cc.lcd.HashingComparator;
import com.braintribe.model.query.From;
import com.braintribe.model.query.Join;
import com.braintribe.model.query.Source;

/**
 * This comparator considers two sources equal if they denote the same path via their froms/joins. E.g, there are many
 * ways how to represent the path {@code Person.company.address.street}, with 1, 2 or 3 joins, but this comparator would
 * consider all of them being equal.
 *
 * Use this when resolving information related to the source path, e.g. when determining whether or not a given source
 * is mapped to persistence.
 * 
 * @author peter.gazdik
 */
public class SourceHashingComparator implements HashingComparator<Source> {

	public static final SourceHashingComparator INSTANCE = new SourceHashingComparator();

	private SourceHashingComparator() {
	}

	@Override
	public boolean compare(Source s1, Source s2) {
		if (s1 == s2)
			return true;

		if (s1 == null || s2 == null)
			return false;

		return buildPath(s1).equals(buildPath(s2));
	}

	@Override
	public int computeHash(Source s) {
		return s == null ? 0 : buildPath(s).hashCode();
	}

	public static String buildPath(Source source) {
		if (source instanceof From)
			return ((From) source).getEntityTypeSignature();

		Join join = (Join) source;
		return buildPath(join.getSource()) + "." + join.getProperty();
	}

}
