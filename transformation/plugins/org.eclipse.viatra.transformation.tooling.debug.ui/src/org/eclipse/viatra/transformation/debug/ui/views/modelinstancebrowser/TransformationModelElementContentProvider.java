/**
 * Copyright (c) 2010-2016, Peter Lunk, IncQuery Labs Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *   Peter Lunk - initial API and implementation
 */
package org.eclipse.viatra.transformation.debug.ui.views.modelinstancebrowser;

import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.viatra.transformation.debug.model.transformationstate.TransformationModelElement;
import org.eclipse.viatra.transformation.debug.model.transformationstate.TransformationModelProvider;

public class TransformationModelElementContentProvider implements ITreeContentProvider{
    private final TransformationModelProvider modelProvider;
    private boolean disposed=false;
    
    public TransformationModelElementContentProvider(TransformationModelProvider modelProvider) {
        this.modelProvider = modelProvider;
    }

    @Override
    public Object[] getElements(Object inputElement) {
        if(inputElement instanceof TransformationModelElement[]){
            return (TransformationModelElement[]) inputElement;
        }
        return new TransformationModelElement[0];
    }
    
    @Override
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        //do nothing
    }
    
    @Override
    public void dispose() {
        disposed = true;
    }

    @Override
    public Object[] getChildren(Object parentElement) {
        if(!disposed && parentElement instanceof TransformationModelElement){
            modelProvider.loadElementContent((TransformationModelElement) parentElement);
            List<TransformationModelElement> children = ((TransformationModelElement)parentElement).getChildren();
            return children.toArray(new TransformationModelElement[children.size()]);
        }
        return new Object[0];
    }

    @Override
    public Object getParent(Object element) {
        return null;
    }

    @Override
    public boolean hasChildren(Object element) {
        if(!disposed && element instanceof TransformationModelElement){
            modelProvider.loadElementContent((TransformationModelElement) element);
            return !((TransformationModelElement) element).getChildren().isEmpty();
        }
        return false;
    }

}
