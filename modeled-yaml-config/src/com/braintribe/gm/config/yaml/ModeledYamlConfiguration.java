// ============================================================================
// Copyright BRAINTRIBE TECHNOLOGY GMBH, Austria, 2002-2022
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
// ============================================================================
package com.braintribe.gm.config.yaml;

import static com.braintribe.gm.config.yaml.PropertyResolutions.reasonifyPropertyResolver;
import static com.braintribe.utils.lcd.CollectionTools2.acquireList;
import static com.braintribe.utils.lcd.CollectionTools2.newList;
import static com.braintribe.utils.lcd.CollectionTools2.newMap;
import static com.braintribe.utils.lcd.StringTools.camelCaseToSocialDistancingCase;
import static java.util.Collections.emptyList;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Supplier;

import com.braintribe.cfg.Configurable;
import com.braintribe.codec.marshaller.api.GmSerializationOptions;
import com.braintribe.codec.marshaller.api.OutputPrettiness;
import com.braintribe.codec.marshaller.api.TypeExplicitness;
import com.braintribe.codec.marshaller.api.TypeExplicitnessOption;
import com.braintribe.codec.marshaller.yaml.YamlMarshaller;
import com.braintribe.common.lcd.function.CheckedFunction;
import com.braintribe.gm.config.api.ModeledConfiguration;
import com.braintribe.gm.config.yaml.index.ClasspathEntry;
import com.braintribe.gm.config.yaml.index.ClasspathIndex;
import com.braintribe.gm.model.reason.Maybe;
import com.braintribe.gm.model.reason.ReasonAggregator;
import com.braintribe.gm.model.reason.Reasons;
import com.braintribe.gm.model.reason.config.ConfigurationError;
import com.braintribe.logging.Logger;
import com.braintribe.model.generic.GMF;
import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.pr.AbsenceInformation;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EssentialTypes;
import com.braintribe.model.generic.reflection.MapType;
import com.braintribe.model.generic.reflection.Property;
import com.braintribe.model.generic.reflection.StandardTraversingContext;
import com.braintribe.utils.StringTools;
import com.braintribe.utils.lcd.Lazy;
import com.braintribe.utils.lcd.NullSafe;
import com.braintribe.ve.api.VirtualEnvironment;
import com.braintribe.ve.impl.StandardEnvironment;

/**
 * This implementation of {@link ModeledConfiguration} uses the filesystem and yaml marshalled modeled data to retrieve configurations.
 * 
 * <p>
 * The lookup strategy is:
 * 
 * <ol>
 * <li>build a filename using kebab cased variant of the {@link EntityType#getShortName() type short name} suffixed with <b>.yaml</b> and try to find
 * this file in the {@link #setConfigFolder(File) config folder}. (e.g. foo.bar.MyConfig -> &lt;config-folder&gt;/my-config.yaml)
 * <li>if the file was not found a default initialialized instance of the config type will be created.
 * </ol>
 * 
 * <p>
 * If the configuration is read from the filesystem the yaml unmarshalling supports default initialization, and variables such as:
 * 
 * <ul>
 * <li>${config.base} which references the {@link #setConfigFolder(File) config folder}
 * <li>${env.SOME_ENV_VAR} resolves OS environment variables
 * </ul>
 * 
 * <h2>Classpath Configurations (YAML)</h2>
 * <p>
 * 
 * The filename has the following structure: {@code $shortKebabName[~use-case][.disambiguator-priority].yaml}, where:
 * <ul>
 * <li>$shortKebabName is the dash separated variant of {@link EntityType#getShortName()}
 * <li>use-case is an optional suffix which is also used for resolution
 * <li>disambiguator is a string we need so we can have multiple files for the same type and use-case. For human readability it's recommended to use
 * something meaningful. However, it plays a role when sorting the files.
 * <li>priority is an integer used for sorting
 * </ul>
 * 
 * NOTE: that is exactly one of disambiguator and priority is specified, the dash between them is omitted.
 * 
 * All the files corresponding to a given configuration type and use-case will be merged via {@link EntityMerger}, with sorting order determined by
 * priority, disambiguator and artifactId (see example below).
 * 
 * <b>Examples:</b>
 * 
 * <pre>
 * artifactIdA:my-entity~use-case.yaml  			     // priority: -1, disambiguator: "",    aId: "artifactIdA"
 * artifactIdZ:my-entity~use-case.yaml                   // priority: -1, disambiguator: "",    aId: "artifactIdZ"
 * artifactIdA:my-entity~use-case.xyz-0.yaml             // priority:  0, disambiguator: "xyz", aId: "artifactIdA"
 * artifactIdA:my-entity~use-case.1.yaml                 // priority:  1, disambiguator: "",    aId: "artifactIdA"
 * artifactIdZ:my-entity~use-case.abc-2.yaml             // priority:  2, disambiguator: "abc", aId: "artifactIdZ"
 * artifactIdZ:my-entity~use-case.xyz-2.yaml             // priority:  2, disambiguator: "xyz", aId: "artifactIdZ"
 * </pre>
 * 
 * @author dirk.scheffler
 *
 */
