/*******************************************************************************
 * Copyright (c) 2010-2015, stampie, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   stampie - initial API and implementation
 *******************************************************************************/
package org.eclipse.incquery.runtime.matchers.util;

import com.google.common.base.Preconditions;

/**
 * A provider implementation that always returns the same object instance. 
 * @author Zoltan Ujhelyi
 */
public class SingletonInstanceProvider<T> implements IProvider<T>{

	private T instance;

	public SingletonInstanceProvider(T instance) {
		Preconditions.checkArgument(instance != null, "Instance parameter must not be null.");
		this.instance = instance;
	}
	
	@Override
	public T get() {
		return instance;
	}
	
}