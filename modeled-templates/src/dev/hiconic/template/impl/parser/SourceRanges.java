package dev.hiconic.template.impl.parser;

import dev.hiconic.template.model.parse.TextPosition;
import dev.hiconic.template.model.parse.TextRange;

final class SourceRanges {
	private SourceRanges() {
	}

	static TextRange subRange(TextRange parent, String source, int start, int end) {
		if (parent == null) return null;
		TextRange range = TextRange.T.create();
		range.setStart(advance(parent.getStart(), source, start));
		range.setEnd(advance(parent.getStart(), source, end));
		return range;
	}

	private static TextPosition advance(TextPosition origin, String source, int count) {
		int line = origin.getLine();
		int column = origin.getColumn();
		for (int i = 0; i < count; i++) {
			if (source.charAt(i) == '\n') {
				line++;
				column = 1;
			} else column++;
		}
		TextPosition result = TextPosition.T.create();
		result.setOffset(origin.getOffset() + count);
		result.setLine(line);
		result.setColumn(column);
		return result;
	}
}
