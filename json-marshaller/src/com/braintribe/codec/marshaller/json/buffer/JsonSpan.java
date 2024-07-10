package com.braintribe.codec.marshaller.json.buffer;

import com.fasterxml.jackson.core.JsonLocation;

public class JsonSpan {
	private JsonLocation start; 
	private JsonLocation end;
	
	
	public JsonSpan(JsonLocation start, JsonLocation end) {
		this.start = start;
		this.end = end;
	}
	
	public JsonLocation start() {
		return start;
	}
	
	public JsonLocation end() {
		return end;
	}

	@Override
	public String toString() {
		JsonLocation loc1 = start();
		JsonLocation loc2 = end();
		int l1 = loc1.getLineNr();
		int c1 = loc1.getColumnNr();
		int l2 = loc2.getLineNr();
		int c2 = loc2.getColumnNr();
		
		if (l1 == l2)
			return "(line: " + l1+", pos: "+c1+"-"+c2+")";
		else
			return "(line: " + l1+", pos: "+c1+" to line: " + l2 + ", pos: " + c2 +")";

	}
}