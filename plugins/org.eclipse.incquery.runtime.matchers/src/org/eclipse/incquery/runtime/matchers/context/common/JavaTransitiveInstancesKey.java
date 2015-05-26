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
	
	private Class<?> cachedInstanceClass;

	/**
	 * Preferred constructor.
	 */
	public JavaTransitiveInstancesKey(Class<?> instanceClass) {
		this(instanceClass.getName());
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
	
	private void resolveClassInternal() throws ClassNotFoundException {
		cachedInstanceClass = Class.forName(wrappedKey);
	}
	
	@Override
	public String getPrettyPrintableName() {
		return wrappedKey;
	}

	@Override
	public String getStringID() {
		return "javaClass#"+ wrappedKey;
	}

	@Override
	public int getArity() {
		return 1;
	}
	
	@Override
	public boolean isEnumerable() {
		return false;
	}

}
