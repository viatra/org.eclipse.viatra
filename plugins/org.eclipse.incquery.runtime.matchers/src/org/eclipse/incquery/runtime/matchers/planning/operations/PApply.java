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

import org.eclipse.incquery.runtime.matchers.psystem.DeferredPConstraint;
import org.eclipse.incquery.runtime.matchers.psystem.PConstraint;

/**
 * Represents a "selection" filter operation according to a deferred PConstraint (or transform in case of eval/aggregate).
 * 
 * @author Bergmann Gabor
 *
 */
public class PApply extends POperation {
	
	private DeferredPConstraint deferredPConstraint;

	public PApply(DeferredPConstraint deferredPConstraint) {
		super();
		this.deferredPConstraint = deferredPConstraint;
	}
	public DeferredPConstraint getDeferredPConstraint() {
		return deferredPConstraint;
	}

	@Override
	public String getShortDebugName() {
		return String.format("APPLY_%s", deferredPConstraint.toString());
	}
	
	@Override
	public Set<? extends PConstraint> getDeltaConstraints() {
		return Collections.singleton(deferredPConstraint);
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((deferredPConstraint == null) ? 0 : deferredPConstraint
						.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof PApply))
			return false;
		PApply other = (PApply) obj;
		if (deferredPConstraint == null) {
			if (other.deferredPConstraint != null)
				return false;
		} else if (!deferredPConstraint.equals(other.deferredPConstraint))
			return false;
		return true;
	}

}
