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


public class PrettyJavaScriptPrototypes implements JavaScriptPrototypes {
	
	private CallPrototype parseDecmial = new StandardCallPrototype("parseDecimal", 1);
	private CallPrototype dateFromLong = new StandardCallPrototype("dateFromLong", 1);
	private CallPrototype parseLongBox = new StandardCallPrototype("parseLongBox", 1);
	
	private CallPrototype list = new StandardCallPrototype("list", 1);
	private CallPrototype set = new StandardCallPrototype("set", 1);
	private CallPrototype map = new StandardCallPrototype("map", 1);
	
	private CallPrototype create = new StandardCallPrototype("create", 1);
	
	private CallPrototype boxFloat = new StandardCallPrototype("boxFloat", 1);
	private CallPrototype boxDouble = new StandardCallPrototype("boxDouble", 1);
	private CallPrototype boxBoolean = new StandardCallPrototype("boxBoolean", 1);
	private CallPrototype boxInteger = new StandardCallPrototype("boxInteger", 1);
	private CallPrototype boxLong = new StandardCallPrototype("boxLong", 1);
	
	private CallPrototype parseLong = new StandardCallPrototype("parseLong", 1);
	
	private CallPrototype resolveType = new StandardCallPrototype("resolveType", 2);
	private CallPrototype typeReflection = new StandardCallPrototype("typeReflection", 0);
	
	private CallPrototype resolveProperty = new StandardCallPrototype("resolveProperty", 2);
	
	private CallPrototype setValue = new StandardCallPrototype("setValue", 3);
	private CallPrototype setAbsence = new StandardCallPrototype("setAbsence", 3);
	
	private CallPrototype parseEnum = new StandardCallPrototype("parseEnum", 2);
	
	@Override
	public CallPrototype parseDecimal() { return parseDecmial; }
	@Override
	public CallPrototype dateFromLong() { return dateFromLong; }
	@Override
	public CallPrototype boxFloat() { return boxFloat; }
	@Override
	public CallPrototype boxDouble() { return boxDouble; }
	@Override
	public CallPrototype boxBoolean() { return boxBoolean; }
	@Override
	public CallPrototype boxInteger() { return boxInteger; }
	@Override
	public CallPrototype boxLong() { return boxLong; }
	@Override
	public CallPrototype parseLong() { return parseLong; }
	@Override
	public CallPrototype parseLongBox() { return parseLongBox; }
	@Override
	public CallPrototype resolveType() { return resolveType; }
	@Override
	public CallPrototype typeReflection() { return typeReflection; }
	@Override
	public CallPrototype resolveProperty() { return resolveProperty; }
	@Override
	public CallPrototype setValue() { return setValue; }
	@Override
	public CallPrototype setAbsence() { return setAbsence; }
	@Override
	public CallPrototype create() { return create; }
	@Override
	public CallPrototype parseEnum() { return parseEnum; }
	@Override
	public CallPrototype list() { return list; }
	@Override
	public CallPrototype set() { return set; }
	@Override
	public CallPrototype map() { return map; }
}
