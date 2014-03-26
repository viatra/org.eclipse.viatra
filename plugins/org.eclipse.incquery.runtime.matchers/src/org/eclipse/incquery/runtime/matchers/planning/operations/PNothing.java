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

import java.util.Collections;
import java.util.Set;

import org.eclipse.incquery.runtime.matchers.psystem.PConstraint;


/**
 * Unit element of joins: no variables, no constraints. Satisfied by a single empty tuple.
 * 
 * <p> should only be used as a "virtual parent" in extreme cases, such as pattern foo(Bar) = {Bar = eval (3*4)}  
 * 
 * @author Bergmann Gabor
 *
 */
public class PNothing extends POperation {

	@Override
	public String getShortDebugName() {
		return "NOTHING";
	}

	@Override
	public Set<? extends PConstraint> getDeltaConstraints() {
		return Collections.emptySet();
	}

	@Override
	public int hashCode() {
		return getClass().hashCode();
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof PNothing))
			return false;
		return true;
	}
	
	
}
