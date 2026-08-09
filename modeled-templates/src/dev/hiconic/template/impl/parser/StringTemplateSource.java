package dev.hiconic.template.impl.parser;

import java.util.List;
import java.util.Objects;

import dev.hiconic.template.api.TemplateSource;
import dev.hiconic.template.model.core.SequenceNode;
import dev.hiconic.template.model.core.TemplateNode;
import dev.hiconic.template.model.core.TextNode;
import dev.hiconic.template.model.core.instr.BlockInstructionNode;
import dev.hiconic.template.model.core.instr.DirectiveNode;
import dev.hiconic.template.model.core.instr.InvokeInstruction;
import dev.hiconic.template.model.core.instr.SilentNode;
import dev.hiconic.template.model.core.instr.WhitespaceAction;
import dev.hiconic.template.model.core.instr.WhitespacePolicy;
import dev.hiconic.template.model.parse.TextPosition;
import dev.hiconic.template.model.parse.TextRange;

/**
 * String-backed {@link TemplateSource}: reproduces the historic character-offset scanning,
 * static-span materialization, whitespace/indentation handling and position indexing of the
 * standard template parser. Cursor positions are character offsets into the source string.
 */
public final class StringTemplateSource implements TemplateSource {
	private final String source;
	private final int[] lines;
	private final int[] columns;
	private WhitespaceAction pendingWhitespace = WhitespaceAction.preserve;

	public StringTemplateSource(String source) {
		this.source = Objects.requireNonNull(source, "source");
		this.lines = new int[source.length() + 1];
		this.columns = new int[source.length() + 1];
		indexPositions();
	}

	@Override
	public int length() {
		return source.length();
	}

	@Override
	public int findNextMarker(int from) {
		for (int i = from; i + 1 < source.length(); i++) {
			char ch = source.charAt(i);
			if (((ch == '$' || ch == '#') && source.charAt(i + 1) == '{'
					|| ch == '%' && source.charAt(i + 1) == '(') && !isEscaped(i))
				return i;
		}
		return -1;
	}

	@Override
	public MarkerToken scanMarker(int markerStart) {
		char sigil = source.charAt(markerStart);
		char closing = sigil == '%' ? ')' : '}';
		int close = findMarkerEnd(markerStart + 2, closing);
		if (close < 0)
			return new MarkerToken(sigil, null, range(markerStart, source.length()), null, source.length(), false);
		int constructEnd = close + 1;
		String rawContent = source.substring(markerStart + 2, close);
		TextRange constructRange = range(markerStart, constructEnd);
		TextRange contentRange = range(markerStart + 2, close);
		return new MarkerToken(sigil, rawContent, constructRange, contentRange, constructEnd, true);
	}

	@Override
	public void materializeStatic(List<TemplateNode> nodes, int start, int end) {
		if (start == end)
			return;
		String value = applyLeadingWhitespace(source.substring(start, end), pendingWhitespace);
		pendingWhitespace = WhitespaceAction.preserve;
		if (value.isEmpty())
			return;
		TextNode text = TextNode.T.create();
		text.setText(unescapeTemplateText(value));
		nodes.add(text);
	}

	@Override
	public void applyWhitespaceBefore(List<TemplateNode> nodes, TemplateNode node) {
		WhitespaceAction action = whitespace(node, true);
		if (action == WhitespaceAction.preserve || nodes.isEmpty())
			return;
		TemplateNode previous = nodes.get(nodes.size() - 1);
		if (!(previous instanceof TextNode text))
			return;
		String trimmed = applyTrailingWhitespace(text.getText(), action);
		if (trimmed.isEmpty()) nodes.remove(nodes.size() - 1); else text.setText(trimmed);
	}

	@Override
	public void applyWhitespaceAfter(TemplateNode node) {
		pendingWhitespace = whitespace(node, false);
	}

