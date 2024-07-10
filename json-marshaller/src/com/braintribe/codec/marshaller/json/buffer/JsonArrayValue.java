package com.braintribe.codec.marshaller.json.buffer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.braintribe.gm.model.reason.Reasons;
import com.braintribe.gm.model.reason.essential.InvalidArgument;
import com.braintribe.model.generic.collection.LinearCollectionBase;
import com.braintribe.model.generic.reflection.EssentialTypes;
import com.braintribe.model.generic.reflection.GenericModelType;
import com.braintribe.model.generic.reflection.LinearCollectionType;
import com.braintribe.model.generic.reflection.ListType;
import com.braintribe.model.generic.reflection.MapType;
import com.braintribe.model.generic.reflection.SetType;
import com.fasterxml.jackson.core.JsonLocation;

public class JsonArrayValue extends JsonComplexValue {
	public JsonArrayValue(ConversionContext conversionContext, JsonLocation start) {
		super(conversionContext, start);
	}
	
	private List<JsonValue> values = new ArrayList<>();
	
	@Override
	public void addValue(JsonName name, JsonValue value) {
		values.add(value);
	}
	
	@Override
	public void onEnd() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void inferType(GenericModelType type) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public Object as(GenericModelType inferredType) throws ConversionError {
		
		switch (inferredType.getTypeCode()) {
			case objectType: return asCollectionType(EssentialTypes.TYPE_LIST);
			case listType: return asCollectionType((ListType)inferredType);
			case setType: return asCollectionType((SetType)inferredType);
			case mapType: return asMap((MapType)inferredType);
				
			default: {
				String msg = "Array literal cannot be converted to type [" + inferredType.getTypeSignature() + "] " + getErrorLocation();
				InvalidArgument invalidArgument = Reasons.build(InvalidArgument.T).text(msg).toReason();
				throw new ConversionError(invalidArgument);
			}
		}
	}
	
	private Map<Object, Object> asMap(MapType mapType) throws ConversionError {
		Map<Object, Object> map = mapType.createPlain();
		
		GenericModelType keyType = mapType.getKeyType();
		GenericModelType valueType = mapType.getValueType();
		
		JsonValue keyJsonValue = null;
		Object key = null;
		
		for (JsonValue jsonValue: values) {
			if (keyJsonValue == null) {
				keyJsonValue = jsonValue;
				try {
					key = jsonValue.as(keyType);
				}
				catch (ConversionError e) {
					String msg = "Invalid map key " + JsonLocations.toString(jsonValue.getStart());
					InvalidArgument reason = Reasons.build(InvalidArgument.T).text(msg).toReason();
					throw new ConversionError(reason, e);
				}
			}
			else {
				keyJsonValue = null;
				try {
					Object value = jsonValue.as(valueType);
					map.put(key, value);
				}
				catch (ConversionError e) {
					String msg = "Invalid map value " + JsonLocations.toString(jsonValue.getStart());
					InvalidArgument reason = Reasons.build(InvalidArgument.T).text(msg).toReason();
					throw new ConversionError(reason, e);
				}
			}
		}
		
		if (keyJsonValue != null) {
			String msg = "Missing value for map key " + keyJsonValue.getErrorLocation();
			InvalidArgument reason = Reasons.build(InvalidArgument.T).text(msg).toReason();
			throw new ConversionError(reason);
		}
		
		return map;
	}
	
	private Collection<Object> asCollectionType(LinearCollectionType collectionType) throws ConversionError {
		LinearCollectionBase<Object> collection = collectionType.createPlain();
		GenericModelType elementType = collectionType.getCollectionElementType();
		for (JsonValue value: values) {
			try {
				Object element = value.as(elementType);
				collection.add(element);
			}
			catch (ConversionError e) {
				String msg = "Invalid " + collectionType.getTypeName() + " element " + JsonLocations.toString(value.getStart());
				InvalidArgument reason = Reasons.build(InvalidArgument.T).text(msg).toReason();
				throw new ConversionError(reason, e);
			}
		}
		
		return collection;
	}
}
