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
package com.braintribe.model.query;

import com.braintribe.model.generic.base.EnumBase;
import com.braintribe.model.generic.reflection.EnumType;
import com.braintribe.model.generic.reflection.EnumTypes;

import java.util.HashMap;

/**
* Defines the type of the <code>operator</code> applied in comparisons.
*/
public enum Operator implements EnumBase {
	equal,
	notEqual,
	like,
	ilike,
	greater,
	greaterOrEqual,
	less,
	lessOrEqual,
	in,
	contains;
	
	public static final EnumType<Operator> T = EnumTypes.T(Operator.class);
	
	@Override
	public EnumType<Operator> type() {
		return T;
	}

	public static String[] getOperatorSigns(){
		return new String[]{
			"=",
			"!=",
			"like",
			"ilike",
			">",
			">=",
			"<",
			"<=",
			"in"
		};
	}
	
	public static String[] getCollectionsOperatorSigns(){
		return new String[]{
			"=",
			"!=",
			"contains"
		};
	}
	
	private static HashMap<String, Operator> signToOperator = new HashMap<String, Operator>();
	
	static{
		signToOperator.put("=", Operator.equal);
		signToOperator.put("!=", Operator.notEqual);
		signToOperator.put("like", Operator.like);
		signToOperator.put("ilike", Operator.ilike);
		signToOperator.put(">", Operator.greater);
		signToOperator.put(">=", Operator.greaterOrEqual);
		signToOperator.put("<", Operator.less);
		signToOperator.put("<=", Operator.lessOrEqual);
		signToOperator.put("in", Operator.in);
		signToOperator.put("contains", Operator.contains);
	}
	
 	public static String getSignToOperator(Operator op){
		for(String operatorSign : signToOperator.keySet()){
			if(signToOperator.get(operatorSign) == op)
				return operatorSign;
		}
		return "";
	}
	
	public static Operator getOperatorToSign(String sign){
		return signToOperator.get(sign);
	}

}


