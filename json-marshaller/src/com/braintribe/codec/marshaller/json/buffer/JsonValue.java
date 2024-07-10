package com.braintribe.codec.marshaller.json.buffer;

import com.braintribe.gm.model.reason.Reasons;
import com.braintribe.gm.model.reason.essential.InvalidArgument;
import com.braintribe.model.generic.reflection.EssentialTypes;
import com.braintribe.model.generic.reflection.GenericModelType;
import com.braintribe.utils.StringTools;
import com.fasterxml.jackson.core.JsonLocation;

public abstract class JsonValue {
	protected JsonLocation start;
	protected JsonLocation end;
	protected GenericModelType inferredType;
	protected ConversionContext conversionContext;
	
	public JsonValue(ConversionContext context, JsonLocation start) {
		this.start = start;
		this.conversionContext = context;
	}
	
	public void inferType(GenericModelType type) {
		this.inferredType = type;
	}
	
	public boolean isString() {
		return false;
	}
	
	public boolean isScalar() {
		return false;
	}
	
	public abstract Object as(GenericModelType inferredType) throws ConversionError;
	
	public String asString() throws ConversionError {
		return (String)as(EssentialTypes.TYPE_STRING);
	}
	
	public JsonLocation getStart() {
		return start;
	}
	
	protected String getErrorLocation() {
		if (end != null) {
			return new JsonSpan(start, end).toString();
		}
		
		return JsonLocations.toString(start);
	}
	
	protected ConversionError conversionError(GenericModelType expectedType, GenericModelType actualType) {
		String msg = "Cannot convert type [" + actualType.getTypeSignature() + "] to type [" + expectedType.getTypeSignature() + "] " + getErrorLocation(); 
		InvalidArgument invalidArgument = Reasons.build(InvalidArgument.T).text(msg).toReason();
		return new ConversionError(invalidArgument);
	}
	
	protected ConversionError conversionError(GenericModelType expectedType, GenericModelType actualType, Object value) {
		String safeValue = StringTools.truncateIfRequired(String.valueOf(value), 20, true);
		String msg = "Cannot convert value [" + safeValue + "] of type [" + actualType.getTypeSignature() + "] to type [" + expectedType.getTypeSignature() + "] " + getErrorLocation(); 
		InvalidArgument invalidArgument = Reasons.build(InvalidArgument.T).text(msg).toReason();
		return new ConversionError(invalidArgument);
	}
	
	protected ConversionError conversionError(Exception e) {
		String msg = e.getMessage() + " " + getErrorLocation(); 
		InvalidArgument invalidArgument = Reasons.build(InvalidArgument.T).text(msg).toReason();
		return new ConversionError(invalidArgument);
	}

}
