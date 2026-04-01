package com.braintribe.gm.config.yaml;

import static com.braintribe.utils.lcd.CollectionTools2.newList;
import static com.braintribe.utils.lcd.CollectionTools2.newMap;
import static com.braintribe.utils.lcd.CollectionTools2.newSet;
import static com.braintribe.utils.lcd.CollectionTools2.removeFirst;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.braintribe.common.lcd.Pair;
import com.braintribe.common.lcd.Tuple.Tuple3;
import com.braintribe.common.lcd.UnknownEnumException;
import com.braintribe.gm.model.reason.Maybe;
import com.braintribe.gm.model.reason.Reason;
import com.braintribe.gm.model.reason.ReasonAggregator;
import com.braintribe.gm.model.reason.Reasons;
import com.braintribe.gm.model.reason.config.ConfigurationError;
import com.braintribe.gm.model.reason.config.IncompatibleMergeTypes;
import com.braintribe.model.generic.GMF;
import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.CollectionType;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.GenericModelType;
import com.braintribe.model.generic.reflection.MapType;
import com.braintribe.model.generic.reflection.Property;
import com.braintribe.model.generic.reflection.StandardTraversingContext;

/**
 * @author peter.gazdik
 */
/* package */ class EntityMerger {

	/**
	 * Merges the two entities by copying properties from defaults where they are absent, or by merging in case of collections.
	 * <p>
	 * Nothing is cloned, parts of both given entity graphs might be modified.
	 * <p>
	 * eSource and dSource are just appended to a returned {@link Reason} in case there are some issues.
	 * <p>
	 * Note that it always returns a result, meaning it merges as much as it can, an additional error might be appended.
	 * <p>
	 * Reasons: {@link IncompatibleMergeTypes} - entity and defaults may contain instances with the same globalId, and the one from entity might even
	 * be a sub-type of the defaults one. If it's not assignable, this reason is returned.
	 */
	public static Maybe<GenericEntity> merge(GenericEntity entity, String eSource, GenericEntity defaults) {
		return new EntityMerger(entity, eSource, defaults).deepMerge();
	}

	private final GenericEntity eRoot;
	private final GenericEntity dRoot;
	private final Map<String, GenericEntity> eGidToEntity;
	private final Map<String, GenericEntity> dGidToEntity;

	private final ReasonAggregator<Reason> reasonAggregator;

	// entities from the defaults Entity that were merged into the result
	private final Set<GenericEntity> mergedDefaults = newSet();
	// mergedDefaults which still haven't their properties examined for sanitation
	/** @see #sanitize(GenericEntity) */
	private final Set<GenericEntity> unsanitizedDefaults = newSet();

	private EntityMerger(GenericEntity eRoot, String eSource, GenericEntity dRoot) {
		this.eRoot = eRoot;
		this.dRoot = dRoot;
		this.eGidToEntity = indexByGid(eRoot);
		this.dGidToEntity = indexByGid(dRoot);

		reasonAggregator = Reasons.aggregator(() -> ConfigurationError
				.create("Multiple errors while merging entities " + nameWithSource(eRoot, eSource) + ", " + nameWithSource(dRoot, "")));
	}

	private String nameWithSource(GenericEntity entity, String source) {
		String gid = entity.getGlobalId();
		String gidPart = gid == null ? "" : ", gid: " + gid;

		return "[" + entity.entityType().getTypeSignature() + ", " + source + gidPart + "]";
	}

	private Maybe<GenericEntity> deepMerge() {
		// Merging
		shallowMerge(eRoot, dRoot);

		for (Entry<String, GenericEntity> entry : eGidToEntity.entrySet()) {
			String gid = entry.getKey();
			GenericEntity e = entry.getValue();
			if (e == eRoot)
				continue;

			GenericEntity d = dGidToEntity.get(gid);
			if (d == null)
				continue;

			shallowMerge(e, d);
		}

		// Sanitizing

		while (!unsanitizedDefaults.isEmpty())
			sanitize(removeFirst(unsanitizedDefaults));

		// Result

		if (reasonAggregator.hasReason())
			return Maybe.incomplete(eRoot, reasonAggregator.get());
		else
			return Maybe.complete(eRoot);
	}

	private void shallowMerge(GenericEntity entity, GenericEntity defaults) {
		mergedDefaults.add(defaults);
		unsanitizedDefaults.remove(defaults);

		EntityType<GenericEntity> et = entity.entityType();

		if (!et.isInstance(defaults)) {
			reasonAggregator.accept(IncompatibleMergeTypes.create(defaults, entity));
			return;
		}

		for (Property p : et.getProperties()) {
			// defaults has nothing to contribute - nothing to do
			if (p.isAbsent(defaults))
				continue;

			boolean eAbsent = p.isAbsent(entity);
			if (eAbsent && !p.getType().areCustomInstancesReachable()) {
				Object dValue = p.getDirectUnsafe(defaults);
				p.setDirectUnsafe(entity, dValue);
				continue;
			}

			Object eValue = p.get(entity);
			if (!eAbsent && eValue == null)
				continue;

			GenericModelType eType = p.getType();
			if (eType.isBase())
				eType = GMF.getTypeReflection().getType(eValue);

			if (!eType.isCollection())
				continue;

			Object dValue = p.get(defaults);
			CollectionType colType = (CollectionType) eType;

			if (eValue == null) {
				eValue = colType.createPlain();
				p.setDirectUnsafe(entity, eValue);
			}

			mergeCollection(eValue, colType, dValue);
		}
	}

	// return false indicates incompatible types
	private void mergeCollection(Object eValue, CollectionType colType, Object dValue) {
		switch (colType.getCollectionKind()) {
			case list:
			case set:
				mergeLinearCollection((Collection<Object>) eValue, colType, dValue);
				break;
			case map:
				mergeMap((Map<Object, Object>) eValue, (MapType) colType, dValue);
				break;
			default:
				throw new UnknownEnumException(colType.getCollectionKind());
		}
	}

	private void mergeLinearCollection(Collection<Object> eValue, CollectionType eType, Object dValue) {
		if (eType.getCollectionElementType().isScalar()) {
			// no need to bother with global ids
			if (dValue instanceof Collection c)
				eValue.addAll(c);

			return;
		}

		if (dValue instanceof Collection c) {
			Set<String> eGids = collectGids(eValue);
			eGids.remove(null);

			for (Object dElement : c)
				if (!isAlreadyPresent(dElement, eGids))
					eValue.add(findSanitizedElement(dElement));
		}
	}

	private void mergeMap(Map<Object, Object> eValue, MapType eType, Object dValue) {
		if (!(dValue instanceof Map))
			return;

		Map<?, ?> dMap = (Map<?, ?>) dValue;

		if (eType.getKeyType().isScalar()) {
			// no need to bother with global ids
			for (Entry<?, ?> dEntry : dMap.entrySet())
				eValue.putIfAbsent(dEntry.getKey(), findSanitizedElement(dEntry.getValue()));

		} else {
			// bother with global ids
			Set<String> eKeyGid = collectGids(eValue.keySet());

			for (Entry<?, ?> dEntry : dMap.entrySet()) {
				Object key = dEntry.getKey();

				if (!isAlreadyPresent(key, eKeyGid))
					// putIfAbset in case key is not a GenericEntity
					eValue.putIfAbsent(findSanitizedElement(key), findSanitizedElement(dEntry.getValue()));
			}
		}
	}

	private Set<String> collectGids(Collection<Object> collection) {
		return collection.stream() //
				.filter(e -> e instanceof GenericEntity) //
				.map(v -> ((GenericEntity) v).getGlobalId()) //
				.collect(java.util.stream.Collectors.toSet());
	}

	private boolean isAlreadyPresent(Object element, Set<String> gids) {
		return element instanceof GenericEntity dEntity && gids.contains(dEntity.getGlobalId());
	}

	private static Map<String, GenericEntity> indexByGid(GenericEntity entity) {
		StandardTraversingContext traversingContext = new StandardTraversingContext();

		entity.entityType().traverse(traversingContext, entity);

		Map<String, GenericEntity> gidToEntity = newMap();

		for (GenericEntity e : traversingContext.getVisitedObjects())
			if (e.getGlobalId() != null)
				gidToEntity.put(e.getGlobalId(), e);

		return gidToEntity;
	}

	//
	// SANITIZING
	//

	/** For each property of given entity it replaces merged default entities with their "entity" counterparts if ones with same global id exist. */
	private void sanitize(GenericEntity defaults) {
		EntityType<GenericEntity> et = defaults.entityType();
		for (Property p : et.getProperties()) {
			if (!p.getType().areCustomInstancesReachable())
				continue;

			// defaults has nothing to contribute - nothing to do
			if (p.isAbsent(defaults))
				continue;

			Object dValue = p.get(defaults);
			if (dValue == null)
				continue;

			GenericModelType dType = p.getType();
			if (dType.isBase())
				dType = GMF.getTypeReflection().getType(dValue);

			if (dType.isCollection())
				sanitizeCollection(dValue);
			else
				sanitizeNonCollection(defaults, p, dValue);
		}
	}

	private void sanitizeNonCollection(GenericEntity entity, Property p, Object dValue) {
		Object eValue = findSanitizedElement(dValue);
		if (eValue != dValue)
			p.setDirectUnsafe(entity, eValue);
	}

	private void sanitizeCollection(Object c) {
		if (c instanceof List l)
			sanitizeList(l);
		else if (c instanceof Set s)
			sanitizeSet(s);
		else if (c instanceof Map m)
			sanitizeMap(m);
	}

	private void sanitizeList(List<Object> l) {
		int i = -1;
		for (Object dElement : l) {
			i++;
			Object eElement = findSanitizedElement(dElement);
			if (eElement != dElement)
				l.set(i, eElement);
		}
	}

	private void sanitizeSet(Set<Object> s) {
		List<Pair<Object, Object>> toReplace = newList();

		for (Object dElement : s) {
			Object eElement = findSanitizedElement(dElement);
			if (eElement != dElement)
				toReplace.add(Pair.of(dElement, eElement));
		}

		for (Pair<Object, Object> pair : toReplace) {
			s.remove(pair.first);
			s.add(pair.second);
		}
	}

	private void sanitizeMap(Map<Object, Object> m) {
		List<Tuple3<Object, Object, Object>> toReplace = newList();

		for (Entry<Object, Object> e : m.entrySet()) {
			Object dK = e.getKey();
			Object dV = e.getValue();

			Object eK = findSanitizedElement(dK);
			Object eV = findSanitizedElement(dV);

			if (eK != dK || eV != dV)
				toReplace.add(Tuple3.of(dK, eK, eV));
		}

		for (Tuple3<Object, Object, Object> tr : toReplace) {
			m.remove(tr.val0());
			m.put(tr.val1(), tr.val2());
		}
	}

	// Element as in collection element, i.e. not a collection itself.
	private Object findSanitizedElement(Object dValue) {
		if (dValue instanceof GenericEntity dEntity) {
			GenericEntity eEntity = findEEntity(dEntity);
			if (eEntity != null)
				return eEntity;

			noticeDefaultEntity(dEntity);
			return dEntity;

		} else {
			return dValue;
		}
	}

	private GenericEntity findEEntity(GenericEntity dEntity) {
		String gid = dEntity.getGlobalId();
		return gid == null ? null : eGidToEntity.get(gid);
	}

	private void noticeDefaultEntity(GenericEntity dEntity) {
		if (mergedDefaults.add(dEntity))
			unsanitizedDefaults.add(dEntity);
	}

}
