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
package com.braintribe.model.processing.service.impl.weaving.test.proto;


import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;

import com.braintribe.asm.Opcodes;

/*
import jdk.internal.org.objectweb.asm.ClassReader;
import jdk.internal.org.objectweb.asm.ClassVisitor;
import jdk.internal.org.objectweb.asm.util.ASMifier;
import jdk.internal.org.objectweb.asm.util.TraceClassVisitor;
*/

public class Classoutput implements Opcodes {
/*	public static void main(String[] args) {
		
		try {
			Class clazz = OpenUserSessionRequestServiceProcessor.class;
			InputStream inputStream = clazz.getResourceAsStream(clazz.getSimpleName() + ".class");

			ClassReader reader = new ClassReader(inputStream);

			ASMifier asMifier = new ASMifier();
			ClassVisitor visitor = new TraceClassVisitor(null, asMifier, new PrintWriter(System.out));
			reader.accept(visitor, ClassReader.EXPAND_FRAMES);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}*/
}
