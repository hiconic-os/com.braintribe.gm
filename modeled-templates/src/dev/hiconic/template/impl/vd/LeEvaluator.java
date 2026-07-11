package dev.hiconic.template.impl.vd;

import dev.hiconic.template.model.core.vd.Le;

public class LeEvaluator extends AbstractOrderedComparisonEvaluator<Le> {
	@Override
	protected boolean test(int comparison) {
		return comparison <= 0;
	}
}
