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
import org.eclipse.incquery.runtime.matchers.psystem.PVariable;

import com.google.common.base.Joiner;

/**
 * Represents a projection to a limited set of variables.
 * <p> May optionally indicate that projections of tuples are inferred to be unique, no actual check needed.
 * 
 * @author Bergmann Gabor
 *
 */
public class PProject extends POperation {

	private Set<PVariable> toVariables;
	// TODO leave here? is this a problem in equivalnece checking?
	private boolean uniquenessCheckNeededAfterwards = true;
		
	
	public PProject(Set<PVariable> toVariables) {
		super();
		this.toVariables = toVariables;
	}
	
	public boolean isUniquenessCheckNeededAfterwards() {
		return uniquenessCheckNeededAfterwards;
	}
	public void setUniquenessCheckNeededAfterwards(
			boolean uniquenessCheckNeededAfterwards) {
		this.uniquenessCheckNeededAfterwards = uniquenessCheckNeededAfterwards;
	}
	public Set<PVariable> getToVariables() {
		return toVariables;
	}

	@Override
	public Set<? extends PConstraint> getDeltaConstraints() {
		return Collections.emptySet();
	}


	@Override
	public String getShortDebugName() {
		return String.format("PROJECT%s_{%s}", uniquenessCheckNeededAfterwards? "" : "*", Joiner.on(",").join(toVariables));
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((toVariables == null) ? 0 : toVariables.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof PProject))
			return false;
		PProject other = (PProject) obj;
		if (toVariables == null) {
			if (other.toVariables != null)
				return false;
		} else if (!toVariables.equals(other.toVariables))
			return false;
		return true;
	}
	
	

}
