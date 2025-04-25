package com.braintribe.codec.marshaller.json.buffer;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.braintribe.gm.model.reason.Reasons;
import com.braintribe.gm.model.reason.essential.NotFound;
import com.braintribe.model.bvd.convert.Convert;
import com.braintribe.model.bvd.convert.ToBoolean;
import com.braintribe.model.bvd.convert.ToDate;
import com.braintribe.model.bvd.convert.ToDecimal;
import com.braintribe.model.bvd.convert.ToDouble;
import com.braintribe.model.bvd.convert.ToEnum;
import com.braintribe.model.bvd.convert.ToFloat;
import com.braintribe.model.bvd.convert.ToInteger;
import com.braintribe.model.bvd.convert.ToLong;
import com.braintribe.model.bvd.convert.ToString;
import com.braintribe.model.bvd.string.Concatenation;
import com.braintribe.model.generic.GMF;
import com.braintribe.model.generic.reflection.EnumType;
import com.braintribe.model.generic.reflection.EssentialTypes;
import com.braintribe.model.generic.reflection.GenericModelType;
import com.braintribe.model.generic.reflection.TypeCode;
import com.braintribe.model.generic.reflection.VdHolder;
import com.braintribe.model.generic.template.Template;
import com.braintribe.model.generic.template.TemplateFragment;
import com.braintribe.model.generic.value.ValueDescriptor;
import com.braintribe.model.generic.value.Variable;
import com.fasterxml.jackson.core.JsonLocation;

public abstract class AbstractJsonScalarValue extends JsonValue {
	
	public AbstractJsonScalarValue(ConversionContext conversionContext, JsonLocation start, JsonLocation end) {
		super(conversionContext, start);
		this.end = end;
	}
	
	public abstract GenericModelType getType();
	
	public abstract Object getValue();
	
	@Override
	public abstract boolean isString();
	
	@Override
	public boolean isScalar() {
		return true;
	}
	
	public JsonSpan getSpan() {
		return new JsonSpan(start, end);
	}
	
	private ValueDescriptor buildConversion(GenericModelType inferredType, Object value) throws ConversionError {
		TypeCode typeCode = inferredType.getTypeCode();
		
		final Convert convert;
		
		switch (typeCode) {
			case stringType: convert = ToString.T.create(); break;
			case dateType: convert = ToDate.T.create(); break;
			case enumType: convert = ToEnum.T.create(); break;
			case booleanType: convert = ToBoolean.T.create(); break;
			case decimalType: convert = ToDecimal.T.create(); break;
			case doubleType: convert = ToDouble.T.create(); break;
			case floatType: convert = ToFloat.T.create(); break;
			case integerType: convert = ToInteger.T.create(); break;
			case longType: convert = ToLong.T.create(); break;
			default:
				throw conversionError(inferredType, GMF.getTypeReflection().getType(value), value);
		}
		
		convert.setOperand(value);
		return convert;
	}
	
	private Object parseTemplate(String expression) throws ConversionError {
		try {
			Template template = Template.parse(expression);
			
			List<TemplateFragment> fragments = template.fragments();
			
			if (fragments.size() == 1) {
				return templateFragmentToEvaluable(fragments.get(0));
			}
			
			Concatenation concatenation = Concatenation.T.create();
			List<Object> operands = concatenation.getOperands();
			
			for (TemplateFragment fragment: fragments) {
				operands.add(templateFragmentToEvaluable(fragment));
			}
			
			return concatenation;
		}
		catch (IllegalArgumentException e) {
			throw conversionError(e);
		}
	}
	
	private Object templateFragmentToEvaluable(TemplateFragment templateFragment) {
		if (templateFragment.isPlaceholder()) {
			Variable var = Variable.T.create();
			var.setName(templateFragment.getText());
			var.setTypeSignature(EssentialTypes.TYPE_STRING.getTypeName());;
			return var;
		}
		else {
			return templateFragment.getText();
		}
	}
	
