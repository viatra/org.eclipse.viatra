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

import org.eclipse.incquery.runtime.matchers.context.IInputKey;

/**
 * Base class for EMF Type keys. 
 * @author Bergmann Gabor
 *
 */
public abstract class BaseEMFTypeKey<EMFKey> implements IInputKey {
	protected EMFKey emfKey;

	public BaseEMFTypeKey(EMFKey emfKey) {
		super();
		this.emfKey = emfKey;
	}

	public EMFKey getEmfKey() {
		return emfKey;
	}

	public void setEmfKey(EMFKey emfKey) {
		this.emfKey = emfKey;
	}

	@Override
	public int hashCode() {
		return ((emfKey == null) ? 0 : emfKey.hashCode());
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(this.getClass().equals(obj.getClass())))
			return false;
		BaseEMFTypeKey other = (BaseEMFTypeKey) obj;
		if (emfKey == null) {
			if (other.emfKey != null)
				return false;
		} else if (!emfKey.equals(other.emfKey))
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		return this.getPrettyPrintableName();
	}
	

}
