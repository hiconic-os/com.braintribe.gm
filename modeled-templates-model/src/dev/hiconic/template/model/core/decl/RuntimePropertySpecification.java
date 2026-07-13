package dev.hiconic.template.model.core.decl;

import java.util.List;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.generic.reflection.PropertyLiteral;

public interface RuntimePropertySpecification extends GenericEntity {
	EntityType<RuntimePropertySpecification> T = EntityTypes.T(RuntimePropertySpecification.class);

	PropertyLiteral name = PropertyLiteral.of(T, "name");
	PropertyLiteral typeSignature = PropertyLiteral.of(T, "typeSignature");
	PropertyLiteral positionalIndex = PropertyLiteral.of(T, "positionalIndex");
	PropertyLiteral required = PropertyLiteral.of(T, "required");
	PropertyLiteral defaultValue = PropertyLiteral.of(T, "defaultValue");
	PropertyLiteral metaData = PropertyLiteral.of(T, "metaData");

	String getName();
	void setName(String name);

	String getTypeSignature();
	void setTypeSignature(String typeSignature);

	Integer getPositionalIndex();
	void setPositionalIndex(Integer positionalIndex);

	boolean getRequired();
	void setRequired(boolean required);

	Object getDefaultValue();
	void setDefaultValue(Object defaultValue);

	List<GenericEntity> getMetaData();
	void setMetaData(List<GenericEntity> metaData);
}
