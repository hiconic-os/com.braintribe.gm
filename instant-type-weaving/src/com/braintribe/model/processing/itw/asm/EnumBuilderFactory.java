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

import java.util.List;

/**
 * A factory for EnumBuilders - in case somebody wants to extend the behavior of the {@link EnumBuilder}, which is a
 * little tricky as the enum constants are initialized in the {@Code <clinit>} method. Thus if you want extend it,
 * you need a sub-type which adds some more bytecode to the default implementation.
 * 
 * @author peter.gazdik
 */
public interface EnumBuilderFactory<T extends EnumBuilder> {

	T create(AsmClassPool classPool, String typeSignature, List<AsmEnumConstant> constants, List<AsmClass> superInterfaces);

}
