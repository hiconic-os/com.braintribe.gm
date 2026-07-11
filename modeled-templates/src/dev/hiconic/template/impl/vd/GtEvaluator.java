package dev.hiconic.template.impl.vd;

import dev.hiconic.template.model.core.vd.Gt;

public class GtEvaluator extends AbstractOrderedComparisonEvaluator<Gt> {
	@Override
	protected boolean test(int comparison) {
		return comparison > 0;
	}
}
