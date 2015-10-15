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
package org.eclipse.incquery.runtime.localsearch.operations.check.nobase;

import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.incquery.runtime.base.api.filters.IBaseIndexObjectFilter;
import org.eclipse.incquery.runtime.emf.EMFScope;
import org.eclipse.incquery.runtime.localsearch.MatchingFrame;
import org.eclipse.incquery.runtime.localsearch.operations.check.CheckOperation;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

/**
 * This operation simply checks if a model element is part of the Query Scope
 * 
 * @author Marton Bur
 *
 */
public class ScopeCheck extends CheckOperation {

    private Integer position;
    private EMFScope scope;

    public ScopeCheck(int position, EMFScope scope) {
        this.position = position;
        this.scope = scope;

    }

    @Override
    protected boolean check(MatchingFrame frame) {
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
                EObject container = eObject;
                while (container.eContainer() != null) {
                    Resource eResource = container.eResource();
                    if (!scope.getScopeRoots().contains(eResource)) {
                        return false;
                    }
                    container = container.eContainer();
                }
                return true;
            }
        } else {
            return true;            
        }
    }

    @Override
    public String toString() {
        return "ScopeCheck";
    }
    @Override
    public List<Integer> getVariablePositions() {
        return Lists.asList(position, new Integer[0]);
    }
}
