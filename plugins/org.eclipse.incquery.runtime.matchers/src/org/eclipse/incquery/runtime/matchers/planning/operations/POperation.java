/*******************************************************************************
 * Copyright (c) 2010-2014, Bergmann Gabor, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Bergmann Gabor - initial API and implementation
 *******************************************************************************/
package org.eclipse.incquery.runtime.matchers.planning.operations;

import java.util.Set;

import org.eclipse.incquery.runtime.matchers.psystem.PConstraint;

/**
 * Abstract superclass for representing a high-level query evaluation operation.
 * 
 *  <p> Subclasses correspond to various POperations modeled after relational algebra.  
 * 
 * @author Bergmann Gabor
 *
 */
public abstract class POperation {

	/**
	 * Newly enforced constraints
	 */
	public abstract Set<? extends PConstraint> getDeltaConstraints();
	
	public abstract String getShortDebugName();
		
	@Override
	public String toString() {
		return getShortDebugName();
	}

}
