package com.braintribe.model.generic.reflection.type.custom;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.braintribe.model.generic.reflection.EntityType;

/**
 * Slightly modified C3 linearization to leniently handle hierarchies that break monotonicity. E.g.
 * 
 * <pre>
 * A extends B, C
 * D extends C, B
 * X extends A, D
 * </pre>
 * 
 * Strict C3 would fail due to a contradiction - B before C, and C before B in the supertypes of X. We simply pick the first encountered, i.e. X goes
 * first to A, A goes first to B, thus B is before C in the result.
 */
/* package */ final class C3Linearization {

	public static List<EntityType<?>> getLinearizedSuperTypes(EntityType<?> type) {
		List<List<EntityType<?>>> sequences = new ArrayList<>();

		for (EntityType<?> parent : type.getSuperTypes())
			sequences.add(new ArrayList<>(parent.getLinearizedSuperTypes()));

		sequences.add(new ArrayList<>(type.getSuperTypes()));

		return Collections.unmodifiableList(new ArrayList<>(merge(type, sequences)));
	}

	private static Set<EntityType<?>> merge(EntityType<?> type, List<List<EntityType<?>>> sequences) {
		Set<EntityType<?>> result = new LinkedHashSet<>();
		result.add(type);

		while (true) {
			sequences.removeIf(List::isEmpty);
			if (sequences.isEmpty())
				return result;

			EntityType<?> candidate = null;

			outer: //
			for (List<EntityType<?>> seq : sequences) {
				EntityType<?> head = seq.get(0);

				// check if head appears in any tail
				for (List<EntityType<?>> other : sequences) {
					if (other == seq)
						continue;

					if (other.subList(1, other.size()).contains(head))
						continue outer;
				}

				candidate = head;
				break;
			}

			// C3 conflict – fall back to the first available head rather than failing
			if (candidate == null)
				candidate = sequences.get(0).get(0);

			result.add(candidate);

			// remove candidate from all heads
			for (List<EntityType<?>> seq : sequences)
				if (seq.get(0) == candidate)
					seq.remove(0);
		}
	}
}