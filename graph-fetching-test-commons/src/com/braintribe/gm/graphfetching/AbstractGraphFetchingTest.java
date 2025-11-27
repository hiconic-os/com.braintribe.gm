package com.braintribe.gm.graphfetching;

import java.io.File;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.braintribe.gm._GraphFetchingTestModel_;
import com.braintribe.gm.graphfetching.api.FetchBuilder;
import com.braintribe.gm.graphfetching.api.Fetching;
import com.braintribe.gm.graphfetching.api.node.EntityGraphNode;
import com.braintribe.gm.graphfetching.test.model.Company;
import com.braintribe.gm.graphfetching.test.model.Person;
import com.braintribe.gm.graphfetching.test.model.data.DataManagement;
import com.braintribe.model.access.IncrementalAccess;
import com.braintribe.model.generic.GMF;
import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.path.api.IListItemModelPathElement;
import com.braintribe.model.generic.path.api.IMapKeyModelPathElement;
import com.braintribe.model.generic.path.api.IMapValueModelPathElement;
import com.braintribe.model.generic.path.api.IModelPathElement;
import com.braintribe.model.generic.path.api.IPropertyModelPathElement;
import com.braintribe.model.generic.path.api.IPropertyRelatedModelPathElement;
import com.braintribe.model.generic.path.api.ISetItemModelPathElement;
import com.braintribe.model.generic.reflection.CollectionType;
import com.braintribe.model.generic.reflection.EssentialCollectionTypes;
import com.braintribe.model.generic.reflection.GenericModelType;
import com.braintribe.model.generic.reflection.MapType;
import com.braintribe.model.generic.reflection.Property;
import com.braintribe.model.generic.reflection.SetType;
import com.braintribe.model.meta.GmMetaModel;
import com.braintribe.model.processing.core.commons.comparison.AssemblyComparison;
import com.braintribe.model.processing.core.commons.comparison.AssemblyComparisonResult;
import com.braintribe.model.processing.meta.oracle.BasicModelOracle;
import com.braintribe.model.processing.query.building.EntityQueries;
import com.braintribe.model.processing.session.api.persistence.PersistenceGmSession;
import com.braintribe.model.query.EntityQuery;
import com.braintribe.testing.junit.assertions.assertj.core.api.Assertions;
import com.braintribe.testing.tools.gm.GmTestTools;
import com.braintribe.utils.paths.UniversalPath;

public abstract class AbstractGraphFetchingTest implements GraphFetchingTestConstants {
	private static final com.braintribe.logging.Logger logger = com.braintribe.logging.Logger.getLogger(AbstractGraphFetchingTest.class);

	protected static GmMetaModel model = GMF.getTypeReflection().getModel(_GraphFetchingTestModel_.name).getMetaModel();

	private static TestDataSeeder seeder;
	private static DataSourceDataGenerator dataGenerator;

	private void configureLogging() {
		LogManager.getLogManager().reset();
		Logger root = Logger.getLogger("");
		root.setLevel(Level.INFO);

		// log file path
		File logFile = new File("out/test.log");

		// remove default console handlers
		for (Handler h : root.getHandlers()) {
			root.removeHandler(h);
		}

		try {
			// FileHandler in append mode -> the file is already truncated above
			FileHandler fh = new FileHandler(logFile.getPath(), true);
			fh.setLevel(Level.FINEST);

			// ultra-short minimal formatter
			fh.setFormatter(new Formatter() {
				private String shortLevel(Level l) {
					int v = l.intValue();
					if (v >= 1000)
						return "E"; // SEVERE
					if (v >= 900)
						return "W"; // WARNING
					if (v >= 800)
						return "I"; // INFO
					if (v >= 700)
						return "D"; // CONFIG
					return "T"; // FINE/FINER/FINEST
				}

				@Override
				public String format(LogRecord r) {
					String cn = r.getSourceClassName();
					int ix = cn.lastIndexOf('.');
					String shortName = ix >= 0 ? cn.substring(ix + 1) : cn;
					return shortLevel(r.getLevel()) + " " + shortName + ": " + r.getMessage() + "\n";
				}
			});

			root.addHandler(fh);

		} catch (Exception e) {
			e.printStackTrace(); // last resort
		}

		// enable detailed log level for your package
		String logPackageName = UniversalPath.empty().push(Fetching.class.getPackage().getName(), ".").pop().toDottedPath();
		Logger.getLogger(logPackageName).setLevel(Level.FINEST);
	}

	private static void truncateLogFile() {
		// log file path
		File logFile = new File("out/test.log");

		try {
			// ensure directory exists
			logFile.getParentFile().mkdirs();

			if (logFile.exists()) {
				Path p = logFile.toPath();
				try (FileChannel ch = FileChannel.open(p, StandardOpenOption.WRITE)) {
					ch.truncate(0); // makes file empty but does not remove handle
				}
			}

		} catch (IOException io) {
			io.printStackTrace();
		}
	}
	
    private static final Map<Class<?>, Object> contextCache = new HashMap<>();

