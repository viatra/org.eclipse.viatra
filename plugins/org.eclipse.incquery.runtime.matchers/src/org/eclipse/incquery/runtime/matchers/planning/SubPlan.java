/*******************************************************************************
 * Copyright (c) 2004-2008 Gabor Bergmann and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Gabor Bergmann - initial API and implementation
 *******************************************************************************/

package org.eclipse.incquery.runtime.matchers.planning;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.incquery.runtime.matchers.planning.operations.POperation;
import org.eclipse.incquery.runtime.matchers.planning.operations.PProject;
import org.eclipse.incquery.runtime.matchers.planning.operations.PStart;
import org.eclipse.incquery.runtime.matchers.psystem.PBody;
import org.eclipse.incquery.runtime.matchers.psystem.PConstraint;
import org.eclipse.incquery.runtime.matchers.psystem.PVariable;

import com.google.common.base.Joiner;

/**
 * A plan representing a subset of (or possibly all the) constraints evaluated. A SubPlan instance is responsible for
 * maintaining a state of the plan; but after it is initialized it is expected be immutable.
 * 
 * @author Gabor Bergmann
 * 
 */
public class SubPlan {
	private PBody body;
	private List<? extends SubPlan> parentPlans;
	private POperation operation;

	private final Set<PVariable> visibleVariables; 
	private final Set<PVariable> allVariables; 
	private final Set<PVariable> introducedVariables; // delta compared to first parent
    private Set<PConstraint> allConstraints;
    private Set<PConstraint> deltaConstraints; // delta compared to all parents
    

    
    
    
    
	public SubPlan(PBody body, POperation operation, SubPlan... parentPlans) {
		this(body, operation, Arrays.asList(parentPlans));
	}
	public SubPlan(PBody body, POperation operation, List<? extends SubPlan> parentPlans) {
		super();
		this.body = body;
		this.parentPlans = parentPlans;
		this.operation = operation;
		
		this.deltaConstraints = new HashSet<PConstraint>(operation.getDeltaConstraints());
		// TODO does not work for union
        this.allConstraints = new HashSet<PConstraint>(deltaConstraints);
        for (SubPlan parentPlan: parentPlans)
        	this.allConstraints.addAll(parentPlan.getAllEnforcedConstraints());
        
        this.allVariables = new HashSet<PVariable>();
        for (PConstraint constraint: allConstraints)
        	this.allVariables.addAll(constraint.getDeducedVariables());
        
        // TODO this is ugly a bit
        if (operation instanceof PStart) {
	        this.visibleVariables = new HashSet<PVariable>(((PStart) operation).getAPrioriVariables());
        	this.allVariables.addAll(visibleVariables);
        } else if (operation instanceof PProject) { 
	        this.visibleVariables = new HashSet<PVariable>(((PProject) operation).getToVariables());
        } else {
	        this.visibleVariables = new HashSet<PVariable>();
	        for (SubPlan parentPlan: parentPlans)
	        	this.visibleVariables.addAll(parentPlan.getVisibleVariables());
	        for (PConstraint constraint: deltaConstraints)
	        	this.visibleVariables.addAll(constraint.getDeducedVariables());
        } 
        
        this.introducedVariables = new HashSet<PVariable>(this.visibleVariables);
        if (!parentPlans.isEmpty()) 
        	introducedVariables.removeAll(parentPlans.get(0).getVisibleVariables());
	}
	
	
	@Override
    public String toString() {
        return toLongString();
    }
    public String toShortString() {
    	return String.format("Plan{%s}:%s", 
    			Joiner.on(',').join(visibleVariables),
    			operation.getShortName());
    }
    public String toLongString() {
    	return String.format("%s<%s>", 
    			toShortString(),
    			Joiner.on("; ").join(parentPlans));
    }


    /**
     * @return all constraints already enforced at this handle
     */
    public Set<PConstraint> getAllEnforcedConstraints() {
        return allConstraints;
    }

    /**
     * @return the new constraints enforced at this stage of plan, that aren't yet enforced at parents
     */
    public Set<PConstraint> getDeltaEnforcedConstraints() {
        return deltaConstraints;
    }

    public void inferConstraint(PConstraint constraint) {
    	deltaConstraints.add(constraint);
    	allConstraints.add(constraint);
    }
	public PBody getBody() {
		return body;
	}
	public Set<PVariable> getVisibleVariables() {
		return visibleVariables;
	}
	public Set<PVariable> getAllVariables() {
		return allVariables;
	}
	/**
	 * Delta compared to first parent
	 */
	public Set<PVariable> getIntroducedVariables() {
		return introducedVariables;
	}
	public List<? extends SubPlan> getParentPlans() {
		return parentPlans;
	}
	public POperation getOperation() {
		return operation;
	}
	
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((operation == null) ? 0 : operation.hashCode());
		result = prime * result
				+ ((parentPlans == null) ? 0 : parentPlans.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof SubPlan))
			return false;
		SubPlan other = (SubPlan) obj;
		if (operation == null) {
			if (other.operation != null)
				return false;
		} else if (!operation.equals(other.operation))
			return false;
		if (parentPlans == null) {
			if (other.parentPlans != null)
				return false;
		} else if (!parentPlans.equals(other.parentPlans))
			return false;
		return true;
	}

	
}
