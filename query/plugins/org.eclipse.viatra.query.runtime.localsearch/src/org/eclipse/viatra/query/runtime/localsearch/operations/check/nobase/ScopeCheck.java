/*******************************************************************************
 * Copyright (c) 2010-2015, Marton Bur, Zoltan Ujhelyi, Akos Horvath, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Marton Bur - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.runtime.localsearch.operations.check.nobase;

import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.viatra.query.runtime.base.api.filters.IBaseIndexObjectFilter;
import org.eclipse.viatra.query.runtime.emf.EMFScope;
import org.eclipse.viatra.query.runtime.localsearch.MatchingFrame;
import org.eclipse.viatra.query.runtime.localsearch.matcher.ISearchContext;
import org.eclipse.viatra.query.runtime.localsearch.operations.check.CheckOperation;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

/**
 * This operation simply checks if a model element is part of the Query Scope
 * 
 * @author Marton Bur
 *
 */
public class ScopeCheck extends CheckOperation {

    private int position;
    private EMFScope scope;

    public ScopeCheck(int position, EMFScope scope) {
        this.position = position;
        this.scope = scope;

    }

    /**
     * @deprecated Use {@link #check(MatchingFrame, ISearchContext)} instead
     */
    @Deprecated
    protected boolean check(MatchingFrame frame) {
        return check(frame, null);
    }

    @Override
    protected boolean check(MatchingFrame frame, ISearchContext context) {
        Preconditions.checkNotNull(frame.getValue(position), "Invalid plan, variable %s unbound", position);
        Object value = frame.getValue(position);
        if(value instanceof EObject){
            EObject eObject = (EObject) value;
            IBaseIndexObjectFilter filterConfiguration = scope.getOptions().getObjectFilterConfiguration();
            boolean filtered = false;
            if(filterConfiguration != null){
                filtered = filterConfiguration.isFiltered(eObject);
            }
            if(filtered){
                return false;
            } else {
                return EcoreUtil.isAncestor(scope.getScopeRoots(), eObject);
            }
        } else {
            return true;            
        }
    }

    @Override
    public String toString() {
        return "check    +"+position+" in scope "+scope;
    }
    @Override
    public List<Integer> getVariablePositions() {
        return Lists.asList(position, new Integer[0]);
    }
}
