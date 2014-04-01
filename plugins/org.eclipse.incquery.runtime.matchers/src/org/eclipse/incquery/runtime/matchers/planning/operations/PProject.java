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

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.incquery.runtime.matchers.planning.SubPlan;
import org.eclipse.incquery.runtime.matchers.psystem.PConstraint;
import org.eclipse.incquery.runtime.matchers.psystem.PVariable;

import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;

/**
 * Represents a projection of a single parent SubPlan onto a limited set of variables.
 * <p> May optionally prescribe an ordering of variables (List, as opposed to Set).
 * 
 * @author Bergmann Gabor
 *
 */
public class PProject extends POperation {

	private Collection<PVariable> toVariables;
	private boolean ordered;
		
	
	public PProject(Set<PVariable> toVariables) {
		super();
		this.toVariables = toVariables;
		this.ordered = false;
	}
	public PProject(List<PVariable> toVariables) {
		super();
		this.toVariables = toVariables;
		this.ordered = true;
	}
	
	public Collection<PVariable> getToVariables() {
		return toVariables;
	}
	public boolean isOrdered() {		
		return ordered;
	}
	
	@Override
	public Set<? extends PConstraint> getDeltaConstraints() {
		return Collections.emptySet();
	}
	@Override
	public int numParentSubPlans() {
		return 1;
	}
	@Override
	public void checkConsistency(SubPlan subPlan) {
		super.checkConsistency(subPlan);
		final SubPlan parentPlan = subPlan.getParentPlans().get(0);
		Preconditions.checkArgument(parentPlan.getVisibleVariables().containsAll(toVariables),
				"Variables missing from project: "  + 
					Joiner.on(',').join(Sets.difference(new HashSet<PVariable>(toVariables), parentPlan.getVisibleVariables())));
	}

	@Override
	public String getShortName() {
		return String.format("PROJECT%s_{%s}", ordered? "!" : "", Joiner.on(",").join(toVariables));
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
