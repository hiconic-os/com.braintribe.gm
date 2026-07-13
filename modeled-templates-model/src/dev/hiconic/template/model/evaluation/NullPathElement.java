package dev.hiconic.template.model.evaluation;

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import dev.hiconic.template.model.parse.TextRange;

public interface NullPathElement extends PathEvaluationError {
	EntityType<NullPathElement> T = EntityTypes.T(NullPathElement.class);

	static NullPathElement create(String path, String segment, String operation) {
		NullPathElement reason = T.create();
		reason.setPath(path);
		reason.setSegment(segment);
		reason.setOperation(operation);
		reason.setText("Cannot " + operation + " path '" + path + "': receiver of '" + segment + "' is null");
		return reason;
	}

	static NullPathElement create(String path, String segment, String operation, TextRange range) {
		NullPathElement reason = create(path, segment, operation);
		reason.setRange(range);
		return reason;
	}
}
