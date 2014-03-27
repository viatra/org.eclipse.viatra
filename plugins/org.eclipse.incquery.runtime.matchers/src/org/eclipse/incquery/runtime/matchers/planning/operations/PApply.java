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
 * Represents a constraint application on a single parent SubPlan. 
 * <p> Either a "selection" filter operation according to a deferred PConstraint (or transform in case of eval/aggregate), or
 * alternatively a shorthand for PJoin + a PEnumerate on the right input for an enumerable PConstraint.
 * 
 * @author Bergmann Gabor
 *
 */
public class PApply extends POperation {
	
	private PConstraint pConstraint;

	public PApply(PConstraint pConstraint) {
		super();
		this.pConstraint = pConstraint;
	}
	public PConstraint getPConstraint() {
		return pConstraint;
	}

	@Override
	public String getShortName() {
		return String.format("APPLY_%s", pConstraint.toString());
	}
	
	@Override
	public Set<? extends PConstraint> getDeltaConstraints() {
		return Collections.singleton(pConstraint);
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((pConstraint == null) ? 0 : pConstraint
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
		if (pConstraint == null) {
			if (other.pConstraint != null)
				return false;
		} else if (!pConstraint.equals(other.pConstraint))
			return false;
		return true;
	}

}
