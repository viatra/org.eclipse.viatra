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

import org.eclipse.incquery.runtime.matchers.psystem.EnumerablePConstraint;
import org.eclipse.incquery.runtime.matchers.psystem.PConstraint;

/**
 * Represents a base relation defined by the instance set of an enumerable PConstraint.
 * @author Bergmann Gabor
 *
 */
public class PEnumerate extends POperation {

	EnumerablePConstraint enumerablePConstraint;
	
	public PEnumerate(EnumerablePConstraint enumerablePConstraint) {
		super();
		this.enumerablePConstraint = enumerablePConstraint;
	}
	public EnumerablePConstraint getEnumerablePConstraint() {
		return enumerablePConstraint;
	}

	@Override
	public Set<? extends PConstraint> getDeltaConstraints() {
		return Collections.singleton(enumerablePConstraint);
	}

	@Override
	public String getShortDebugName() {
		return enumerablePConstraint.toString();
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((enumerablePConstraint == null) ? 0 : enumerablePConstraint
						.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof PEnumerate))
			return false;
		PEnumerate other = (PEnumerate) obj;
		if (enumerablePConstraint == null) {
			if (other.enumerablePConstraint != null)
				return false;
		} else if (!enumerablePConstraint.equals(other.enumerablePConstraint))
			return false;
		return true;
	}
	
}
