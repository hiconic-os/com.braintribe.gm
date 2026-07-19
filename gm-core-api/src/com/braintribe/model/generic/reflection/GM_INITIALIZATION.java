package com.braintribe.model.generic.reflection;

/**
 * This class is used to disable the automatic T literal initialization of {@link EntityType}s and {@link EnumType}s, performed by {@link EntityTypes}
 * and {@link EnumTypes}.
 * <p>
 * This is necessary when trying to scan classes of an artifact, e.g. when generated TS reflection.
 * <p>
 * Why? Because when scanning classes, we load the scanned artifact and all its dependencies with a (ReverseOrderURLClassLoader). However, in cases
 * like having an annotation with a GM Enum as value, it leads to an instantiation of that type (i.e. that enum) and that triggers T literal
 * initialization. Without any measures, it would then try to load the {@link GenericModelTypeReflection}, which would lead to platform
 * initialization, which creates a very complex situation that usually ends with an error.
 * 
 * @author peter.gazdik
 */
public class GM_INITIALIZATION {

	public static boolean T_LITERAL_INIT_ENABLED = true;

	// This method is not used, but ensures the T_LITERAL_INIT_ENABLED field is modifiable!!!
	void disableTLiteralInitialization() {
		T_LITERAL_INIT_ENABLED = false;
	}

}
