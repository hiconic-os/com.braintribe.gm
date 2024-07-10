package com.braintribe.codec.marshaller.json.buffer;

public class JsonField {
	public final JsonName name;
	public final JsonValue value;
	public final boolean property;
	
	public JsonField(JsonName name, JsonValue value, boolean property) {
		this.name = name;
		this.value = value;
		this.property = property;
	}
}
