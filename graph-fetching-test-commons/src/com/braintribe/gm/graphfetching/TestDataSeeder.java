package com.braintribe.gm.graphfetching;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import com.braintribe.gm.graphfetching.test.model.Address;
import com.braintribe.gm.graphfetching.test.model.City;
import com.braintribe.gm.graphfetching.test.model.Company;
import com.braintribe.gm.graphfetching.test.model.Country;
import com.braintribe.gm.graphfetching.test.model.Document;
import com.braintribe.gm.graphfetching.test.model.Gender;
import com.braintribe.gm.graphfetching.test.model.Person;
import com.braintribe.gm.graphfetching.test.model.SignableDocument;
import com.braintribe.gm.graphfetching.test.model.Signature;
import com.braintribe.model.processing.session.api.persistence.PersistenceGmSession;

public class TestDataSeeder extends AbstractDataGenerator {

	private final PersistenceGmSession session;
	// Isolierte Random-Quellen je Bedarf
	private final Random countryRandom;
	private final Random cityRandom;
	private final Random addressRandom;
	private final Random personRandom;
	private final Random companyRandom;
	private final Random documentRandom;
	private final long seed;

	// Datenlisten
	private final List<Country> countries = new ArrayList<>();
	private final List<City> cities = new ArrayList<>();
	private final List<Address> addresses = new ArrayList<>();
	private final List<Person> persons = new ArrayList<>();
	private final List<Company> companies = new ArrayList<>();
	private final List<Document> documents = new ArrayList<>();
	private final List<Company> idmCompanies = new ArrayList<>();
	
	// Mit Session (Persistenz)
	public TestDataSeeder(PersistenceGmSession session, boolean generateId) {
		this(session, 42L, generateId);
	}
	public TestDataSeeder(PersistenceGmSession session, long seed, boolean generateId) {
		super(session, generateId);
		this.session = session;
		this.seed = seed;
		this.countryRandom = new Random(seed + 1);
		this.cityRandom = new Random(seed + 2);
		this.addressRandom = new Random(seed + 3);
		this.personRandom = new Random(seed + 4);
		this.companyRandom = new Random(seed + 5);
		this.documentRandom = new Random(seed + 6);
		seed();
	}
	// Ohne Session (nur transient)
	public TestDataSeeder(boolean generateId) {
		super(null, generateId);
		this.seed = 42L;
		this.session = null;
		this.countryRandom = new Random(seed + 1);
		this.cityRandom = new Random(seed + 2);
		this.addressRandom = new Random(seed + 3);
		this.personRandom = new Random(seed + 4);
		this.companyRandom = new Random(seed + 5);
		this.documentRandom = new Random(seed + 6);
		seed();
	}
	public TestDataSeeder(long seed, boolean generateId) {
		super(null, generateId);
		this.session = null;
		this.seed = seed;
		this.countryRandom = new Random(seed + 1);
		this.cityRandom = new Random(seed + 2);
		this.addressRandom = new Random(seed + 3);
		this.personRandom = new Random(seed + 4);
		this.companyRandom = new Random(seed + 5);
		this.documentRandom = new Random(seed + 6);
		seed();
	}

	public void seed() {
		createCountries(10);
		createCities(20);
		createPersons(100);
		createCompanies(40);
		createIdmTestEntities();
	}

	private void createIdmTestEntities() {
		Company company1 = create(Company.T);
		company1.setName("company.ONE");
		Company company2 = create(Company.T);
		company2.setName("company.land");

		Person duplicatePerson = getPersons().get(0);
		Person person1 = getPersons().get(1);
		Person person2 = getPersons().get(2);

		company1.setLawyer(duplicatePerson);
		company1.getOwners().add(person1);
		company2.getOwners().add(duplicatePerson);
		company2.setLawyer(person2);

		idmCompanies.add(company1);
		idmCompanies.add(company2);
	}

	private void createCountries(int count) {
		String[] base = new String[] { "Austria", "Germany", "Switzerland", "France", "Italy", "Spain", "Portugal", "Netherlands", "Belgium",
				"Poland", "Czechia", "Slovakia", "Hungary", "Slovenia", "Croatia" };
		for (int i = 0; i < count; i++) {
			Country c = create(Country.T);
			c.setName(base[i % base.length] + (i >= base.length ? ("-" + (i + 1)) : ""));
			countries.add(c);
		}
	}

	private void createCities(int count) {
		String[] names = new String[] { "Vienna", "Graz", "Linz", "Salzburg", "Innsbruck", "Munich", "Berlin", "Hamburg", "Cologne", "Frankfurt",
				"Zurich", "Geneva", "Basel", "Lausanne", "Bern", "Paris", "Lyon", "Marseille", "Nice", "Toulouse", "Milan", "Rome", "Naples", "Turin",
				"Bologna" };
		for (int i = 0; i < count; i++) {
			City city = create(City.T);
			String cityName = names[i % names.length] + (i >= names.length ? ("-" + (i + 1)) : "");
			city.setName(cityName);
			city.setPostalCode(String.format("%05d", 10000 + cityRandom.nextInt(80000)));
			cities.add(city);
		}
	}

