/*******************************************************************************
 * Copyright (c) 2004-2010 Gabor Bergmann and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Gabor Bergmann - initial API and implementation
 *******************************************************************************/

package org.eclipse.incquery.runtime.matchers.psystem;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.incquery.runtime.matchers.psystem.basicdeferred.ExportedParameter;
import org.eclipse.incquery.runtime.matchers.psystem.basicenumerables.ConstantValue;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

/**
 * A set of constraints representing a pattern body
 * @author Gabor Bergmann
 *
 */
public class PBody {
    private PQuery query;

    private Set<PVariable> allVariables;
    private Set<PVariable> uniqueVariables;
    private List<ExportedParameter> symbolicParameters;
    private Map<Object, PVariable> variablesByName;
    private Set<PConstraint> constraints;
    private int nextVirtualNodeID;

    public PBody(PQuery query) {
        super();
        this.query = query;
        allVariables = new LinkedHashSet<PVariable>();
        uniqueVariables = new LinkedHashSet<PVariable>();
        variablesByName = new HashMap<Object, PVariable>();
        constraints = new LinkedHashSet<PConstraint>();
    }

    /**
     * @return whether the submission of the new variable was successful
     */
    private boolean addVariable(PVariable var) {
        checkMutability();
        Object name = var.getName();
        if (!variablesByName.containsKey(name)) {
            allVariables.add(var);
            if (var.isUnique())
                uniqueVariables.add(var);
            variablesByName.put(name, var);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Use this method to add a newly created constraint to the pSystem.
     *
     * @return whether the submission of the new constraint was successful
     */
    boolean registerConstraint(PConstraint constraint) {
        checkMutability();
        return constraints.add(constraint);
    }

    /**
     * Use this method to remove an obsolete constraint from the pSystem.
     *
     * @return whether the removal of the constraint was successful
     */
    boolean unregisterConstraint(PConstraint constraint) {
        checkMutability();
        return constraints.remove(constraint);
    }

    @SuppressWarnings("unchecked")
    public <ConstraintType> Set<ConstraintType> getConstraintsOfType(Class<ConstraintType> constraintClass) {
        Set<ConstraintType> result = new HashSet<ConstraintType>();
        for (PConstraint pConstraint : constraints) {
            if (constraintClass.isInstance(pConstraint))
                result.add((ConstraintType) pConstraint);
        }
        return result;
    }

    public PVariable newVirtualVariable() {
        checkMutability();
        String name;
        do {
            name = String.format(".virtual{%d}", nextVirtualNodeID++);
        } while (variablesByName.containsKey(name));
        PVariable var = new PVariable(this, name, true);
        addVariable(var);
        return var;
    }

    public PVariable newConstantVariable(Object value) {
        checkMutability();
        PVariable virtual = newVirtualVariable();
        new ConstantValue(this, virtual, value);
        return virtual;
    }

    public Set<PVariable> getAllVariables() {
        return allVariables;
    }

    public Set<PVariable> getUniqueVariables() {
        return uniqueVariables;
    }

    private PVariable getVariableByName(Object name) {
        return variablesByName.get(name).getUnifiedIntoRoot();
    }

    /**
     * Find a PVariable by name
     * @param name
     * @return the found variable
     * @throws IllegalArgumentException if no PVariable is found with the selected name
     */
    public PVariable getVariableByNameChecked(Object name) throws IllegalArgumentException {
        if (!variablesByName.containsKey(name))
            throw new IllegalArgumentException(String.format("Cannot find PVariable %s", name));
        return getVariableByName(name);
    }

    public PVariable getOrCreateVariableByName(String name) {
        checkMutability();
        if (!variablesByName.containsKey(name))
            addVariable(new PVariable(this, name));
        return getVariableByName(name);
    }

    public Set<PConstraint> getConstraints() {
        return constraints;
    }

    public PQuery getPattern() {
        return query;
    }

    void noLongerUnique(PVariable pVariable) {
        assert (!pVariable.isUnique());
        uniqueVariables.remove(pVariable);
    }

    /**
     * Returns the symbolic parameters of the body. </p>
     * 
     * <p>
     * <strong>Warning</strong>: if two PVariables are unified, the returned list changes. If you want to have a stable
     * version, consider using {@link #getSymbolicParameters()}.
     * 
     * @return a non-null, but possibly empty list
     */
    public List<PVariable> getSymbolicParametersVariables() {
        return Lists.transform(symbolicParameters, new Function<ExportedParameter, PVariable>() {

            @Override
            public PVariable apply(ExportedParameter constraint) {
                return constraint.getParameterVariable();
            }
        });
    }
    
    /**
     * Returns the exported parameter constraints of the body
     * 
     * @return a non-null, but possibly empty list
     */
    public List<ExportedParameter> getSymbolicParameters() {
        return symbolicParameters == null ? Lists.<ExportedParameter>newArrayList() : symbolicParameters;
    }

    public void setExportedParameters(List<ExportedParameter> symbolicParameters) {
        checkMutability();
        this.symbolicParameters = Lists.newArrayList(symbolicParameters);
    }

    void checkMutability() throws IllegalStateException {
        query.checkMutability();
    }
}
