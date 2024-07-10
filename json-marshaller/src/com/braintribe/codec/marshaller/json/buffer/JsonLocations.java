package com.braintribe.codec.marshaller.json.buffer;

import com.fasterxml.jackson.core.JsonLocation;

public interface JsonLocations {
	static String toString(JsonLocation l) {
		return "(line: " + l.getLineNr()+", pos: "+l.getColumnNr()+")";
	}
}
