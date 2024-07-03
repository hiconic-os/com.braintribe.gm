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
package com.braintribe.model.processing.itw.asm;

import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

/**
 *
 */
public abstract class AsmClass implements AsmType {

	protected String name;
	protected int modifiers;
	protected AsmClassPool classPool;

	public AsmClass(String name, AsmClassPool classPool) {
		this.name = name;
		this.classPool = classPool;
	}

	public AsmClassPool getClassPool() {
		return classPool;
	}

	public String getName() {
		return name;
	}

	public String getSimpleName() {
		int index = name.lastIndexOf(".");
		return index >= 0 ? name.substring(index + 1) : name;
	}

	public int getModifiers() {
		return modifiers;
	}

	// ////////////////////

	public abstract AsmMethod getMethod(String name, AsmClass returnType, AsmClass... params);

	protected abstract AsmMethod getMethod(MethodSignature ms, AsmClass returnType);

	private String internalName;
	private String internalNameLonger;

	public String getInternalName() {
		if (internalName == null) {
			internalName = AsmUtils.getInternalName(this);
		}

		return internalName;
	}

	@Override
	public String genericSignatureOrNull() {
		return null;
	}

	/** Internal name including the L and semicolon, if necessary (object) */
	@Override
	public String getInternalNameLonger() {
		if (internalNameLonger == null) {
			internalNameLonger = AsmUtils.getInternalNameLonger(this);
		}

		return internalNameLonger;
	}

	@Override
	public AsmClass getRawType() {
		return this;
	}

	public boolean isInterface() {
		return Modifier.isInterface(modifiers);
	}

	public boolean isAbstract() {
		return Modifier.isAbstract(modifiers);
	}

	public abstract AsmClass getDeclaringClass();

	public abstract List<? extends AsmClass> getDeclaredClasses();

	@Override
	public abstract String toString();

	@Override
	public abstract boolean isPrimitive();

	@Override
	public boolean isArray() {
		return false;
	}

	protected void addMemberNamesTo(Set<String> names) {
		if (isAbstract()) {
			for (AsmClass iface : getInterfaces()) {
				if (iface != null) {
					iface.addMemberNamesTo(names);
				}
			}
		}

		AsmClass s = getSuperClass();
		if (s != null) {
			s.addMemberNamesTo(names);
		}

		getMethods().forEach( m -> names.add(m.getName()));

		getFields().forEach(f -> names.add(f.getName()));
	}

	public abstract AsmClass getSuperClass();

	public abstract List<? extends AsmClass> getInterfaces();

	public abstract Stream<? extends AsmMethod> getMethods();

	public abstract Stream<AsmField> getFields();

	protected static final class MethodSignature {
		protected String name;
		protected AsmClass[] params;

		public MethodSignature(String name, AsmClass[] params) {
			this.name = name;
			this.params = params;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + name.hashCode();
			result = prime * result + params.length;
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof MethodSignature)) {
				return false;
			}

			MethodSignature ms = (MethodSignature) obj;

			return name.equals(ms.name) && Arrays.equals(params, ms.params);
		}

		@Override
		public String toString() {
			return name + "(..)";
		}
	}
}