    protected synchronized <T> T getOrCreateContext(Class<?> type, Supplier<T> supplier) {
        return (T)contextCache.computeIfAbsent(type, k -> supplier.get());
    }
    
    protected static class PersistenceContext {
    	IncrementalAccess access;
    }

	@BeforeClass
	public static void init() {
		truncateLogFile();
	}
	
	protected PersistenceContext persistenceContext;

	protected abstract IncrementalAccess buildAccess();
	
	@Before
    public void setup() {
		persistenceContext = getOrCreateContext(getClass(), () -> {
			PersistenceContext context = new PersistenceContext();
			context.access = buildAccess();
			
			PersistenceGmSession session = GmTestTools.newSession(context.access);
			seeder = new TestDataSeeder(session, generateIds());
			session.commit();
			
			dataGenerator = new DataSourceDataGenerator(session, generateIds());
			session.commit();

			return context;
        });
    }
	
	protected boolean generateIds() {
		return false;
	}
	
	@Before
	public void before() {
		configureLogging();
	}
	
	protected FetchBuilder fetchBuilder(PersistenceGmSession session, EntityGraphNode node) {
		return configure(Fetching.build(session, node));
	}

	protected FetchBuilder configure(FetchBuilder fetchBuilder) {
		return fetchBuilder;
	}

	protected PersistenceGmSession newSession() {
		return GmTestTools.newSession(persistenceContext.access);
	}
	
	@Test
	public void graphNodeCreation() {
		DataManagement dm = Fetching.graphPrototype(DataManagement.T);
		dm.getLableRatings();
		dm.getSourceHashes();
		dm.getSourceOccurrences();
		dm.getResourcesByName();
		
		EntityGraphNode rootNode = Fetching.rootNode(dm);
		
		System.out.println(rootNode.stringify());
		
		EntityGraphNode rootNode2 = Fetching.rootNode(DataManagement.T,
			Fetching.node(DataManagement.lableRatings),
			Fetching.node(DataManagement.sourceHashes),
			Fetching.node(DataManagement.sourceOccurrences),
			Fetching.node(DataManagement.resourcesByName)
		);
		
		System.out.println(rootNode2.stringify());
	}
	
	@Test
	public void testPolymorphism() {
		PersistenceGmSession session = newSession(); 

		BasicModelOracle oracle = new BasicModelOracle(model);
		EntityGraphNode graphNode = Fetching.reachable(DataManagement.T).polymorphy(oracle).build();
		
		System.out.println(graphNode.stringify());

		DataManagement dataManagement = dataGenerator.getDataManagement();
		
		List<DataManagement> expectedData = Collections.singletonList(dataManagement);
		
		List<DataManagement> actualData = fetchBuilder(session, graphNode).fetchDetached(expectedData);
		
		AssemblyComparisonResult comparisonResult = AssemblyComparison.build() //
				.useGlobalId()
				.enableTracking() //
				.compare(expectedData, actualData);

		Assertions.assertThat(comparisonResult.equal())//
				.describedAs(() -> comparisonResult.mismatchDescription() + " @ " + stringify(comparisonResult.firstMismatchPath())).isTrue();
	}
	
	@Test
	public void testReachable() {
		PersistenceGmSession session = newSession(); 

		BasicModelOracle oracle = new BasicModelOracle(model);
		EntityGraphNode graphNode = Fetching.reachable(Company.T).polymorphy(oracle).build();

		System.out.println(graphNode.stringify());

		Set<Company> expectedCompanies = new HashSet<>(seeder.getCompanies());

		Set<Object> companyIds = expectedCompanies.stream().map(c -> c.getId()).collect(Collectors.toSet());

		{
			Set<Company> actualCompanies = new HashSet<>(session.query()
					.entities(EntityQuery.create(Company.T).where(EntityQueries.in(EntityQueries.property(GenericEntity.id), companyIds))).list());

			fetchBuilder(session, graphNode).fetch(actualCompanies);
		}

		logger.info("==== ACTUAL RUN ===");

		Set<Company> actualCompanies = new HashSet<>(session.query()
				.entities(EntityQuery.create(Company.T).where(EntityQueries.in(EntityQueries.property(GenericEntity.id), companyIds))).list());

		fetchBuilder(session, graphNode).fetch(actualCompanies);

		AssemblyComparisonResult comparisonResult = AssemblyComparison.build() //
				.useGlobalId()
				.enableTracking() //
				.compare(expectedCompanies, actualCompanies);

		Assertions.assertThat(comparisonResult.equal())//
				.describedAs(() -> comparisonResult.mismatchDescription() + " @ " + stringify(comparisonResult.firstMismatchPath())).isTrue();

	}