public class ModeledYamlConfiguration implements ModeledConfiguration {

	private static final Logger logger = Logger.getLogger(ModeledYamlConfiguration.class);

	private final Map<ConfigKey, Lazy<Maybe<? extends GenericEntity>>> configs = new ConcurrentHashMap<>();
	private File configFolder;
	private VirtualEnvironment virtualEnvironment = StandardEnvironment.INSTANCE;
	private boolean writePooled;
	private final Lazy<Map<String, String>> properties = new Lazy<>(this::loadProperties);
	private Function<String, Maybe<String>> propertyLookup = reasonifyPropertyResolver(this::resolveStandardProperty);

	private ClasspathIndex classpathIndex;
	private String classpathConfPath = "";

	private final ConfigurationRegistry configRegistry = new ConfigurationRegistry();

	@Configurable
	public void setConfigFolder(File configFolder) {
		this.configFolder = configFolder;
	}

	@Configurable
	public void setClasspathIndex(ClasspathIndex classpathIndex) {
		this.classpathIndex = classpathIndex;
	}

	// e.g. rx/conf/ for loading from classpath
	@Configurable
	public void setClasspathConfPath(String classpathConfPath) {
		if (classpathConfPath == null || classpathConfPath.isBlank())
			return;

		if (classpathConfPath.startsWith("/"))
			classpathConfPath = classpathConfPath.substring(1);
		if (!classpathConfPath.endsWith("/"))
			classpathConfPath += "/";

		this.classpathConfPath = classpathConfPath;
	}

	public <E extends GenericEntity> void registerConfiguration( //
			String origin, EntityType<E> configType, String useCase, ConfigurationStage stage, int priority, Supplier<? extends E> configSupplier) {

		configRegistry.register(origin, configType, useCase, stage, priority, configSupplier);
	}

	@Configurable
	public void setExternalPropertyLookup(Function<String, String> externalPropertyLookup) {
		this.propertyLookup = reasonifyPropertyResolver(externalPropertyLookup);
	}

	@Configurable
	public void setExternalReasonedPropertyLookup(Function<String, Maybe<String>> externalReasonedPropertyLookup) {
		this.propertyLookup = externalReasonedPropertyLookup;
	}

	@Configurable
	public void setVirtualEnvironment(VirtualEnvironment virtualEnvironment) {
		this.virtualEnvironment = virtualEnvironment;
	}

	@Configurable
	public void setWritePooled(boolean writePooled) {
		this.writePooled = writePooled;
	}

	@Override
	public <C extends GenericEntity> C config(EntityType<C> configType, String useCase) {
		return configReasoned(configType, useCase).get();
	}

	public <C extends GenericEntity> void store(EntityType<C> configType, C config) {
		YamlMarshaller yamlMarshaller = new YamlMarshaller();
		yamlMarshaller.setWritePooled(writePooled);
		File configFile = new File(configFolder, buildConfigFileName(configType));

		GmSerializationOptions options = GmSerializationOptions.deriveDefaults() //
				.inferredRootType(configType).setOutputPrettiness(OutputPrettiness.high)
				.set(TypeExplicitnessOption.class, TypeExplicitness.polymorphic).build();

		try (OutputStream out = new BufferedOutputStream(new FileOutputStream(configFile))) {
			yamlMarshaller.marshall(out, config, options);
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}

		configs.remove(new ConfigKey(configType, ""));
	}

	private String buildConfigFileName(EntityType<?> type) {
		return StringTools.camelCaseToSocialDistancingCase(type.getShortName()).toLowerCase() + ".yaml";
	}

	public <C extends GenericEntity> void store(C config) {
		store(config.entityType(), config);
	}

	private record ConfigKey(EntityType<?> configType, String useCase) {
	}

	private record ConfigEntry(GenericEntity entity, String origin) {
	}

	@Override
	public <C extends GenericEntity> Maybe<C> configReasoned(EntityType<C> configType, String useCase) {
		// using the lazy initialized here is to avoid to block the map access and to do the actual loading afterwards
		var configKey = new ConfigKey(configType, useCase);
		return (Maybe<C>) configs.computeIfAbsent(configKey, k -> new Lazy<>(() -> this.loadConfig(configType, useCase))).get();
	}

