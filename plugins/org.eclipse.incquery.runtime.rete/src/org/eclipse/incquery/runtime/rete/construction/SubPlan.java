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

package org.eclipse.incquery.runtime.rete.construction;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.incquery.runtime.rete.collections.CollectionsFactory;
import org.eclipse.incquery.runtime.rete.construction.psystem.PConstraint;
import org.eclipse.incquery.runtime.rete.construction.psystem.PVariable;
import org.eclipse.incquery.runtime.rete.tuple.Tuple;

/**
 * A plan representing a subset of (or possibly all the) constraints evaluated. A SubPlan instance is responsible for
 * maintaining a state of the plan; but after it is initialized it is expected be immutable.
 * 
 * @author Gabor Bergmann
 * 
 */
public class SubPlan {
    private Tuple variablesTuple;
    private Map<Object, Integer> variablesIndex;
    private Set<PConstraint> constraints;
    private SubPlan primaryParentPlan;
    private SubPlan secondaryParentPlan;
    /** TODO may contain variables that have been trimmed and are no longer in the tuple */
	private final Set<PVariable> variablesSet; 

    private SubPlan(Map<Object, Integer> variablesIndex, Tuple variablesTuple) {
        super();
        this.variablesIndex = variablesIndex;
        this.variablesTuple = variablesTuple;
        this.constraints = CollectionsFactory.getSet();//new HashSet<PConstraint>();
		variablesSet = new HashSet<PVariable>();
		for (Object pVar : variablesIndex.keySet()) {
			variablesSet.add((PVariable) pVar);
		}
    }

    public SubPlan(Tuple variablesTuple) {
        this(variablesTuple.invertIndex(), variablesTuple);
    }

    public SubPlan(SubPlan primaryParent) {
        this(primaryParent.variablesIndex, primaryParent.variablesTuple);
        this.primaryParentPlan = primaryParent;
        constraints.addAll(primaryParent.getAllEnforcedConstraints());
    }

    public SubPlan(SubPlan primaryParent, Tuple variablesTuple) {
        this(variablesTuple.invertIndex(), variablesTuple);
        this.primaryParentPlan = primaryParent;
        constraints.addAll(primaryParent.getAllEnforcedConstraints());
    }

    public SubPlan(SubPlan primaryParent, SubPlan secondaryParent, Tuple variablesTuple) {
        this(variablesTuple.invertIndex(), variablesTuple);
        this.primaryParentPlan = primaryParent;
        this.secondaryParentPlan = secondaryParent;
        constraints.addAll(primaryParent.getAllEnforcedConstraints());
        constraints.addAll(secondaryParent.getAllEnforcedConstraints());
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("SubPlan(" + getVariablesTuple() + "@" + "|");
        for (PConstraint constraint : constraints)
            sb.append(constraint.toString() + "&");
        sb.append(")");
        return sb.toString();
    }

    /**
     * @return the tuple of variables that define the schema emanating from the handle
     */
    public Tuple getVariablesTuple() {
        return variablesTuple;
    }

    /**
     * @return the index of the variable within variablesTuple
     */
    public Map<Object, Integer> getVariablesIndex() {
        return variablesIndex;
    }

    /**
     * @return the set of variables involved
     */
    public Set<PVariable> getVariablesSet() {
        return variablesSet;
    }

    /**
     * @return all constraints already enforced at this handle
     */
    public Set<PConstraint> getAllEnforcedConstraints() {
        return constraints;
    }

    /**
     * @return the new constraints enforced at this handle, that aren't yet enforced at parents
     */
    public Set<PConstraint> getDeltaEnforcedConstraints() {
        Set<PConstraint> result = CollectionsFactory.getSet(constraints);//new HashSet<PConstraint>(constraints);
        if (primaryParentPlan != null)
            result.removeAll(primaryParentPlan.getAllEnforcedConstraints());
        if (secondaryParentPlan != null)
            result.removeAll(secondaryParentPlan.getAllEnforcedConstraints());
        return result;
    }

    public void addConstraint(PConstraint constraint) {
        constraints.add(constraint);
    }

    public SubPlan getPrimaryParentPlan() {
        return primaryParentPlan;
    }

    public SubPlan getSecondaryParentPlan() {
        return secondaryParentPlan;
    }

}
