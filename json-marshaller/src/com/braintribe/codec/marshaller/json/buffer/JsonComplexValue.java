package com.braintribe.codec.marshaller.json.buffer;

import com.fasterxml.jackson.core.JsonLocation;

public abstract class JsonComplexValue extends JsonValue {
	public JsonComplexValue(ConversionContext context, JsonLocation start) {
		super(context, start);
	}
	
	/**
	 * 
	 * @param name is only non-null in case of object parsing and not in case of array parse
	 * @param value
	 */
	public abstract void addValue(JsonName name, JsonValue value);
	
	public abstract void onEnd();
}
