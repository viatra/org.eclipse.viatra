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
package org.eclipse.viatra.query.runtime.emf.types;

import org.eclipse.emf.ecore.EDataType;
import org.eclipse.viatra.query.runtime.emf.helper.ViatraQueryRuntimeHelper;

/**
 * Instance tuples are of form (x), where x is an instance of the given eDataType residing at an attribute slot of an eObject in the model.
 * @author Bergmann Gabor
 *
 */
public class EDataTypeInSlotsKey extends BaseEMFTypeKey<EDataType> {

	/**
	 * @param emfKey
	 */
	public EDataTypeInSlotsKey(EDataType emfKey) {
		super(emfKey);
	}

	@Override
	public String getPrettyPrintableName() {
		return "(Attribute Slot Values: " + ViatraQueryRuntimeHelper.prettyPrintEMFType(wrappedKey) + ")";
	}

	@Override
	public String getStringID() {
		return "slotValue#" + ViatraQueryRuntimeHelper.prettyPrintEMFType(wrappedKey);
	}

	@Override
	public int getArity() {
		return 1;
	}
	
	@Override
	public boolean isEnumerable() {
		return true;
	}
	
}
