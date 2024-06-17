package com.braintribe.model.processing.session.impl.notifying;

import static com.braintribe.model.generic.manipulation.util.ManipulationBuilder.delete;
import static com.braintribe.model.generic.manipulation.util.ManipulationBuilder.instantiation;

import com.braintribe.model.generic.GMF;
import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.manipulation.DeleteManipulation;
import com.braintribe.model.generic.manipulation.DeleteMode;
import com.braintribe.model.generic.manipulation.InstantiationManipulation;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.session.GmSession;
import com.braintribe.model.processing.session.api.notifying.EntityCreation;

public class EntityCreationImpl<T extends GenericEntity> implements EntityCreation<T> {

	protected final GmSession session;
	protected final EntityType<T> type;

	protected boolean raw;
	protected Object id;
	protected String globalId;
	protected String partition;

	public EntityCreationImpl(GmSession session, EntityType<T> type) {
		this.session = session;
		this.type = type;
	}

	@Override
	public EntityCreation<T> raw() {
		this.raw = true;
		return this;
	}

	@Override
	public EntityCreation<T> withPartition(String partition) {
		this.partition = partition;
		return this;
	}

	@Override
	public T globalWithRandomUuid() {
		return global(GMF.platform().newUuid());
	}

	@Override
	public T global(String globalId) {
		this.globalId = globalId;
		return create();
	}

	@Override
	public T persistent(Object id) {
		this.id = id;
		return create();
	}

	@Override
	public T preliminary() {
		return create();
	}

	private T create() {
		T entity = type.createRaw();

		if (id != null)
			entity.setId(id);
		else if (globalId != null)
			entity.setGlobalId(globalId);

		if (partition != null)
			entity.setPartition(partition);

		entity.attach(session);

		InstantiationManipulation instantiationManipulation = instantiation(entity);
		DeleteManipulation inverseManipulation = delete(entity, DeleteMode.ignoreReferences);
		instantiationManipulation.linkInverse(inverseManipulation);

		session.noticeManipulation(instantiationManipulation);

		if (!raw)
			type.initialize(entity);

		return entity;
	}

}