	@Override
	public Object as(GenericModelType inferredType) throws ConversionError {
		Object value = getValue();
		GenericModelType type = getType();

		// if null conversion is always possible
		if (value == null)
			return null;
		
		if (isString() && conversionContext.supportPlaceholders()) {
			String expression = (String)value;
			Object parsedValue = parseTemplate(expression);
			
			if (parsedValue.getClass() == String.class) {
				value = parsedValue;
			}
			else {
				ValueDescriptor vd = buildConversion(inferredType, (ValueDescriptor)parsedValue);
				return VdHolder.newInstance(vd);
			}
		}
		
		// if type is identical, no conversion is required
		if (inferredType == type)
			return value;
		
		switch (inferredType.getTypeCode()) {
			// type object allows for any value
			case objectType: return value;

			// allowed conversions
			case dateType: return asDate();
			case decimalType: return asDecimal();
			case doubleType: return asDouble();
			case floatType: return asFloat();
			case longType: return asLong();
			case enumType: return asEnum((EnumType<?>)inferredType);
			
			// no possible alternatives than the actual type
			case booleanType: 
			case integerType:
			case stringType: 
			case entityType:
			case listType:
			case mapType:
			case setType:
				throw conversionError(inferredType);
		}
		return null;
	}
	
	private ConversionError conversionError(GenericModelType inferredType) {
		return conversionError(inferredType, getType(), getValue());
	}
	
	private Date asDate() throws ConversionError {
		GenericModelType type = getType();
		Object value = getValue();
		
		switch (type.getTypeCode()) {
			case integerType:
			case longType:
				return new Date(((Number)value).longValue());
			case stringType:
				try {
					return conversionContext.getDateCoding().decode((String)value);
				}
				catch (IllegalArgumentException e) {
					throw conversionError(e);
				}
			default:
				throw conversionError(EssentialTypes.TYPE_DATE);
		}
	}

	public long asLong() throws ConversionError {
		Object value = getValue();
		
		switch (getType().getTypeCode()) {
			case integerType:
				return ((Number)value).longValue();
			case stringType:
				try {
					return Long.parseLong((String)value);
				}
				catch (NumberFormatException e) {
					throw conversionError(e);
				}
			default:
				throw conversionError(EssentialTypes.TYPE_LONG);
		}
	}

	public float asFloat() throws ConversionError {
		switch (getType().getTypeCode()) {
			case integerType:
			case doubleType:
				return ((Number)getValue()).floatValue();
			default:
				throw conversionError(EssentialTypes.TYPE_FLOAT);
		}
	}

	public double asDouble() throws ConversionError {
		switch (getType().getTypeCode()) {
			case longType:
			case floatType:
			case integerType:
				return ((Number)getValue()).doubleValue();
			default:
				throw conversionError(EssentialTypes.TYPE_DOUBLE);
		}
	}

	public BigDecimal asDecimal() throws ConversionError {
		Object value = getValue();
		switch (getType().getTypeCode()) {
			case longType:
				return new BigDecimal((Long)value);
			case integerType:
				return new BigDecimal((Integer)value);
			case floatType:
				return BigDecimal.valueOf((Float)value);
			case doubleType:
				return BigDecimal.valueOf((Double)value);
			case stringType:
				return new BigDecimal((String)value);
			default:
				throw conversionError(EssentialTypes.TYPE_DECIMAL);
		}
	}

	public Enum<? extends Enum<?>> asEnum(EnumType<?> enumType) throws ConversionError {
		String constantName = asString();
		
		Enum<? extends Enum<?>> enumValue = enumType.findEnumValue(constantName);
		
		if (enumValue != null)
			return enumValue;

		String msg = "Unknown enum constant [" + constantName + "] in enum type " + enumType.getTypeSignature() + " " + getSpan();
		NotFound notFound = Reasons.build(NotFound.T).text(msg).toReason();
		
		throw new ConversionError(notFound);
	}
}
