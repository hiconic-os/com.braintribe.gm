package com.braintribe.gm.graphfetching;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.processing.session.api.persistence.PersistenceGmSession;

public class AbstractDataGenerator implements GraphFetchingTestConstants {
	private final PersistenceGmSession session;
	private final boolean generateId;
	private final Map<EntityType<?>, AtomicLong> idSequences = new ConcurrentHashMap<>();
	
	
	public AbstractDataGenerator(PersistenceGmSession session, boolean generateId) {
		super();
		this.session = session;
		this.generateId = generateId;
	}

	// Die zentrale generische create-Methode
	public <T extends GenericEntity> T create(EntityType<T> type) {
		return create(type, type);
	}
	
	public <T extends GenericEntity> T create(EntityType<T> type, EntityType<? super T> idType) {
		T entity;
		if (session != null) {
			entity = session.create(type);
		} else {
			entity = type.create();
		}
		// Zentrale, vorhersehbare Sequenz-ID zuweisen
		AtomicLong sequence = idSequences.computeIfAbsent(idType, t -> new AtomicLong(1));
		long id = sequence.getAndIncrement();
		if (generateId)
			entity.setId(id);
		entity.setGlobalId(type.getTypeSignature() + "@" + String.valueOf(id));
		entity.setPartition(ACCESS_ID_TEST);
		return entity;
	}

}
