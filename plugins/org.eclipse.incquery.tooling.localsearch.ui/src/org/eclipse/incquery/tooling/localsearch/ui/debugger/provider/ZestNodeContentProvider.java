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
import java.util.Collection;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.gef4.zest.core.viewers.IGraphEntityRelationshipContentProvider;
import org.eclipse.incquery.runtime.localsearch.MatchingFrame;
import org.eclipse.jface.viewers.ArrayContentProvider;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;

/**
 * 
 * @author Marton Bur
 *
 */
public class ZestNodeContentProvider extends ArrayContentProvider implements IGraphEntityRelationshipContentProvider {

    @Override
    public Object[] getElements(Object inputElement) {
        MatchingFrame frame = (MatchingFrame) inputElement;

        ArrayList<Object> elems = Lists.newArrayList();
        for (int i = 0; i < frame.getSize(); i++) {
            elems.add(frame.get(i));
        }

        Collection<Object> elements = Collections2.filter(elems, new Predicate<Object>() {
            @Override
            public boolean apply(Object arg0) {
                return (arg0 != null && arg0 instanceof EObject);
            }
        });

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