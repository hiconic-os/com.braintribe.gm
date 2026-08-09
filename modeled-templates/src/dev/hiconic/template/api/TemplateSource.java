package dev.hiconic.template.api;

import java.util.List;

import dev.hiconic.template.model.core.SequenceNode;
import dev.hiconic.template.model.core.TemplateNode;
import dev.hiconic.template.model.parse.TextRange;

/**
 * Scanner/Source SPI consumed by {@code StandardTemplateParser}.
 *
 * <p>The parser itself is linear and model-driven: {@code parseSequence}/{@code predeclareSequence}
 * match blocks purely by reading order and depth over {@code TemplateNode}s and markers. Only the
 * primitives that are bound to the concrete source representation are isolated here, in four groups:
 *
 * <ol>
 *   <li><b>Marker detection</b> &mdash; {@link #findNextMarker(int)} locates the next sigil,
 *       {@link #scanMarker(int)} describes the construct at that position (sigil, raw content,
 *       ranges).</li>
 *   <li><b>Static-span materialization</b> &mdash; {@link #materializeStatic(List, int, int)} turns
 *       the inert text between markers into node(s) (a {@code TextNode} in the string mode).</li>
 *   <li><b>Whitespace and indentation policy</b> &mdash;
 *       {@link #applyWhitespaceBefore(List, TemplateNode)},
 *       {@link #applyWhitespaceAfter(TemplateNode)},
 *       {@link #trimSilentBlockBoundaries(SequenceNode)},
 *       {@link #trimBlockEndBoundary(SequenceNode)},
 *       {@link #lineIndentAt(int)}, {@link #stripBlockBodyIndent(SequenceNode, String)}.</li>
 *   <li><b>Position/range</b> &mdash; {@link #range(int, int)}, {@link #location(TextRange)},
 *       {@link #fragment(TextRange)}, {@link #length()}.</li>
 * </ol>
 *
 * <p>All scanning methods are pure functions of the given position, so the same source serves both
 * the parser's consuming cursor (in {@code parseSequence}) and the non-consuming look-ahead used by
 * {@code predeclareSequence}. Positions are opaque {@code int} cursors; in the string mode they are
 * character offsets. A document-oriented implementation (later) maps them to structural anchors.
 * {@link StringTemplateSource} reproduces the historic string behavior exactly.
 */
public interface TemplateSource {

	/** Total length of the source in cursor units (character count in the string mode). */
	int length();

	/**
	 * Index of the next marker sigil at or after {@code from}, or {@code -1} if none. Escaped
	 * openers are skipped.
	 */
	int findNextMarker(int from);

	/**
	 * Describes the marker construct that starts at {@code markerStart} (as returned by
	 * {@link #findNextMarker(int)}). Never consumes; the caller advances its own cursor using
	 * {@link MarkerToken#constructEnd()}. If the construct is unterminated,
	 * {@link MarkerToken#terminated()} is {@code false} and only {@link MarkerToken#sigil()} and
	 * {@link MarkerToken#constructRange()} (spanning to the source end) are meaningful.
	 */
	MarkerToken scanMarker(int markerStart);

	/** A resolved text range between two cursor positions. */
	TextRange range(int start, int end);

	/** Human-readable "line L, column C" for the start of {@code range}. */
	String location(TextRange range);

	/** A bounded source excerpt for the given range, used in diagnostics. */
	String fragment(TextRange range);

	/**
	 * Materializes the inert span {@code [start, end)} into the given node list, applying any
	 * pending leading-whitespace policy. A no-op for an empty span.
	 */
	void materializeStatic(List<TemplateNode> nodes, int start, int end);

	/** Applies the node's leading whitespace policy to the preceding materialized node, if any. */
	void applyWhitespaceBefore(List<TemplateNode> nodes, TemplateNode node);

	/** Records the node's trailing whitespace policy so the next static span honors it. */
	void applyWhitespaceAfter(TemplateNode node);

	/** Trims the leading and trailing boundaries of a silent block's body. */
	void trimSilentBlockBoundaries(SequenceNode sequence);

	/** Trims the trailing boundary of a block body against its closing marker's line. */
	void trimBlockEndBoundary(SequenceNode sequence);

	/**
	 * The leading indentation of the line containing {@code offset}, or the empty string if the
	 * construct is not the first non-whitespace on its line.
	 */
	String lineIndentAt(int offset);

	/** Removes the given block indentation from the start of each line in the body's text nodes. */
	void stripBlockBodyIndent(SequenceNode sequence, String indent);

	/**
	 * A scanned marker construct in reading order: its sigil, the raw (untrimmed) body text, the
	 * range of the whole construct and of its content, the cursor position just past the closer,
	 * and whether a matching closer was found.
	 */
	record MarkerToken(char sigil, String rawContent, TextRange constructRange, TextRange contentRange,
			int constructEnd, boolean terminated) {
	}
}
