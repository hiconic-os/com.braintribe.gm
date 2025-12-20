package com.braintribe.gm.graphfetching.processing.query;

import java.util.List;
import java.util.Set;

import com.braintribe.gm.graphfetching.api.query.FetchQuery;
import com.braintribe.gm.graphfetching.api.query.FetchResults;
import com.braintribe.gm.graphfetching.api.query.FetchSource;
import com.braintribe.gm.graphfetching.api.query.FetchQueryOptions;
import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EssentialCollectionTypes;
import com.braintribe.model.generic.value.Variable;
import com.braintribe.model.processing.query.building.SelectQueries;
import com.braintribe.model.processing.session.api.persistence.PersistenceGmSession;
import com.braintribe.model.query.From;
import com.braintribe.model.query.SelectQuery;
import com.braintribe.model.record.ListRecord;

public class GmSessionFetchQuery implements FetchQuery {
	private static final String VAR_NAME_IDS = "ids";
	private final PersistenceGmSession session;
	private final SelectQuery query;
	private final List<Object> selections; 
	private final FetchSource from;
	private FetchQueryOptions options; 

	public GmSessionFetchQuery(PersistenceGmSession session, EntityType<?> entityType, FetchQueryOptions options) {
		super();
		this.session = session;
		this.options = options;
		
		From gmFrom = SelectQueries.source(entityType);
		Variable idsVar = Variable.T.create();
		idsVar.setName(VAR_NAME_IDS);
		idsVar.setTypeSignature(EssentialCollectionTypes.TYPE_SET.getTypeSignature());
		query = SelectQueries.from(gmFrom).where(SelectQueries.in(SelectQueries.property(gmFrom, GenericEntity.id), idsVar));

		selections = query.getSelections();

		Object select = options.getHydrateFrom()? gmFrom: SelectQueries.property(gmFrom, GenericEntity.id); 
		
		int pos = select(select);
		from = new GmSessionFetchSource(this, entityType, gmFrom, pos);
	}
	
	public FetchQueryOptions getOptions() {
		return options;
	}
	
	public int select(Object select) {
		int pos = selections.size();
		selections.add(select);
		return pos;
	}
	
	@Override
	public FetchSource from() {
		return from;
	}
	
	@Override
	public FetchResults fetchFor(Set<Object> entityIds) {
		List<ListRecord> records = session.queryDetached().select(query).setVariable(VAR_NAME_IDS, entityIds).list();
		return new GmSessionFetchResults(records);
	}

	@Override
	public String stringify() {
		return query.stringify();
	}

	public SelectQuery gmQuery() {
		return query;
	}
}
