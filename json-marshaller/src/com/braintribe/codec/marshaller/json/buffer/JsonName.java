package com.braintribe.codec.marshaller.json.buffer;

import com.braintribe.model.generic.reflection.EssentialTypes;
import com.braintribe.model.generic.reflection.GenericModelType;
import com.fasterxml.jackson.core.JsonLocation;

// dateToPerson: map<Date, Person>
// { "name": "value" }

public class JsonName extends AbstractJsonScalarValue {
	private String value;
	
	public JsonName(ConversionContext conversionContext, String name, JsonLocation start, JsonLocation end) {
		super(conversionContext, start, end);
		value = name;
	}
	
	@Override
	public GenericModelType getType() {
		return EssentialTypes.TYPE_STRING;
	}
	
	@Override
	public String getValue() {
		return value;
	}
	
	@Override
	public boolean isString() {
		return true;
	}
	
	@Override
	public String asString() throws ConversionError {
		return value;
	}
}
