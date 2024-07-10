package com.braintribe.codec.marshaller.json.buffer;

import com.braintribe.model.generic.reflection.EssentialTypes;
import com.braintribe.model.generic.reflection.GenericModelType;
import com.fasterxml.jackson.core.JsonLocation;

public class JsonScalarValue extends AbstractJsonScalarValue {
	private GenericModelType type;
	private Object value;
	
	public JsonScalarValue(ConversionContext conversionContext, GenericModelType type, Object value, JsonLocation start, JsonLocation end) {
		super(conversionContext, start, end);
		this.type = type;
		this.value = value;
		this.end = end;
	}
	
	@Override
	public GenericModelType getType() {
		return type;
	}
	
	@Override
	public Object getValue() {
		return value;
	}
	
	@Override
	public boolean isString() {
		return type == EssentialTypes.TYPE_STRING;
	}
}
