package dev.hiconic.template.impl.node;

/**
 * Internal non-error control-flow signal. It must only be consumed by evaluators
 * whose model type declares the matching flow-control capability.
 */
final class FlowControlSignal extends RuntimeException {
	private static final long serialVersionUID = 1L;

	enum Kind { BREAK, CONTINUE }

	private final Kind kind;

	private FlowControlSignal(Kind kind) {
		super(kind.name(), null, false, false);
		this.kind = kind;
	}

	Kind kind() {
		return kind;
	}

	static FlowControlSignal breakSignal() {
		return new FlowControlSignal(Kind.BREAK);
	}

	static FlowControlSignal continueSignal() {
		return new FlowControlSignal(Kind.CONTINUE);
	}
}