	// load from all possible sources and merge
	private <C extends GenericEntity> Maybe<C> loadConfig(EntityType<C> configType, String useCase) {
		Maybe<List<ConfigEntry>> cpMaybe = readCpConfig(configType, useCase);
		Maybe<List<ConfigEntry>> fsMaybe = readFsConfig(configType, useCase);

		if (cpMaybe.isUnsatisfied() || fsMaybe.isUnsatisfied()) {
			var reasonAggregator = Reasons.aggregatorForceWrap(() -> ConfigurationError.create(//
					"Error while loading config of type [" + configType.getShortName() + "], use-case [" + useCase + "]"));

			reasonAggregator.acceptMaybe(cpMaybe);
			reasonAggregator.acceptMaybe(fsMaybe);

			return reasonAggregator.get().asMaybe();
		}

		List<ConfigEntry> preCp = configRegistry.findConfiguration(ConfigurationStage.beforeClasspath, configType, useCase);
		List<ConfigEntry> preFs = configRegistry.findConfiguration(ConfigurationStage.beforeConfDir, configType, useCase);
		List<ConfigEntry> afterAll = configRegistry.findConfiguration(ConfigurationStage.afterEverythingElse, configType, useCase);

		List<ConfigEntry> cp = cpMaybe.get();
		List<ConfigEntry> fs = fsMaybe.get();

		var reasonAggregator = Reasons.aggregatorForceWrap(() -> ConfigurationError.create(//
				"Error while merging config of type [" + configType.getShortName() + "], use-case [" + useCase + "]"));

		ConfigEntry finalEntry = null;
		finalEntry = mergeEntities(reasonAggregator, finalEntry, preCp);
		finalEntry = mergeEntities(reasonAggregator, finalEntry, cp);
		finalEntry = mergeEntities(reasonAggregator, finalEntry, preFs);
		finalEntry = mergeEntities(reasonAggregator, finalEntry, fs);
		finalEntry = mergeEntities(reasonAggregator, finalEntry, afterAll);

		C result = finalEntry != null ? (C) finalEntry.entity() : configType.create();

		deepDeabsentify(result);

		if (!reasonAggregator.hasReason())
			return Maybe.complete(result);
		else
			return Maybe.incomplete(result, reasonAggregator.get());
	}

	private ConfigEntry mergeEntities( //
			ReasonAggregator<ConfigurationError> reasonAggregator, ConfigEntry mergedEntry, List<ConfigEntry> configEntities) {

		for (ConfigEntry ce : configEntities) {
			if (mergedEntry == null) {
				mergedEntry = ce;
			} else {
				// TODO this would not work as entities do not have absent properties...

				Maybe<GenericEntity> mergeMaybe = EntityMerger.merge(ce.entity(), ce.origin(), mergedEntry.entity());
				if (mergeMaybe.isUnsatisfied())
					reasonAggregator.accept(mergeMaybe.whyUnsatisfied());

				mergedEntry = new ConfigEntry(mergeMaybe.value(), mergedEntry.origin() + "\n" + ce.origin());
			}
		}

		return mergedEntry;
	}

	private void deepDeabsentify(GenericEntity entity) {
		entity.entityType().traverse(new AbsenceInfoBustingTc(), entity);
	}

	class AbsenceInfoBustingTc extends StandardTraversingContext {
		@Override
		public boolean isAbsenceResolvable(Property property, GenericEntity entity, AbsenceInformation absenceInformation) {
			property.setDirectUnsafe(entity, property.getDefaultValue());
			return false;
		}
	}

	private String resolveStandardProperty(String name) {
		String value = properties.get().get(name);
		if (value != null)
			return value;

		value = virtualEnvironment.getProperty(name);
		if (value != null)
			return value;

		return virtualEnvironment.getEnv(name);
	}

	private Map<String, String> loadProperties() {
		// TODO read properties from classpath too?
		if (configFolder == null)
			return Collections.emptyMap();

		MapType configType = GMF.getTypeReflection().getMapType(EssentialTypes.TYPE_STRING, EssentialTypes.TYPE_STRING);
		File configFile = new File(configFolder, "properties.yaml");

		Maybe<Map<String, String>> propertiesMaybe = new ModeledYamlConfigurationLoader() //
				.virtualEnvironment(virtualEnvironment) //
				.loadConfig(configType, configFile, Collections::emptyMap, false);

		if (propertiesMaybe.isSatisfied())
			return propertiesMaybe.get();

		logger.error("Error while reading config properties from [" + configFile + "]: " + propertiesMaybe.whyUnsatisfied().stringify());

		return Collections.emptyMap();
	}

	// ######################################################
	// ## . . . . . . . . Config Providers . . . . . . . . ##
	// ######################################################

	// Classpath

