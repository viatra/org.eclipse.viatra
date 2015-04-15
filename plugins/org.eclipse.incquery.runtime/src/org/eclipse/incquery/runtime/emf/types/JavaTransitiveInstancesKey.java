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
package org.eclipse.incquery.runtime.emf.types;

/**
 * Instance tuples are of form (x), where object x is an instance of the given Java class or its subclasses.
 * <p> Non-enumerable type, can only be checked.
 * @author Bergmann Gabor
 *
*/
public class JavaTransitiveInstancesKey extends BaseEMFTypeKey<Class<?>> {

	public JavaTransitiveInstancesKey(Class<?> instanceClass) {
		super(instanceClass);
	}

	@Override
	public String getPrettyPrintableName() {
		return emfKey.getName();
	}

	@Override
	public String getStringID() {
		return "javaClass#"+ emfKey.getName();
	}

	@Override
	public int getArity() {
		return 1;
	}

}