	private void createPersons(int count) {
		String[] firstNames = new String[] { "Alex", "Sam", "Jamie", "Taylor", "Jordan", "Casey", "Robin", "Chris", "Pat", "Drew", "Max", "Kim",
				"Lee", "Charlie", "Morgan" };
		String[] lastNames = new String[] { "Miller", "Smith", "Johnson", "Brown", "Davis", "Wilson", "Moore", "Taylor", "Anderson", "Thomas",
				"Jackson", "White", "Harris", "Martin", "Thompson" };
		for (int i = 0; i < count; i++) {
			Address address = createRandomAddress();
			Person p = create(Person.T);
			p.setFirstName(firstNames[i % firstNames.length]);
			p.setLastName(lastNames[(i / firstNames.length) % lastNames.length]);
			p.setGender(randomGender());
			p.setBirthday(randomBirthday(1960, 2005));
			p.setAddress(address);
			persons.add(p);
		}

		for (int i = 0; i < count; i++) {
			persons.get(i).setBestFriend(persons.get(count - 1 - i));
		}
	}

	private void createCompanies(int count) {
		for (int i = 0; i < count; i++) {
			Company company = create(Company.T);
			company.setName("Company " + (i + 1));
			company.setFoundedAt(randomBirthday(1950, 2020));
			company.setAddress(createRandomAddress());

			Set<Person> owners = pickDistinctPersons(1 + companyRandom.nextInt(3));
			company.setOwners(owners);

			Set<Person> ceos = pickDistinctFrom(owners, 1);
			company.setCeos(ceos);

			Person lawyer = pickRandomPersonExcluding(owners);
			company.setLawyer(lawyer);

			Set<Document> contracts = new HashSet<>();
			int docs = documentRandom.nextInt(4); // 0..3
			for (int d = 0; d < docs; d++) {
				contracts.add(createRandomContract(company.getName(), d + 1));
			}
			company.setContracts(contracts);

			companies.add(company);
		}
	}

	private Address createRandomAddress() {
		Address a = create(Address.T);
		City city = cities.get(addressRandom.nextInt(cities.size()));
		a.setCity(city);
		a.setStreet(randomStreetName());
		a.setStreetNumber(String.valueOf(1 + addressRandom.nextInt(200)));
		addresses.add(a);
		return a;
	}

	private Document createRandomContract(String companyName, int idx) {
		boolean signable = (idx % 2) == 0;
		
		final Document doc;
		
		if (signable) {
			SignableDocument signableDocument = create(SignableDocument.T, Document.T);
			int signatureCount = idx % 5;
			for (int i = 0; i < signatureCount; i++) {
				Signature signature = create(Signature.T); 
				signature.setHash("" + signableDocument.getId());
				signature.setSignature(companyName + signature.getId());
				signableDocument.getSignatures().add(signature);
			}
			doc = signableDocument;
		}
		else {
			doc = create(Document.T);
		}
		
		doc.setName(companyName + " Contract #" + idx);
		Set<String> tags = new HashSet<>();
		tags.add("contract");
		tags.add("company");
		tags.add(companyName.toLowerCase().replace(' ', '-'));
		doc.setTags(tags);
		doc.setText("This is a sample contract document for " + companyName + ".");
		documents.add(doc);
		
		return doc;
	}

	private String randomStreetName() {
		String[] streets = new String[] { "Main Street", "High Street", "Oak Avenue", "Maple Road", "Elm Street", "Cedar Lane", "Pine Street",
				"Birch Way", "Walnut Drive", "Chestnut Court" };
		return streets[addressRandom.nextInt(streets.length)];
	}

	private Gender randomGender() {
		Gender[] all = Gender.values();
		return all[personRandom.nextInt(all.length)];
	}

	private Date randomBirthday(int startYearInclusive, int endYearInclusive) {
		int year = startYearInclusive + personRandom.nextInt(endYearInclusive - startYearInclusive + 1);
		int month = personRandom.nextInt(12);
		int day = 1 + personRandom.nextInt(28);
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, year);
		cal.set(Calendar.MONTH, month);
		cal.set(Calendar.DAY_OF_MONTH, day);
		cal.set(Calendar.HOUR_OF_DAY, 12);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal.getTime();
	}

	private Set<Person> pickDistinctPersons(int howMany) {
		List<Person> shuffled = new ArrayList<>(persons);
		Collections.shuffle(shuffled, companyRandom);
		Set<Person> result = new HashSet<>();
		for (int i = 0; i < Math.min(howMany, shuffled.size()); i++) {
			result.add(shuffled.get(i));
		}
		return result;
	}
	private Set<Person> pickDistinctFrom(Set<Person> existing, int howMany) {
		List<Person> candidates = new ArrayList<>(persons);
		candidates.removeAll(existing);
		Collections.shuffle(candidates, companyRandom);
		Set<Person> result = new HashSet<>();
		for (int i = 0; i < Math.min(howMany, candidates.size()); i++) {
			result.add(candidates.get(i));
		}
		if (result.isEmpty() && !persons.isEmpty()) {
			result.add(persons.get(0));
		}
		return result;
	}
	private Person pickRandomPersonExcluding(Set<Person> exclude) {
		List<Person> candidates = new ArrayList<>(persons);
		candidates.removeAll(exclude);
		if (candidates.isEmpty()) {
			return persons.isEmpty() ? null : persons.get(0);
		}
		return candidates.get(companyRandom.nextInt(candidates.size()));
	}

	// Getter
	public List<Country> getCountries() {
		return countries;
	}
	public List<City> getCities() {
		return cities;
	}
	public List<Address> getAddresses() {
		return addresses;
	}
	public List<Person> getPersons() {
		return persons;
	}
	public List<Company> getCompanies() {
		return companies;
	}
	public List<Document> getDocuments() {
		return documents;
	}
	public List<Company> getIdmCompanies() {
		return idmCompanies;
	}
}