	private Maybe<List<ConfigEntry>> readCpConfig(EntityType<?> configType, String useCase) {
		if (classpathIndex == null)
			return Maybe.complete(emptyList());

		String prefix = classpathConfPath + configFilePrefix(configType, useCase);
		List<ClasspathEntry> cpEntries = classpathIndex.forPrefix(prefix);

		if (cpEntries.isEmpty())
			return Maybe.complete(emptyList());

		List<ClasspathEntry> sortedEpEntries = ConfigurationEntrySorter.sortClasspathEntries(cpEntries);

		return listToEntities(configType, "classpath", sortedEpEntries, e -> e.url.openStream(), e -> e.url.toString());
	}

	// FileSystem

	private Maybe<List<ConfigEntry>> readFsConfig(EntityType<?> configType, String useCase) {
		if (configFolder == null)
			return Maybe.complete(emptyList());

		String prefix = configFilePrefix(configType, useCase);
		List<File> files = findConfigFiles(configFolder, prefix);

		if (files.isEmpty())
			return Maybe.complete(emptyList());

		List<File> sortedFiles = ConfigurationEntrySorter.sortFiles(files);

		return listToEntities(configType, "conf directory [" + configFolder.getPath() + "]", //
				sortedFiles, f -> new BufferedInputStream(new FileInputStream(f)), File::getAbsolutePath);
	}

	private List<File> findConfigFiles(File configFolder, String prefix) {
		File[] files = configFolder.listFiles((dir, name) -> name.startsWith(prefix) && name.endsWith(".yaml"));
		return files != null ? Arrays.asList(files) : emptyList();
	}

	private String configFilePrefix(EntityType<?> configType, String useCase) {
		String baseFileName = camelCaseToSocialDistancingCase(configType.getShortName());

		String prefix = baseFileName;
		if (useCase != null && !useCase.isBlank())
			prefix += "~" + useCase;
		prefix += ".";

		return prefix;
	}

	private <C extends GenericEntity, E> Maybe<List<ConfigEntry>> listToEntities( //
			EntityType<C> configType, String configSource, List<E> entries, //
			CheckedFunction<E, InputStream, IOException> inputStreamProvider, Function<E, String> originProvider) {

		var reasonAggregator = Reasons.aggregatorForceWrap(() -> ConfigurationError.create("Error while loading config from " + configSource));

		List<ConfigEntry> result = newList();
		for (E entry : entries) {
			Maybe<C> configMaybe = new ModeledYamlConfigurationLoader() //
					.virtualEnvironment(virtualEnvironment) //
					.variableResolverReasoned(propertyLookup) //
					.absentifyMissingProperties(true) //
					.loadConfig(configType, () -> inputStreamProvider.apply(entry));

			if (configMaybe.isSatisfied())
				result.add(new ConfigEntry(configMaybe.get(), originProvider.apply(entry)));
			else
				reasonAggregator.accept(configMaybe.whyUnsatisfied());
		}

		if (reasonAggregator.hasReason())
			return reasonAggregator.get().asMaybe();
		else
			return Maybe.complete(result);
	}

	//
	// Registered
	//

	class ConfigurationRegistry {

		private record RegistrationKey(EntityType<?> configType, String useCase, ConfigurationStage stage) {
		}

		private record Registration(String origin, int priority, Supplier<? extends GenericEntity> configSupplier)
				implements Comparable<Registration> {

			@Override
			public int compareTo(Registration other) {
				return Integer.compare(this.priority, other.priority);
			}

			public ConfigEntry toConfigEntry() {
				return new ConfigEntry(configSupplier.get(), origin);
			}
		}

		private final Map<RegistrationKey, List<Registration>> registrations = newMap();

		public <C extends GenericEntity> void register(String origin, EntityType<C> configType, String useCase, ConfigurationStage stage,
				int priority, Supplier<? extends C> configSupplier) {

			NullSafe.nonNull(configType, "configType");
			NullSafe.nonNull(stage, "configuration stage");
			NullSafe.nonNull(configSupplier, "configuration supplier");

			if (StringTools.isBlank(origin))
				origin = "<unknown origin>";
			if (useCase == null)
				useCase = "";

			var regKey = new RegistrationKey(configType, useCase, stage);
			var regVal = new Registration(origin, priority, configSupplier);

			acquireList(registrations, regKey).add(regVal);
		}

		public <C extends GenericEntity> List<ConfigEntry> findConfiguration(ConfigurationStage stage, EntityType<C> configType, String useCase) {
			var regKey = new RegistrationKey(configType, useCase, stage);
			List<Registration> regs = registrations.get(regKey);

			if (regs == null)
				return emptyList();

			return regs.stream() //
					.sorted() //
					.map(Registration::toConfigEntry) //
					.toList();
		}

	}

}
