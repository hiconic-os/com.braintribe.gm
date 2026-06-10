package com.braintribe.logging.level;

import java.util.Comparator;

public class StructuredPackageComparator implements Comparator<String> {
	@Override
	public int compare(String a, String b) {
		if (LogLevelNames.ROOT.equals(a)) {
			return LogLevelNames.ROOT.equals(b) ? 0 : -1;
		}
		if (LogLevelNames.ROOT.equals(b)) {
			return 1;
		}

		String[] partsA = a.split("\\.");
		String[] partsB = b.split("\\.");

		int len = Math.min(partsA.length, partsB.length);
		for (int i = 0; i < len; i++) {
			int cmp = partsA[i].compareTo(partsB[i]);
			if (cmp != 0) {
				return cmp;
			}
		}

		return Integer.compare(partsA.length, partsB.length);
	}
}
