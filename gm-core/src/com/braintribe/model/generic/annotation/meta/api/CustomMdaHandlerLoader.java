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
package com.braintribe.model.generic.annotation.meta.api;

import static com.braintribe.utils.lcd.CollectionTools2.asLinkedSet;
import static com.braintribe.utils.lcd.CollectionTools2.asList;
import static com.braintribe.utils.lcd.CollectionTools2.newList;
import static com.braintribe.utils.lcd.CollectionTools2.newSet;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.annotation.Repeatable;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.invoke.MethodType;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import com.braintribe.logging.Logger;
import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.annotation.meta.api.synthesis.SingleAnnotationDescriptor;
import com.braintribe.model.generic.annotation.meta.base.BasicMdaHandler;
import com.braintribe.model.generic.annotation.meta.base.BasicRepeatableMdaHandler;
import com.braintribe.model.generic.reflection.Property;
import com.braintribe.model.generic.reflection.TypeCode;
import com.braintribe.model.meta.data.MetaData;

/**
 * Loader for custom {@link MdaHandler}s from classpath files under {@value #MDA_LOCATION}.
 * <p>
 * Full documentation for custom MDA handlers is on {@link MdaHandler} (rather than here) as that class is public.
 * 
 * @author peter.gazdik
 */
/* package */ class CustomMdaHandlerLoader {

	public static void load(InternalMdaRegistry registry) {
		new CustomMdaHandlerLoader(registry).run();
	}

	// ####################################################
	// ## . . . . . . . . Implementation . . . . . . . . ##
	// ####################################################

	/* package */ static final String MDA_LOCATION = "META-INF/gmf.mda";

	private static final Logger log = Logger.getLogger(CustomMdaHandlerLoader.class);

	// -----------------

	private final InternalMdaRegistry registry;

	private final ClassLoader classLoader = GenericEntity.class.getClassLoader();
	private final Lookup lookup = MethodHandles.lookup();

	private URL currentUrl;
	private String currentLine;
	private String[] entries;

	private Class<? extends Annotation> annoClass;
	private Class<? extends Annotation> aggregatorAnnoClass;
	private Class<? extends MetaData> mdClass;

	private CustomMdaHandlerLoader(InternalMdaRegistry registry) {
		this.registry = registry;
	}

	private void run() {

		Enumeration<URL> declarationUrls = null;
		try {
			declarationUrls = classLoader.getResources(MDA_LOCATION);
		} catch (IOException e) {
			log.error("Error while retrieving configurer files (gm.configurer) on classpath of classloader: " + classLoader, e);
			return;
		}

		StringBuilder sb = new StringBuilder();

		while (declarationUrls.hasMoreElements())
			processUrl(declarationUrls.nextElement(), sb);

		log.debug("Loaded custom MDA handlers:\n" + sb.toString());
	}

	private void processUrl(URL url, StringBuilder sb) {
		currentUrl = url;

		sb.append(" - ").append(currentUrl.toString());

		try (Scanner scanner = new Scanner(url.openStream())) {
			while (scanner.hasNextLine())
				processLine(scanner.nextLine());

		} catch (Exception e) {
			log.error("Error while parsing configurators from " + url, e);
		}
	}

	private void processLine(String line) {
		line = line.trim();
		if (line.isEmpty())
			return;

		currentLine = line;

		entries = line.split(",");
		if (entries.length < 2) {
			logError("Line must contain at least 2 comma separated entries. "
					+ "First entry is the annotation class, second entry is the entity MD class (signature).");
			return;
		}

		if (!loadBothTypes())
			return;

		if (entries.length % 2 == 1) {
			logError("Line must contain an even number of entries. First entry is the annotation class, second entry is "
					+ "the entity MD class (signature), followd by pairs of annotation attribute and property name. "
					+ "E.g. 'pckg.MyAnnotation, pkg.MyMd,value,property1,attribute2,property2'");
			return;
		}

		if (configureValidLookingEntry())
			logInfo("Successfully registered MdaHandler for annotation " + annoClass.getName() + " and meta-data " + mdClass.getName());
	}

	private boolean loadBothTypes() {
		annoClass = getClassSafe(entries[0].trim(), Annotation.class);
		if (annoClass == null)
			return false;

		mdClass = getClassSafe(entries[1].trim(), MetaData.class);
		if (mdClass == null)
			return false;

		aggregatorAnnoClass = findAggregatorAnnoClassIfExists();

		return true;
	}

	private Class<? extends Annotation> findAggregatorAnnoClassIfExists() {
		if (annoClass.isAnnotationPresent(Repeatable.class))
			return annoClass.getAnnotation(Repeatable.class).value();
		else
			return null;
	}

	private <T> Class<? extends T> getClassSafe(String className, Class<T> superType) {
		try {
			Class<?> clazz = Class.forName(className, false, classLoader);
			return clazz.asSubclass(superType);

		} catch (ClassNotFoundException e) {
			logError(superType.getSimpleName() + " " + className + " class not found.");
			return null;

		} catch (ClassCastException e) {
			logError("Class is not a " + superType.getSimpleName() + ": " + className);
			return null;
		}
	}

	private boolean configureValidLookingEntry() {
		if (entries.length == 2)
			return processPredicateMda();
		else if (aggregatorAnnoClass == null)
			return processRegularMda();
		else
			return processRepeatableMda();
	}

	// ###############################################
	// ## . . . . . . Predicate Handler . . . . . . ##
	// ###############################################

	private boolean processPredicateMda() {
		MethodHandle globalIdHandle = findGlobalIdHandle();
		if (globalIdHandle == null)
			return false;

		registry.register(new BasicMdaHandler<>( //
				annoClass, //
				mdClass, //
				anno -> (String) readAttribute(anno, globalIdHandle, "globalId") //
		));

		return true;
	}

	// ###############################################
	// ## . . . . . . . Regular Handler . . . . . . ##
	// ###############################################

	private boolean processRegularMda() {
		MdaHandler<?, ?> mdaHandler = createRegularMdaHandler();
		if (mdaHandler == null)
			return false;

		registry.register(mdaHandler);
		return true;
	}

	private BasicMdaHandler<?, ?> createRegularMdaHandler() {
		MethodHandle globalIdHandle = findGlobalIdHandle();
		if (globalIdHandle == null)
			return null;

		List<AttributeToProperty> atps = resolveAtps();

		return new BasicMdaHandler<>( //
				annoClass, //
				mdClass, //
				// anno.globalId(): String
				anno -> (String) readAttribute(anno, globalIdHandle, "globalId"), //
				(ctx, anno, md) -> {
					for (AttributeToProperty atp : atps)
						copyFromAnnotationToMd(atp, anno, md);

				}, //
				(ctx, descriptor, md) -> {
					for (AttributeToProperty atp : atps)
						copyFromMdToAnnotationDescriptor(atp, md, descriptor);
				} //
		);
	}

	// ###############################################
	// ## . . . . . . Repeatable Handler . . . . . .##
	// ###############################################

	private boolean processRepeatableMda() {
		RepeatableMdaHandler<?, ?, ?> repeatableHandler = createRepeatableMdaHandler();
		if (repeatableHandler == null)
			return false;

		registry.registerRepeatable(repeatableHandler);
		return true;
	}

	private RepeatableMdaHandler<?, ?, ?> createRepeatableMdaHandler() {
		MethodHandle globalIdHandle = findGlobalIdHandle();
		if (globalIdHandle == null)
			return null;

		MethodHandle valueOfAggregatorHandle = findValueOfAggregatorHandle();
		if (valueOfAggregatorHandle == null)
			return null;

		List<AttributeToProperty> atps = resolveAtps();

		return new BasicRepeatableMdaHandler<>( //
				annoClass, //
				aggregatorAnnoClass, //
				mdClass, //
				// anno.globalId(): String
				anno -> readAttribute(anno, globalIdHandle, "globalId"), //
				// repeatableAnno.values(): A[] // annoClass is Class<A>
				repeatablAnno -> readAttribute(repeatablAnno, valueOfAggregatorHandle, "value"), //
				(ctx, anno, md) -> {
					for (AttributeToProperty atp : atps)
						copyFromAnnotationToMd(atp, anno, md);

				}, //
				(ctx, descriptor, md) -> {
					for (AttributeToProperty atp : atps)
						copyFromMdToAnnotationDescriptor(atp, md, descriptor);
				} //
		);
	}

	// ###############################################
	// ## . . . . . . . . Commons . . . . . . . . . ##
	// ###############################################

	private String attribute;
	private String propertyName;
	private final Set<String> attributeNames = newSet();

	private List<AttributeToProperty> resolveAtps() {
		List<AttributeToProperty> result = newList();

		int i = 2;

		while (i < entries.length) {
			attribute = entries[i++];
			propertyName = entries[i++];

			attributeNames.add(attribute);

			addAtpIfAnnoSupportsAttribute(result, false);
		}

		// We map the props automatically if the attributes were not mentioned in the mapping, but exit
		// E.g. annotation.inherited(): boolean -> MetaData.inherited
		addAtpIfPossible(result, MetaData.inherited);
		addAtpIfPossible(result, MetaData.important);

		// We currently don't support MetaData.conflictPriority - it's of type 'Double' and Java annos only allow 'double'.

		return result;
	}

	private void addAtpIfPossible(List<AttributeToProperty> result, String attrAndPropName) {
		if (!attributeNames.contains(attrAndPropName)) {
			attribute = propertyName = attrAndPropName;
			addAtpIfAnnoSupportsAttribute(result, true);
		}
	}

	private void addAtpIfAnnoSupportsAttribute(List<AttributeToProperty> result, boolean ignoreIfMissing) {
		Class<?> type = resolveAttributeType(ignoreIfMissing);
		if (type == null)
			return;

		MethodHandle methodHandle = findHandle(annoClass, attribute, type);
		if (methodHandle == null)
			return;

		if (verifyPropertyExists(type))
			result.add(new AttributeToProperty(attribute, methodHandle, propertyName));
	}

	private Class<?> resolveAttributeType(boolean ignoreIfMissing) {
		try {
			Method m = annoClass.getMethod(attribute);

			return m.getReturnType();

		} catch (NoSuchMethodException e) {
			if (!ignoreIfMissing)
				logError("Annotation type " + annoClass.getName() + " has no " + attribute + "() attribute.");
			return null;

		} catch (SecurityException e) {
			throw new RuntimeException("Cannot access " + attribute + "() method of MD Annotation " + annoClass.getName(), e);
		}
	}

	private boolean verifyPropertyExists(Class<?> type) {
		Method m = getGetter(propertyName);
		if (m == null)
			return false;

		if (!type.isArray()) {
			if (m.getReturnType() != type)
				return logWrongTypeAndReturnFalse(type, m);
			else
				return true;
		}

		Class<?> rawReturnType = m.getReturnType();
		if (rawReturnType != List.class && rawReturnType != Set.class)
			return logWrongTypeAndReturnFalse(type, m);

		Class<?> componentType = type.getComponentType();

		// We know this is List<?> or Set<?>, we have to check: ? == componentType
		Type genericReturnType = m.getGenericReturnType();
		if (genericReturnType instanceof ParameterizedType) {
			ParameterizedType pt = (ParameterizedType) genericReturnType;

			// We check that typeArguments == [componentType]
			Type[] typeArguments = pt.getActualTypeArguments();
			if (typeArguments.length == 1 && typeArguments[0] == componentType)
				return true;
		}

		return logWrongTypeAndReturnFalse(type, m);
	}

	private boolean logWrongTypeAndReturnFalse(Class<?> type, Method m) {
		logError("MD Entity type " + mdClass.getName() + " has a property " + propertyName + " of type " + m.getReturnType().getName()
				+ ". This doesn't match the type " + type.getName() + " of the corresponding annotation attribute " + attribute + " of "
				+ annoClass.getName());
		return false;
	}

	private Method getGetter(String propertyName) {
		String getterName = "get" + Character.toUpperCase(propertyName.charAt(0)) + propertyName.substring(1);

		try {
			return mdClass.getMethod(getterName);

		} catch (NoSuchMethodException e) {
			logError("MD Entity type " + mdClass.getName() + " has no getter for property " + propertyName);
			return null;

		} catch (SecurityException e) {
			throw new RuntimeException("Cannot getter " + getterName + "() of MD Entity type " + mdClass.getName(), e);
		}
	}

	private MethodHandle findGlobalIdHandle() {
		return findHandle(annoClass, "globalId", String.class);
	}

	private MethodHandle findValueOfAggregatorHandle() {
		return findHandle(aggregatorAnnoClass, "value", arrayType(annoClass));
	}

	private static Class<?> arrayType(Class<?> clazz) {
		// Copied from Class.arrayType, introduced in Java 12
		return Array.newInstance(clazz, 0).getClass();
	}

	private MethodHandle findHandle(Class<?> clazz, String attribute, Class<?> type) {
		try {
			return lookup.findVirtual(clazz, attribute, MethodType.methodType(type));

		} catch (NoSuchMethodException e) {
			logError("Annotation type " + annoClass.getName() + " has no " + attribute + "() attribute.");
			return null;

		} catch (IllegalAccessException e) {
			throw new RuntimeException("Cannot access " + attribute + "() method of MD Annotation " + annoClass.getName(), e);
		}
	}

	private void copyFromAnnotationToMd(AttributeToProperty atp, Annotation anno, MetaData md) {
		atp.ensureProperty(md);

		Object value = readAttribute(anno, atp.methodHandle, atp.attribute);
		value = convertToCollectionIfArray(value, atp);

		md.write(atp.property, value);
	}

	private Object convertToCollectionIfArray(Object value, AttributeToProperty atp) {
		if (value == null)
			return null;

		if (!value.getClass().isArray())
			return value;

		if (atp.property.getType().getTypeCode() == TypeCode.listType)
			return asList((Object[]) value);
		else
			return asLinkedSet((Object[]) value);
	}

	private void copyFromMdToAnnotationDescriptor(AttributeToProperty atp, MetaData md, SingleAnnotationDescriptor descriptor) {
		atp.ensureProperty(md);

		Object value = md.read(atp.property);
		value = convertToArrayIfCollection(value);

		descriptor.addAnnotationValue(atp.attribute, value);
	}

	private Object convertToArrayIfCollection(Object value) {
		if (value instanceof Collection)
			return ((Collection<?>) value).toArray();
		else
			return value;
	}

	private <T> T readAttribute(Annotation anno, MethodHandle handle, String attribute) {
		try {
			return (T) handle.invoke(anno);
		} catch (Throwable e) {
			throw new RuntimeException("Error while reading " + attribute + " from " + anno.getClass().getName() + " Annotation " + anno, e);
		}
	}

	static class AttributeToProperty {
		public final String attribute;
		public final MethodHandle methodHandle;
		public final String propertyName;
		public Property property;

		public AttributeToProperty(String attribute, MethodHandle methodHandle, String propertyName) {
			this.attribute = attribute;
			this.methodHandle = methodHandle;
			this.propertyName = propertyName;
		}

		public void ensureProperty(MetaData md) {
			if (property == null)
				property = md.entityType().getProperty(propertyName);
		}
	}

	private void logError(String message) {
		log.error("[MdaHandler CP Loader] " + message + ". Line: " + currentLine + ", URL: " + currentUrl);
	}

	private void logInfo(String message) {
		log.info("[MdaHandler CP Loader] " + message + ". Line: " + currentLine + ", URL: " + currentUrl);
	}

}
