// ============================================================================
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
// ============================================================================
package com.braintribe.codec.marshaller.yaml;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.UncheckedIOException;
import java.util.LinkedList;
import java.util.List;

import com.braintribe.model.bvd.string.Concatenation;
import com.braintribe.model.generic.value.Variable;

// Copied from value-descriptor-evaluator to avoid the dependency
/* package */ class TemplateStringParser {
	interface RuleContext {
		void activateRule(Rule rule);
		void activateRuleReplayLastCharacter(Rule rule);
		void appendElement(Object el);
		int pos();
	}
	
	interface Rule {
		void accept(char c, RuleContext context);
		void end(RuleContext context);
	}
	
	private static class TextRule implements Rule {
		private StringBuilder builder = new StringBuilder();
		private boolean escape;

		@Override
		public void accept(char c, RuleContext context) {
			if (escape) {
				switch (c) {
				
				case '$':
					builder.append('$');
					escape = false;
					break;
					
				case '{':
					escape = false;
					context.activateRule(new BracedVarRule());
					break;
					
				default:
					escape = false;
					if (VarRule.isVarNameLetter(c)) {
						context.activateRuleReplayLastCharacter(new VarRule());
					}
					else {
						builder.append('$');
						builder.append(c);
					}
					break;
				}	
			}
			else {
				if (c == '$')
					escape = true;
				else
					builder.append(c);
			}
		}

		@Override
		public void end(RuleContext context) {
			if (escape)
				throw new IllegalStateException("unexpected end of text at pos " + context.pos());
			
			if (builder.length() > 0)
				context.appendElement(builder.toString());
		}
		
	}
	
	private static class BracedVarRule implements Rule {
		private StringBuilder builder = new StringBuilder();
		private boolean closed;
		
		@Override
		public void accept(char c, RuleContext context) {
			if (c == '}') {
				closed = true;
				context.activateRule(new TextRule());
			}
			else {
				builder.append(c);
			}
		}
		
		@Override
		public void end(RuleContext context) {
			if (!closed || builder.length() == 0)
				throw new IllegalStateException("unexpected end of variable at pos " + context.pos());
		
			Variable variable = Variable.T.create();
			variable.setTypeSignature("string");
			variable.setName(builder.toString());
			context.appendElement(variable);
		}
	}
	
	private static class VarRule implements Rule {
		private StringBuilder builder = new StringBuilder();
		
		@Override
		public void accept(char c, RuleContext context) {
			if (isVarNameLetter(c)) {
				builder.append(c);
			}
			else {
				context.activateRuleReplayLastCharacter(new TextRule());
			}
		}

		public static boolean isVarNameLetter(char c) {
			return c == '_'  || Character.isLetterOrDigit(c);
		}
		
		@Override
		public void end(RuleContext context) {
			if (builder.length() == 0)
				throw new IllegalStateException("unexpected end of variable at pos " + context.pos());
		
			Variable variable = Variable.T.create();
			variable.setTypeSignature("string");
			variable.setName(builder.toString());
			context.appendElement(variable);
		}
		
	}
	
	private static class RuleContextImpl implements RuleContext {
		private Rule rule = new TextRule();
		private int pos;
		private Reader reader;
		private char lastChar;
		private List<Object> elements = new LinkedList<>();

		public RuleContextImpl(Reader reader) {
			this.reader = reader;
		}
		
		public Object parse() {
			try {
				int res;
				
				while ((res = reader.read()) != -1) {
					lastChar = (char) res;
					pos++;
					this.rule.accept(lastChar, this);
				}
				
				this.rule.end(this);
				
				switch (elements.size()) {
				case 0:
					return "";
				case 1:
					return elements.get(0);
				default:
					Concatenation concatenation = Concatenation.T.create();
					concatenation.getOperands().addAll(elements);
					return concatenation;
				}
			} catch (IOException e) {
				throw new UncheckedIOException(e);
			}
		}
		
		@Override
		public void activateRule(Rule rule) {
			this.rule.end(this);
			this.rule = rule;
		}
		
		@Override
		public void activateRuleReplayLastCharacter(Rule rule) {
			activateRule(rule);
			this.rule.accept(lastChar, this);
		}
		

		@Override
		public void appendElement(Object el) {
			elements.add(el);
		}

		@Override
		public int pos() {
			return pos;
		}

	}

	public static Object parse(String template) {
		RuleContextImpl ruleContext = new RuleContextImpl(new StringReader(template));
		return ruleContext.parse();
	}
}
