package dev.hiconic.template.test.model;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.annotation.meta.Mandatory;
import com.braintribe.model.generic.annotation.meta.Max;
import com.braintribe.model.generic.annotation.meta.MaxLength;
import com.braintribe.model.generic.annotation.meta.Min;
import com.braintribe.model.generic.annotation.meta.MinLength;
import com.braintribe.model.generic.annotation.meta.Pattern;
import com.braintribe.model.generic.annotation.meta.PositionalArguments;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

@PositionalArguments({"code", "score"})
public interface ConstraintFixture extends GenericEntity {
	EntityType<ConstraintFixture> T = EntityTypes.T(ConstraintFixture.class);

	@Mandatory
	@Pattern("[A-Z]+")
	@MinLength(2)
	@MaxLength(4)
	String getCode();
	void setCode(String code);

	@Min("1")
	@Max(value = "10", exclusive = true)
	Integer getScore();
	void setScore(Integer score);

	@Pattern("[a-z]+")
	String getDynamic();
	void setDynamic(String dynamic);

	int getPrimitive();
	void setPrimitive(int primitive);
}
