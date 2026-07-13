package dev.hiconic.template.model.evaluation;

import com.braintribe.gm.model.reason.Reason;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.generic.reflection.PropertyLiteral;
import dev.hiconic.template.model.parse.TextRange;

public interface PathEvaluationError extends Reason {
	EntityType<PathEvaluationError> T = EntityTypes.T(PathEvaluationError.class);

	PropertyLiteral path = PropertyLiteral.of(T, "path");
	PropertyLiteral segment = PropertyLiteral.of(T, "segment");
	PropertyLiteral operation = PropertyLiteral.of(T, "operation");
	PropertyLiteral range = PropertyLiteral.of(T, "range");

	String getPath();
	void setPath(String path);
	String getSegment();
	void setSegment(String segment);
	String getOperation();
	void setOperation(String operation);
	TextRange getRange();
	void setRange(TextRange range);

	static PathEvaluationError create(String path, String segment, String operation, String text) {
		PathEvaluationError reason = T.create();
		reason.setPath(path);
		reason.setSegment(segment);
		reason.setOperation(operation);
		reason.setText(text);
		return reason;
	}

	static PathEvaluationError create(String path, String segment, String operation, String text, TextRange range) {
		PathEvaluationError reason = create(path, segment, operation, text);
		reason.setRange(range);
		return reason;
	}
}
