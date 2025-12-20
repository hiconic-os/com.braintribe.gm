package com.braintribe.gm.graphfetching.processing.fetch;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.braintribe.gm.graphfetching.api.node.AbstractEntityGraphNode;
import com.braintribe.model.generic.GenericEntity;

public class FetchTask {
	public final AbstractEntityGraphNode node;
	public final FetchType fetchType;
	public final Map<Object, GenericEntity> entities;
	public final FetchPathNode fetchPath;
	
	public FetchTask(AbstractEntityGraphNode node, FetchType fetchType, Collection<? extends GenericEntity> entities) {
		this.node = node;
		this.fetchType = fetchType;
		this.entities = new HashMap<Object, GenericEntity>();
		
		for (GenericEntity entity: entities) {
			this.entities.put(entity.getId(), entity);
		}
		this.fetchPath = new FetchPathNode(node);
	}
	
	public FetchTask(AbstractEntityGraphNode node, FetchType fetchType, Map<Object, GenericEntity> entities) {
		this.node = node;
		this.fetchType = fetchType;
		this.entities = entities;
		this.fetchPath = new FetchPathNode(node);
	}
	
	public FetchTask(AbstractEntityGraphNode node, FetchType fetchType, Map<Object, GenericEntity> entities, FetchPathNode parentPath) {
		this.node = node;
		this.fetchType = fetchType;
		this.entities = entities;
		this.fetchPath = new FetchPathNode(parentPath, node);
	}
	
	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();
		b.append(fetchType);
		b.append(" ");
		b.append(node.name());
		if (node.isPolymorphic() != null)
			b.append(" poly ");
		
		b.append(fetchPath.toString());
		
//		b.append(" (");
//		int i = 0;
//		for (Object key: entities.keySet()) {
//			if (i > 0)
//				b.append(",");
//			
//			if (i == 20) {
//				b.append("...");
//				break;
//			}
//				
//			b.append(key);
//			
//			i++;
//		}
//		b.append(")");
		return b.toString();
	}
}
