package dev.hiconic.template.model.core.vd;

import java.util.List;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.generic.value.ValueDescriptor;
import dev.hiconic.template.model.core.TypeReference;
import dev.hiconic.template.model.core.path.PathAccess;
import dev.hiconic.template.model.parse.TextRange;

public interface TemplatePropertyPath extends ValueDescriptor {
	EntityType<TemplatePropertyPath> T = EntityTypes.T(TemplatePropertyPath.class);
	ValueDescriptor getRoot();
	void setRoot(ValueDescriptor root);
	List<PathAccess> getAccesses();
	void setAccesses(List<PathAccess> accesses);
	TypeReference getType();
	void setType(TypeReference type);
	TextRange getSourceRange();
	void setSourceRange(TextRange sourceRange);
}