	@Test
	public void testIdmFetching() {
		PersistenceGmSession session = newSession();
		List<Company> companies = seeder.getIdmCompanies();

		Company companyPrototype = Fetching.graphPrototype(Company.T);

		companyPrototype.getAddress().getCity();
		companyPrototype.getLawyer().getAddress();
		companyPrototype.getOwners().iterator().next().getBestFriend();

		EntityGraphNode rootNode = Fetching.rootNode(companyPrototype);
		
		System.out.println(rootNode.stringify());

		List<Company> actualCompanies = fetchBuilder(session, rootNode).fetchDetached(companies);

		Person p1 = actualCompanies.get(0).getLawyer();
		Person p2 = actualCompanies.get(1).getOwners().iterator().next();

		Assertions.assertThat(p1).isSameAs(p2);
		Assertions.assertThat(p1.getAddress()).isNotNull();
		Assertions.assertThat(p1.getBestFriend()).isNotNull();

		Person p3 = actualCompanies.get(1).getLawyer();
		Person p4 = actualCompanies.get(0).getOwners().iterator().next();

		Assertions.assertThat(p3).isNotSameAs(p4);
		Assertions.assertThat(p3.getAddress()).isNotNull();
		Assertions.assertThat(p3.getBestFriend()).isNull();

		Assertions.assertThat(p4.getAddress()).isNull();
		Assertions.assertThat(p4.getBestFriend()).isNotNull();
	}
	@Test
	public void testBasicFetching() {

		PersistenceGmSession session = newSession();

		List<Company> companies = seeder.getCompanies();
		Set<Object> companyIds = companies.stream().map(c -> c.getId()).collect(Collectors.toSet());

		Company companyPrototype = Fetching.graphPrototype(Company.T);

		companyPrototype.getAddress().getCity();
		companyPrototype.getContracts();
		companyPrototype.getOwners().iterator().next().getAddress().getCity();

		EntityGraphNode rootNode = Fetching.rootNode(companyPrototype);

		Set<Company> expectedCompanies = new HashSet<>(Fetching.fetchFromLocal(rootNode, companies));

		Set<Company> actualCompanies = new HashSet<>(session.query()
				.entities(EntityQuery.create(Company.T).where(EntityQueries.in(EntityQueries.property(GenericEntity.id), companyIds))).list());

		fetchBuilder(session, rootNode).fetch(actualCompanies);

		AssemblyComparisonResult comparisonResult = AssemblyComparison.build() //
				.useGlobalId()
				.enableTracking() //
				.compare(expectedCompanies, actualCompanies);

		Assertions.assertThat(comparisonResult.equal())//
				.describedAs(() -> comparisonResult.mismatchDescription() + " @ " + stringify(comparisonResult.firstMismatchPath())).isTrue();

	}

	private String stringify(IModelPathElement element) {
		StringBuilder builder = new StringBuilder();
		stringify(element, builder);
		return builder.toString();
	}

	/* Company@13.address.city.name Company@13.contracts[0].name Company@13.contracts[someString].name Company@13.contracts[someString].name
	 * Company@13.personToAddress[Person@1].street; Company@13.personToAddress(Person@1).address; */

	private void stringify(IModelPathElement element, StringBuilder builder) {

		IModelPathElement previous = element.getPrevious();

		if (previous != null)
			stringify(previous, builder);

		switch (element.getElementType()) {
			case Root:
			case EntryPoint:
				builder.append(element.getType().getTypeSignature());
				break;
			case ListItem:
				builder.append('[');
				builder.append(((IListItemModelPathElement) element).getIndex());
				builder.append(']');
				break;
			case SetItem:
				builder.append('(');
				ISetItemModelPathElement setElement = (ISetItemModelPathElement) element;
				SetType setType = (SetType) getCollectionType(setElement);
				GenericModelType setElementType = setType.getCollectionElementType();
				builder.append(stringify(setElementType, setElement.getValue()));
				builder.append(')');
				break;
			case MapKey:
				break;
			case MapValue:
				IMapKeyModelPathElement mapKeyElement = (IMapKeyModelPathElement) element;
				MapType mapType = (MapType) getCollectionType(mapKeyElement);
				GenericModelType keyType = mapType.getKeyType();
				builder.append('[');
				builder.append(stringify(keyType, mapKeyElement.getValue()));
				builder.append(']');
				break;
			case Property:
				builder.append(".");
				builder.append(((IPropertyModelPathElement) element).getProperty().getName());
				break;
		}
	}

	private CollectionType getCollectionType(IPropertyRelatedModelPathElement element) {
		Property property = element.getProperty();

		if (property != null)
			return (CollectionType) property.getType();

		if (element instanceof ISetItemModelPathElement) {
			return EssentialCollectionTypes.TYPE_SET;
		} else if (element instanceof IMapKeyModelPathElement) {
			return EssentialCollectionTypes.TYPE_MAP;
		} else if (element instanceof IMapValueModelPathElement) {
			return EssentialCollectionTypes.TYPE_MAP;
		} else if (element instanceof IListItemModelPathElement) {
			return EssentialCollectionTypes.TYPE_LIST;
		}

		throw new IllegalStateException("unexpected element type " + element.getType());
	}

	private String stringify(GenericModelType type, Object value) {
		if (type.isBase())
			type = type.getActualType(value);

		if (type.isEntity()) {
			GenericEntity entity = (GenericEntity) value;
			return entity.entityType().getShortName() + "@" + entity.getId();
		} else {
			return String.valueOf(value);
		}

	}
}
