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
package org.eclipse.incquery.runtime.matchers.psystem.basicenumerables;

import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.incquery.runtime.emf.types.EStructuralFeatureInstancesKey;
import org.eclipse.incquery.runtime.matchers.psystem.PBody;
import org.eclipse.incquery.runtime.matchers.psystem.PVariable;
import org.eclipse.incquery.runtime.matchers.tuple.FlatTuple;

/**
 * This class exists only to maintain compatibility with old generated code.
 * @author Bergmann Gabor
 * @deprecated use {@link TypeConstraint}
 */
@Deprecated
public class TypeBinary {
	@Deprecated
	public TypeBinary(PBody body, Object ignored1, PVariable varSource, PVariable varTarget, EStructuralFeature feature, String ignored2) {
		new TypeConstraint(body, new FlatTuple(varSource, varTarget), new EStructuralFeatureInstancesKey(feature));
	}
}
