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
package com.braintribe.model.processing.itw.synthesis.java;

/**
 * Constants that are used to initialize primitive properties in case we are using the Object array to represent properties (efficient mode). 
 */
public class JvmConstants {

	private static char c;
	public static Character DEFAULT_CHAR = c;
	
	private static byte b;
	public static Byte DEFAULT_BYTE = b;
	
	private static short s;
	public static Short DEFAULT_SHORT = s;
	
	private static int i;
	public static Integer DEFAULT_INT = i;
	
	private static long l;
	public static Long DEFAULT_LONG = l;
	
	private static float f;
	public static Float DEFAULT_FLOAT = f;
	
	private static double d;
	public static Double DEFAULT_DOUBLE = d;
	
	private static boolean bool;
	public static Boolean DEFAULT_BOOLEAN = bool;
	
}
