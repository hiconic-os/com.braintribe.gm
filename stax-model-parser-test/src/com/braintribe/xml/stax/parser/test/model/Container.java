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
package com.braintribe.xml.stax.parser.test.model;

import java.util.List;
import java.util.Set;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

public interface Container extends GenericEntity{
	final EntityType<Container> T = EntityTypes.T(Container.class);
	
	void setStringValue( String value);
	String getStringValue();
	
	void setBooleanValue( boolean value);
	boolean getBooleanValue();
	
	void setIntegerValue( int value);
	int getIntegerValue();
	
	void setStringSet( Set<String> stringSet);
	Set<String> getStringSet();
	
	void setStringList( List<String> stringList);
	List<String> getStringList();
	
	void setProcessingInstruction( String value);
	String getProcessingInstruction();
	
	void setGrouping( Grouping grouping);
	Grouping getGrouping();
	
	/*
	void setProperties(Map<String,String> map);
	Map<String,String> getProperties();
	
	void setPropertiesL(List<String> list);	
	List<String> getPropertiesL();
	*/
	void setAutoValue( String autoValue);
	String getAutoValue();
	
	void setEntries( List<Entry> entries);
	List<Entry> getEntries();
	
}
