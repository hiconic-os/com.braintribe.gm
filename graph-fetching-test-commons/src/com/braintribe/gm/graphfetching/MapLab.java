package com.braintribe.gm.graphfetching;

import java.util.Map;

import com.braintribe.gm.graphfetching.api.Fetching;
import com.braintribe.gm.graphfetching.test.model.data.DataManagement;
import com.braintribe.gm.graphfetching.test.model.data.DataResource;
import com.braintribe.gm.graphfetching.test.model.data.DataSource;

public class MapLab {
	public static void main(String[] args) {
		DataManagement dm = Fetching.graphPrototype(DataManagement.T);
		DataSource ds = Fetching.graphPrototype(DataSource.T);
		DataResource rs = Fetching.graphPrototype(DataResource.T);
		
		ds.getInfo();
		rs.getCreationInfo();
		
		
		dm.getLableRatings();
		
		Map<DataSource,DataResource> sourceOccurrences = dm.getSourceOccurrences();
		
		sourceOccurrences.put(ds, rs);
		
		sourceOccurrences.keySet().add(ds);
		sourceOccurrences.values().add(rs);
		
	}
}
