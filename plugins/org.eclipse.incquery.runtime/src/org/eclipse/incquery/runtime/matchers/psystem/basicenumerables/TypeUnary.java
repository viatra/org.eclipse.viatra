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

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.incquery.runtime.emf.types.EClassTransitiveInstancesKey;
import org.eclipse.incquery.runtime.emf.types.EDataTypeInSlotsKey;
import org.eclipse.incquery.runtime.matchers.psystem.PBody;
import org.eclipse.incquery.runtime.matchers.psystem.PVariable;
import org.eclipse.incquery.runtime.matchers.tuple.FlatTuple;

/**
 * This class exists only to maintain compatibility with old generated code.
 * @author Bergmann Gabor
 * @deprecated use {@link TypeConstraint}
 */
@Deprecated
public class TypeUnary {
	@Deprecated
	public TypeUnary(PBody body, PVariable var, EClassifier classifier, String ignored) {
		if (classifier instanceof EClass)
			new TypeConstraint(body, new FlatTuple(var), new EClassTransitiveInstancesKey((EClass) classifier));
		else 
			new TypeConstraint(body, new FlatTuple(var), new EDataTypeInSlotsKey((EDataType) classifier));
	}
}
