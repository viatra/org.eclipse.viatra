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
import java.util.HashSet;
import java.util.Set;

import org.eclipse.incquery.runtime.matchers.psystem.PConstraint;
import org.eclipse.incquery.runtime.matchers.psystem.PVariable;

import com.google.common.base.Joiner;

/**
 * Represents a natural join.
 * @author Bergmann Gabor
 *
 */
public class PJoin extends POperation {
	
	// TODO leave here? is this a problem in equivalnece checking?
	private Set<PVariable> onVariables;
	

	public PJoin(Set<PVariable> onVariables) {
		super();
		this.onVariables = new HashSet<PVariable>(onVariables);
	}
	public Set<PVariable> getOnVariables() {
		return onVariables;
	}

	@Override
	public Set<? extends PConstraint> getDeltaConstraints() {
		return Collections.emptySet();
	}

	@Override
	public String getShortDebugName() {
		return String.format("JOIN_{%s}", Joiner.on(",").join(onVariables));
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
		if (!(obj instanceof PJoin))
			return false;
		return true;
	}
	

}
