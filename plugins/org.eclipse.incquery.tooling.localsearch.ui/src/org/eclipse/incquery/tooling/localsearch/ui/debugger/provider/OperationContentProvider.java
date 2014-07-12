/*******************************************************************************
 * Copyright (c) 2010-2014, Marton Bur, Akos Horvath, Zoltan Ujhelyi, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Marton Bur - initial API and implementation
 *******************************************************************************/
package org.eclipse.incquery.tooling.localsearch.ui.debugger.provider;

import java.util.List;

import org.eclipse.incquery.runtime.localsearch.operations.ISearchOperation;
import org.eclipse.incquery.runtime.localsearch.operations.check.NACOperation;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

/**
 * 
 * @author Marton Bur
 *
 */
public class OperationContentProvider implements ITreeContentProvider {
    
    private List<ISearchOperation> operations;
    
    @Override
    public void dispose() {
    }

    @SuppressWarnings("unchecked")
    @Override
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        if(newInput != null){
            operations = (List<ISearchOperation>) newInput;
        }
    }

    @Override
    public Object[] getElements(Object inputElement) {
        return operations.toArray();
    }

    @Override
    public Object[] getChildren(Object parentElement) {
        if(parentElement instanceof NACOperation){
            NACOperation nac = (NACOperation) parentElement;
            // FIXME get(0)
            return nac.getCalledMatcher().getPlan().get(0).getSearchPlan().getOperations().toArray();
        }
        return null;
    }

    @Override
    public Object getParent(Object element) {
        return null;
    }

    @Override
    public boolean hasChildren(Object element) {
        if(element instanceof NACOperation){
            return true;
        }
        return false;
    }

}
