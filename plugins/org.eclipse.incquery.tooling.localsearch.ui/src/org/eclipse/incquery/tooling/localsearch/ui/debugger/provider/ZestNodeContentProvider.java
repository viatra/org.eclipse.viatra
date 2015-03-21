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

import java.util.ArrayList;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.gef4.zest.core.viewers.IGraphEntityRelationshipContentProvider;
import org.eclipse.incquery.runtime.localsearch.MatchingFrame;
import org.eclipse.jface.viewers.ArrayContentProvider;

import com.google.common.collect.Lists;

/**
 * An initial implementation for the content provider to show the selected matching frame in a Zest viewer
 * 
 * @author Marton Bur
 *
 */
public class ZestNodeContentProvider extends ArrayContentProvider implements IGraphEntityRelationshipContentProvider {

    @Override
    public Object[] getElements(Object inputElement) {
        MatchingFrame frame = (MatchingFrame) inputElement;

        ArrayList<Object> elements = Lists.newArrayList();
        for (int i = 0; i < frame.getSize(); i++) {
            Object element = frame.get(i);
            if(element != null && element instanceof EObject){
            	elements.add(element);            	
            }
        }
        return elements.toArray();
    }
    

    @Override
    public Object[] getRelationships(Object source, Object dest) {
        if (source instanceof EObject && dest instanceof EObject) {
            EObject eSource = (EObject) source;
            EObject eDest = (EObject) dest;

            // TODO add precondition here to know which constraint was the cause for this edge
            if (eSource.eCrossReferences().contains(eDest) || eSource.eContents().contains(eDest)) {
                return new Object[] { eSource, eDest };
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

}