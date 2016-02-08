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

import org.eclipse.incquery.runtime.matchers.context.common.BaseInputKeyWrapper;

/**
 * Base class for EMF Type keys. 
 * @author Bergmann Gabor
 *
 */
public abstract class BaseEMFTypeKey<EMFKey> extends BaseInputKeyWrapper<EMFKey> {

	public BaseEMFTypeKey(EMFKey emfKey) {
		super(emfKey);
	}

	public EMFKey getEmfKey() {
		return getWrappedKey();
	}
	
	@Override
	public String toString() {
		return this.getPrettyPrintableName();
	}
	

}
