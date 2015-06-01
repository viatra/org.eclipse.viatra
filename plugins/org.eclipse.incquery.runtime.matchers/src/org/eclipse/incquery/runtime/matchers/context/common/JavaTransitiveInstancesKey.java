/*******************************************************************************
 * Copyright (c) 2010-2015, Bergmann Gabor, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Bergmann Gabor - initial API and implementation
 *******************************************************************************/
package org.eclipse.incquery.runtime.matchers.context.common;

import org.apache.commons.lang.ClassUtils;


/**
 * Instance tuples are of form (x), where object x is an instance of the given Java class or its subclasses.
 * <p> Classes with the same name are considered equivalent. 
 * Can be instantiated with class name, even if the class itself is not loaded yet; but if the class is available, passing it in the constructor is beneficial to avoid classloading troubles.  
 * <p> Non-enumerable type, can only be checked.
 * <p> Stateless type (objects can't change their type)
 * @author Bergmann Gabor
 *
*/
public class JavaTransitiveInstancesKey extends BaseInputKeyWrapper<String> {
	
	/**
	 * The actual Class whose (transitive) instances this relation contains. Can be null at compile time, if only the name is available.
	 * Can be a primitive.  
	 */
	private Class<?> cachedInstanceClass;
	
	/**
	 * Same as {@link #cachedInstanceClass}, but primitive classes are replaced with their wrapper classes (e.g. int --> java.lang.Integer).
	 */
	private Class<?> cachedWrapperInstanceClass;

	/**
	 * Preferred constructor.
	 */
	public JavaTransitiveInstancesKey(Class<?> instanceClass) {
		this(ClassUtils.primitiveToWrapper(instanceClass).getName());
		this.cachedInstanceClass = instanceClass;
	}
	
	/**
	 * Call this constructor only in contexts where the class itself is not available for loading, e.g. it has not yet been compiled.
	 */
	public JavaTransitiveInstancesKey(String className) {
		super(className);
	}
	

	/**
	 * Returns null if class cannot be loaded.
	 */
	public Class<?> getInstanceClass() {
		if (cachedInstanceClass == null) {
			try {
				resolveClassInternal();
			} catch (ClassNotFoundException e) {
				// class not yet available at this point
			}
		}
		return cachedInstanceClass;
	}


	/**
	 * @return non-null instance class
	 * @throws ClassNotFoundException 
	 */
	public Class<?> forceGetInstanceClass() throws ClassNotFoundException {
		if (cachedInstanceClass == null) {
			resolveClassInternal();
		}
		return cachedInstanceClass;
	}
	
	/**
	 * @return non-null instance class, wrapped if primitive class
	 * @throws ClassNotFoundException 
	 */
	public Class<?> forceGetWrapperInstanceClass() throws ClassNotFoundException {
		forceGetInstanceClass();
		return getWrapperInstanceClass();
	}
	
	/**
	 * @return instance class, wrapped if primitive class, null if class cannot be loaded
	 */
	public Class<?> getWrapperInstanceClass()  {
		if (cachedWrapperInstanceClass == null) {
			cachedWrapperInstanceClass = ClassUtils.primitiveToWrapper(getInstanceClass());
		}
		return cachedWrapperInstanceClass;
	}
	
	private void resolveClassInternal() throws ClassNotFoundException {
		cachedInstanceClass = Class.forName(wrappedKey);
	}
	
	@Override
	public String getPrettyPrintableName() {
		getWrapperInstanceClass();
		return cachedWrapperInstanceClass == null ? wrappedKey == null ? "<null>" : wrappedKey : cachedWrapperInstanceClass.getName();
	}

	@Override
	public String getStringID() {
		return "javaClass#"+ getPrettyPrintableName();
	}

	@Override
	public int getArity() {
		return 1;
	}
	
	@Override
	public boolean isEnumerable() {
		return false;
	}
	
	@Override
	public String toString() {
		return this.getPrettyPrintableName();
	}


}
