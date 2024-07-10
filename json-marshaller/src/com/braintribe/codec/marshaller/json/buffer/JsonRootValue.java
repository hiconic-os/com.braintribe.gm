package com.braintribe.codec.marshaller.json.buffer;

import com.braintribe.model.generic.reflection.GenericModelType;
import com.fasterxml.jackson.core.JsonLocation;

public class JsonRootValue extends JsonComplexValue {
	private JsonValue value;

	public JsonRootValue(ConversionContext context, GenericModelType inferredType, JsonLocation start) {
		super(context, start);
		this.inferredType = inferredType;
	}
	
	@Override
	public void addValue(JsonName name, JsonValue value) {
		if (this.value != null)
			throw new IllegalStateException("Unexpected number of root values");
		
		value.inferType(inferredType);
		this.value = value;
	}

	@Override
	public void onEnd() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Object as(GenericModelType inferredType) throws ConversionError {
		return value.as(inferredType);
	}

}
