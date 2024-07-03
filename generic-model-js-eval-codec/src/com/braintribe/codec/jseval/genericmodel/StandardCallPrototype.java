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
package com.braintribe.codec.jseval.genericmodel;

import java.util.ArrayList;
import java.util.List;

import com.braintribe.codec.CodecException;

public class StandardCallPrototype implements CallPrototype {
	protected List<Evaluator> tokens = new ArrayList<Evaluator>();
	
	public StandardCallPrototype(String name, int argumentCount) {
		// function opening
		tokens.add(new StringEvaluator("_." + name + "("));
		
		for (int i = 0; i < argumentCount; i++) {
			if (i > 0)
				tokens.add(new StringEvaluator(","));
			tokens.add(new VariableEvaluator(i));
		}
		
		// function closing
		tokens.add(new StringEvaluator(")"));
	}
	
	protected static interface Evaluator {
		public void evaluate(StringBuilder builder, Object params[]) throws CodecException ;
	}
	
	protected static class StringEvaluator implements Evaluator {
		private String token;
		
		public StringEvaluator(String token) {
			this.token = token;
		}
		
		@Override
		public void evaluate(StringBuilder builder, Object[] params) throws CodecException {
			builder.append(token);
		}
		
		@Override
		public String toString() {
			return "str\"" + token + "\"";
		}
	}
	
	protected static class VariableEvaluator implements Evaluator {
		private int index;
		
		public VariableEvaluator(int index) {
			this.index = index;
		}
		
		@Override
		public void evaluate(StringBuilder builder, Object[] params) throws CodecException {
			Object param = params[index];
			if (param instanceof CodeWriter) {
				CodeWriter codeWriter = (CodeWriter)param;
				codeWriter.write(builder);
			}
			else
				builder.append(param.toString());
		}
		
		@Override
		public String toString() {
			return "var["+index+"]";
		}
	}
	
	/* (non-Javadoc)
	 * @see com.braintribe.gwt.genericmodel.client.codec.eval.CallPrototype#evaluate(java.lang.String)
	 */
	@Override
	public String evaluate(final String... params) throws CodecException {
		StringBuilder builder = new StringBuilder();
		for (Evaluator evaluator: tokens) {
			evaluator.evaluate(builder, params);
		}
		return builder.toString();
	}
	
	/* (non-Javadoc)
	 * @see com.braintribe.gwt.genericmodel.client.codec.eval.CallPrototype#evaluate(java.lang.StringBuilder, com.braintribe.gwt.genericmodel.client.codec.eval.CodeWriter)
	 */
	@Override
	public void evaluate(StringBuilder builder, Object... params) throws CodecException {
		for (Evaluator evaluator: tokens) {
			evaluator.evaluate(builder, params);
		}
	}
	
}
