package com.braintribe.gm.graphfetching.test.gen;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.braintribe.gm.graphfetching.test.gen.TechDataGenerator.Config;
import com.braintribe.gm.graphfetching.test.model.Person;
import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.Property;
import com.braintribe.model.processing.core.commons.comparison.AssemblyComparison;
import com.braintribe.model.processing.core.commons.comparison.AssemblyComparisonResult;

public class TechDataGenerationLab {
	public static void main(String[] args) {
		Config config = new Config();
		TechDataGenerator generator1 = new TechDataGenerator(config, EntityType::create);
		Map<EntityType<?>,List<GenericEntity>> all1 = generator1.generateAll();
		Map<EntityType<?>,List<GenericEntity>> all2 = generator1.generateAll();
	
		List<GenericEntity> expected = all1.values().stream().flatMap(List::stream).collect(Collectors.toList());
		List<GenericEntity> actual = all2.values().stream().flatMap(List::stream).collect(Collectors.toList());
		
		AssemblyComparisonResult compare = AssemblyComparison.build() //
				.enableTracking() //
				.useGlobalId() //
				.compare(expected, actual);
		
		System.out.println(compare.equal());
		
		int maxEnts = 5;
		for (Map.Entry<EntityType<?>, List<GenericEntity>> entry: all1.entrySet()) {
			EntityType<?> entityType = entry.getKey();
			
			System.out.println("===== " + entityType.getShortName() + " =====");
			
			int i = 0;
			for (GenericEntity entity: entry.getValue()) {
				if (i == maxEnts)
					break;
				
				for (Property property: entityType.getProperties()) {
					if (!property.isIdentifier() && !property.getType().isScalar())
						continue;
					
					Object value = property.get(entity);

					System.out.println(property.getName() + ": " + value);
				}
				System.out.println("-----------");
				
			}
		}
	}

	private static Person createCyclicPersonAssembly() {
		Person p1 = Person.T.create();
		Person p2 = Person.T.create();
		Person p3 = Person.T.create();
		p1.setBestFriend(p2);
		p2.setBestFriend(p3);
		p3.setBestFriend(p1);
		
		return p1;
	}
	
	
}
