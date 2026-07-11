package dev.hiconic.template.impl.vd;

import dev.hiconic.template.model.core.vd.Ge;

public class GeEvaluator extends AbstractOrderedComparisonEvaluator<Ge> {
	@Override
	protected boolean test(int comparison) {
		return comparison >= 0;
	}
}
