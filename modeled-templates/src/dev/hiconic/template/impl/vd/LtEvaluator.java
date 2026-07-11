package dev.hiconic.template.impl.vd;

import dev.hiconic.template.model.core.vd.Lt;

public class LtEvaluator extends AbstractOrderedComparisonEvaluator<Lt> {
	@Override
	protected boolean test(int comparison) {
		return comparison < 0;
	}
}
