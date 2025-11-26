package com.braintribe.gm.graphfetching;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;

import com.braintribe.gm.graphfetching.test.model.data.ChunkQuality;
import com.braintribe.gm.graphfetching.test.model.data.ChunkedSource;
import com.braintribe.gm.graphfetching.test.model.data.CreationInfo;
import com.braintribe.gm.graphfetching.test.model.data.DataManagement;
import com.braintribe.gm.graphfetching.test.model.data.DataResource;
import com.braintribe.gm.graphfetching.test.model.data.DataSource;
import com.braintribe.gm.graphfetching.test.model.data.FileReference;
import com.braintribe.gm.graphfetching.test.model.data.FileSource;
import com.braintribe.gm.graphfetching.test.model.data.InmemorySource;
import com.braintribe.gm.graphfetching.test.model.data.SourceInfo;
import com.braintribe.gm.graphfetching.test.model.data.StringEncodedBinaryData;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.processing.session.api.persistence.PersistenceGmSession;

public class DataSourceDataGenerator extends AbstractDataGenerator {
	
	private static String[] tags = {"important", "obsolete", "priviledged"};
	private static String[] users = {"John", "Jack", "Jim"};
	private static String[] extensions = {"txt", "jpg", "pdf", "html"};
	private static Date[] dates = {
			createDate(1976, 1, 14),
			createDate(1975, 7, 6),
			createDate(1990, 10, 10),
			createDate(2011, 7, 24),
	};
	
	private DataManagement dataManagement;
	private int fileSourceCounter = 0;
	private int resourceCounter = 0;

	public DataSourceDataGenerator(PersistenceGmSession session, boolean generateId) {
		super(session, generateId);
		generate();
	}

	private void generate() {
		if (dataManagement != null)
			return;
		
		DataManagement management = create(DataManagement.T);
		
		List<DataSource> sources = new ArrayList<>();
		
		sources.add(createFileSource());
		sources.add(createFileSource());
		sources.add(createFileSource());
		sources.add(createFileSource());
		sources.add(createInmemorySource());
		sources.add(createInmemorySource());
		sources.add(createInmemorySource());
		sources.add(createInmemorySource());
		sources.add(createChunkedSource(sources.get(0), sources.get(3), sources.get(5)));
		sources.add(createChunkedSource(sources.get(1), sources.get(4), sources.get(6)));
		
		List<DataResource> resources = new ArrayList<>();
		
		for (DataSource source: sources) 
			resources.add(createDataResource(source));
		
		sources.add(createFileSource());
		
		management.getSources().addAll(sources);
		management.getResources().addAll(resources);
		
		this.dataManagement = management; 
	}
	
	public DataManagement getDataManagement() {
		return dataManagement;
	}
	
	private static Date createDate(int year, int month, int day) {
		return Date.from(LocalDate.of(year, month, day).atStartOfDay(ZoneOffset.UTC).toInstant());
	}
	
	public DataResource createDataResource(DataSource dataSource) {
		CreationInfo creationInfo = create(CreationInfo.T);
		creationInfo.setCreatedBy(users[creationInfo.getGlobalId().hashCode() % users.length]);
		creationInfo.setCreatedAt(dates[creationInfo.getGlobalId().hashCode() % dates.length]);
		DataResource dataResource = create(DataResource.T);
		
		String name = "resource-" + dataResource.getGlobalId();
		
		dataResource.setName(name);
		dataResource.setCreationInfo(creationInfo);
		dataResource.setSource(dataSource);
		
		for (int i = 0; i < 5; i++) {
			String altName = name + "-" + i;
			dataResource.getAltNames().add(altName);
		}
		
		int tagCount = resourceCounter++ % 3;
		
		for (int i = 0; i < tagCount; i++) {
			dataResource.getTags().add(tags[i]);
		}
		
		return dataResource;
	}
	
	public FileSource createFileSource() {
		FileReference reference = create(FileReference.T);
		
		int num = fileSourceCounter++;
		String extension = extensions[num % extensions.length];
		reference.setName("name-" + reference.getGlobalId() + "." + extension);
		reference.setPath("/foo/bar/" + reference.getGlobalId());
		FileSource fileSource = create(FileSource.T, DataSource.T);
		fileSource.setExtension(extension);
		fileSource.setReference(reference);
		fillDataSource(fileSource);
		return fileSource;
	}
	
	public void fillDataSource(DataSource dataSource) {
		SourceInfo sourceInfo = create(SourceInfo.T);
		String hash = String.valueOf(sourceInfo.getGlobalId().hashCode());
		sourceInfo.setHash(hash);
		int length = hash.length();
		sourceInfo.setSize(length);
		dataSource.setInfo(sourceInfo);
		dataSource.setCacheable(length % 2 == 0);
	}
	
	public InmemorySource createInmemorySource() {
		StringEncodedBinaryData binaryData = create(StringEncodedBinaryData.T);
		String data = Base64.getEncoder().encodeToString(binaryData.getGlobalId().getBytes());
		binaryData.setData(data);
		binaryData.setEncoding("Base64");
		InmemorySource inmemorySource = create(InmemorySource.T, DataSource.T);
		inmemorySource.setLarge(data.length() % 2 == 0); 
		inmemorySource.setBinaryData(binaryData);
		fillDataSource(inmemorySource);
		
		return inmemorySource;
	}
	
	public ChunkedSource createChunkedSource(DataSource... sources) {
		ChunkedSource chunkedSource = create(ChunkedSource.T, DataSource.T);
		
		EntityType<?> uniType = null;
		ChunkQuality quality = ChunkQuality.uniform;
		
		for (DataSource source: sources) {
			if (uniType == null)
				uniType = source.entityType();
			else if (uniType != source.entityType())
				quality = ChunkQuality.mixed;
			
			chunkedSource.getChunks().add(source);
		}
		
		chunkedSource.setChunkQuality(quality);
		
		fillDataSource(chunkedSource);
		
		return chunkedSource;
	}
	
	
}