	@Override
	public void trimSilentBlockBoundaries(SequenceNode sequence) {
		List<TemplateNode> nodes = sequence.getNodes();
		if (nodes.isEmpty()) return;
		if (nodes.get(0) instanceof TextNode first) {
			String value = applyLeadingWhitespace(first.getText(), WhitespaceAction.trimLine);
			if (value.isEmpty()) nodes.remove(0); else first.setText(value);
		}
		if (!nodes.isEmpty() && nodes.get(nodes.size() - 1) instanceof TextNode last) {
			String value = applyTrailingWhitespace(last.getText(), WhitespaceAction.trimLine);
			if (value.isEmpty()) nodes.remove(nodes.size() - 1); else last.setText(value);
		}
	}

	@Override
	public void trimBlockEndBoundary(SequenceNode sequence) {
		List<TemplateNode> nodes = sequence.getNodes();
		if (nodes.isEmpty()) return;
		if (nodes.get(nodes.size() - 1) instanceof TextNode last) {
			String value = applyTrailingWhitespace(last.getText(), WhitespaceAction.trimLine);
			if (value.isEmpty()) nodes.remove(nodes.size() - 1); else last.setText(value);
		}
	}

	@Override
	public String lineIndentAt(int offset) {
		int lineStart = offset;
		while (lineStart > 0) {
			char ch = source.charAt(lineStart - 1);
			if (ch == '\n' || ch == '\r')
				break;
			lineStart--;
		}
		for (int i = lineStart; i < offset; i++) {
			char ch = source.charAt(i);
			if (ch != ' ' && ch != '\t')
				return "";
		}
		return source.substring(lineStart, offset);
	}

	@Override
	public void stripBlockBodyIndent(SequenceNode sequence, String indent) {
		if (indent == null || indent.isEmpty())
			return;
		for (TemplateNode node : sequence.getNodes()) {
			if (node instanceof TextNode text)
				text.setText(stripLineIndent(text.getText(), indent));
		}
	}

	@Override
	public TextRange range(int start, int end) {
		TextRange range = TextRange.T.create();
		range.setStart(position(start));
		range.setEnd(position(end));
		return range;
	}

	@Override
	public String location(TextRange range) {
		return "line " + range.getStart().getLine() + ", column " + range.getStart().getColumn();
	}

	@Override
	public String fragment(TextRange range) {
		int start = range.getStart().getOffset();
		int end = Math.min(range.getEnd().getOffset(), start + 160);
		return source.substring(start, end);
	}

	private int findMarkerEnd(int from, char closing) {
		char quote = 0;
		int nestedBraces = 0;
		int nestedBrackets = 0;
		int nestedParentheses = 0;
		for (int i = from; i < source.length(); i++) {
			char ch = source.charAt(i);
			if (quote != 0) {
				if (ch == quote && !isEscaped(i))
					quote = 0;
			} else if (isEscaped(i)) {
				continue;
			} else if (ch == '\'' || ch == '"') {
				quote = ch;
			} else if (ch == '{') {
				nestedBraces++;
			} else if (ch == '}') {
				if (closing == '}' && nestedBraces == 0 && nestedBrackets == 0 && nestedParentheses == 0)
					return i;
				if (nestedBraces > 0)
					nestedBraces--;
			} else if (ch == '[') {
				nestedBrackets++;
			} else if (ch == ']') {
				nestedBrackets = Math.max(0, nestedBrackets - 1);
			} else if (ch == '(') {
				nestedParentheses++;
			} else if (ch == ')') {
				if (closing == ')' && nestedParentheses == 0 && nestedBraces == 0 && nestedBrackets == 0)
					return i;
				nestedParentheses = Math.max(0, nestedParentheses - 1);
			}
		}
		return -1;
	}

	private boolean isEscaped(int index) {
		int backslashes = 0;
		for (int i = index - 1; i >= 0 && source.charAt(i) == '\\'; i--)
			backslashes++;
		return (backslashes & 1) == 1;
	}

