package dev.hiconic.template.model.core.path;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.annotation.Abstract;
import dev.hiconic.template.model.core.TypeReference;
import dev.hiconic.template.model.parse.TextRange;

@Abstract
public interface PathAccess extends GenericEntity {
	TypeReference getResultType();
	void setResultType(TypeReference resultType);
	String getSource();
	void setSource(String source);
	TextRange getSourceRange();
	void setSourceRange(TextRange sourceRange);
}
