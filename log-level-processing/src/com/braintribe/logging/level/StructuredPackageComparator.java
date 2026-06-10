package com.braintribe.logging.level;

import java.util.Comparator;

public class StructuredPackageComparator implements Comparator<String> {
        @Override
        public int compare(String a, String b) {
            // Root logger (empty string) always sorts first
            if (a.isEmpty()) return b.isEmpty() ? 0 : -1;
            if (b.isEmpty()) return 1;

            String[] partsA = a.split("\\.");
            String[] partsB = b.split("\\.");

            int len = Math.min(partsA.length, partsB.length);
            for (int i = 0; i < len; i++) {
                int cmp = partsA[i].compareTo(partsB[i]);
                if (cmp != 0) {
                    return cmp;
                }
            }
            // Shorter package comes first (shallower hierarchy)
            return Integer.compare(partsA.length, partsB.length);
        }
    }