	private WhitespaceAction whitespace(TemplateNode node, boolean before) {
		if (!(node instanceof DirectiveNode directive))
			return node instanceof SilentNode ? WhitespaceAction.trimLine : WhitespaceAction.preserve;
		WhitespacePolicy policy = directive.getWhitespace();
		if (policy == null && node instanceof InvokeInstruction)
			return WhitespaceAction.preserve;
		if (policy == null && node instanceof BlockInstructionNode)
			return before ? WhitespaceAction.trimLine : WhitespaceAction.preserve;
		if (policy == null)
			return WhitespaceAction.trimLine;
		WhitespaceAction action = before ? policy.getBefore() : policy.getAfter();
		return action == null ? WhitespaceAction.preserve : action;
	}

	private String applyLeadingWhitespace(String text, WhitespaceAction action) {
		if (action == WhitespaceAction.preserve) return text;
		int i = 0;
		if (action == WhitespaceAction.trim) {
			while (i < text.length() && Character.isWhitespace(text.charAt(i))) i++;
		} else {
			while (i < text.length() && (text.charAt(i) == ' ' || text.charAt(i) == '\t' || text.charAt(i) == '\r')) i++;
			if (i < text.length() && text.charAt(i) == '\n') i++; else return text;
		}
		return text.substring(i);
	}

	private String applyTrailingWhitespace(String text, WhitespaceAction action) {
		int i = text.length();
		if (action == WhitespaceAction.trim) {
			while (i > 0 && Character.isWhitespace(text.charAt(i - 1))) i--;
		} else {
			while (i > 0 && (text.charAt(i - 1) == ' ' || text.charAt(i - 1) == '\t' || text.charAt(i - 1) == '\r')) i--;
			if (i > 0 && text.charAt(i - 1) == '\n') {
				i--;
				if (i > 0 && text.charAt(i - 1) == '\r') i--;
			} else {
				return text;
			}
		}
		return text.substring(0, i);
	}

	private String stripLineIndent(String text, String indent) {
		StringBuilder result = null;
		boolean lineStart = true;
		int i = 0;
		while (i < text.length()) {
			if (lineStart && startsWith(text, i, indent)) {
				if (result == null) {
					result = new StringBuilder(text.length());
					result.append(text, 0, i);
				}
				i += indent.length();
				lineStart = false;
				continue;
			}
			char ch = text.charAt(i++);
			if (result != null)
				result.append(ch);
			lineStart = ch == '\n';
		}
		return result == null ? text : result.toString();
	}

	private boolean startsWith(String text, int index, String prefix) {
		if (index + prefix.length() > text.length())
			return false;
		for (int i = 0; i < prefix.length(); i++) {
			if (text.charAt(index + i) != prefix.charAt(i))
				return false;
		}
		return true;
	}

	private String unescapeTemplateText(String text) {
		StringBuilder result = null;
		for (int i = 0; i < text.length(); i++) {
			char ch = text.charAt(i);
			boolean escapedMarker = ch == '\\' && i + 2 < text.length()
					&& ((text.charAt(i + 1) == '$' || text.charAt(i + 1) == '#') && text.charAt(i + 2) == '{'
							|| text.charAt(i + 1) == '%' && text.charAt(i + 2) == '(');
			boolean escapedBackslash = ch == '\\' && i + 1 < text.length() && text.charAt(i + 1) == '\\';
			if (!escapedMarker && !escapedBackslash) {
				if (result != null)
					result.append(ch);
				continue;
			}
			if (result == null) {
				result = new StringBuilder(text.length());
				result.append(text, 0, i);
			}
			result.append(text.charAt(++i));
		}
		return result == null ? text : result.toString();
	}

	private TextPosition position(int at) {
		TextPosition position = TextPosition.T.create();
		position.setOffset(at);
		position.setLine(lines[at]);
		position.setColumn(columns[at]);
		return position;
	}

	private void indexPositions() {
		int line = 1;
		int column = 1;
		for (int i = 0; i <= source.length(); i++) {
			lines[i] = line;
			columns[i] = column;
			if (i < source.length()) {
				if (source.charAt(i) == '\n') {
					line++;
					column = 1;
				} else {
					column++;
				}
			}
		}
	}
}
