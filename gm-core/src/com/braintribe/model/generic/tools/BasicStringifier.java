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
package com.braintribe.model.generic.tools;

/**
 * @author peter.gazdik
 */
public class BasicStringifier extends AbstractStringifier {

	public BasicStringifier() {
		this(new StringBuilder(), "", "\t");
	}

	public BasicStringifier(AbstractStringifier parent) {
		super(parent);
	}

	public BasicStringifier(StringBuilder builder, String prefix, String tab) {
		super(builder, prefix, tab);
	}

	public BasicStringifier(Appendable builder, String prefix, String tab) {
		super(builder, prefix, tab);
	}

	public Appendable appendable() {
		return builder;
	}

	@Override
	public void levelUp() {
		super.levelUp();
	}

	@Override
	public void levelDown() {
		super.levelDown();
	}

	@Override
	public String getFullClassName(Object object) {
		return super.getFullClassName(object);
	}

	@Override
	public String getSimpleClassName(Object object) {
		return super.getSimpleClassName(object);
	}

	@Override
	public String getSimpleName(String name) {
		return super.getSimpleName(name);
	}

	@Override
	public void print(String s) {
		super.print(s);
	}

	@Override
	public void println() {
		super.println();
	}

	@Override
	public void println(String s) {
		super.println(s);

	}

}
