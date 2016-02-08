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

import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.incquery.runtime.emf.helper.IncQueryRuntimeHelper;

/**
 * Instance tuples are of form (x, y), where x is an eObject that has y as the value of the given feature (or one of the values in case of multi-valued).
 * @author Bergmann Gabor
 *
 */
public class EStructuralFeatureInstancesKey extends BaseEMFTypeKey<EStructuralFeature> {
	
	public EStructuralFeatureInstancesKey(EStructuralFeature emfKey) {
		super(emfKey);
	}

	@Override
	public String getPrettyPrintableName() {
		return IncQueryRuntimeHelper.prettyPrintEMFType(wrappedKey);
	}

	@Override
	public String getStringID() {
		return "feature#"+ getPrettyPrintableName();
	}

	@Override
	public int getArity() {
		return 2;
	}
	
	@Override
	public boolean isEnumerable() {
		return true;
	}

}
