package com.braintribe.gm.graphfetching.processing.query;

import com.braintribe.gm.graphfetching.api.query.FetchJoin;
import com.braintribe.gm.graphfetching.api.query.FetchSource;
import com.braintribe.model.generic.reflection.CollectionType;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.GenericModelType;
import com.braintribe.model.generic.reflection.Property;
import com.braintribe.model.query.Join;
import com.braintribe.model.query.JoinType;
import com.braintribe.model.query.Source;
import com.braintribe.utils.lcd.Lazy;

public class GmSessionFetchSource implements FetchSource {
	protected final GmSessionFetchQuery query;
	protected final Source source;
	private final int pos;
	protected GenericModelType type;
	private Lazy<Integer> lazyScalarCount = new Lazy<>(this::determineScalarCount);
	
	public GmSessionFetchSource(GmSessionFetchQuery query, GenericModelType type, Source source, int pos) {
		super();
		this.query = query;
		this.type = type;
		this.source = source;
		this.pos = pos;
	}

	@Override
	public int pos() {
		return pos;
	}

	@Override
	public FetchJoin leftJoin(Property property) {
		return join(property, JoinType.left);
	}

	@Override
	public FetchJoin join(Property property) {
		return join(property, JoinType.inner);
	}
	
	private FetchJoin join(Property property, JoinType joinType) {
		String propertyName = property.getName();
		Join join = source.join(propertyName, joinType);
		int pos = query.select(join);
		return new GmSessionFetchJoin(property, query, join, pos); 
	}
	
	@Override
	public int scalarCount() {
		return lazyScalarCount.get();
	}
	
	private int determineScalarCount() {
		EntityType<?> entityType = null;
		
		if (type.isCollection()) {
			CollectionType collectionType = (CollectionType)type;
			GenericModelType elementType = collectionType.getCollectionElementType();
			if (elementType.isEntity())
				entityType = (EntityType<?>) elementType;
		}
		else {
			if (type.isEntity())
				entityType = (EntityType<?>)type;
		}
		
		if (entityType == null)
			return 1;
		
		int count = 0;
		
		for (Property property: entityType.getProperties()) {
			if (property.isIdentifier() || property.getType().isScalar())
				count++;
		}
		
		return count;
	}
}
