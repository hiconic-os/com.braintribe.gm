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
package com.braintribe.model.processing.dataio;

public interface GmSerializationCodes {
	static final byte CODE_NULL = 0;
	
	static final byte CODE_FALSE = 1;
	static final byte CODE_TRUE = 2;
	
	static final byte CODE_INTEGER = 3; 
	static final byte CODE_LONG = 4; 
	
	static final byte CODE_FLOAT = 5;
	static final byte CODE_DOUBLE = 6;
	static final byte CODE_DECIMAL = 7;
	
	static final byte CODE_STRING = 8;
	static final byte CODE_DATE = 9;
	
	static final byte CODE_REF = 10;
	static final byte CODE_ENUM = 11;
	
	static final byte CODE_LIST = 12;
	static final byte CODE_SET = 13;
	static final byte CODE_MAP = 14;
	
	static final byte CODE_ESCAPE = 15;
	
	// don't add more codes here
	// 0xff -> prop term
}

/*
 * bit shift + if
 * vs.
 * in.readShort()
 */
