/*******************************************************************************
 * Copyright (c) 2010-2013, Zoltan Ujhelyi, Akos Horvath, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi, Akos Horvath - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.runtime.localsearch.operations.extend;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.viatra.query.runtime.localsearch.MatchingFrame;
import org.eclipse.viatra.query.runtime.localsearch.matcher.ISearchContext;
import org.eclipse.viatra.query.runtime.matchers.util.Preconditions;

/**
 * Iterates all child elements of a selected EObjects. 
 * 
 * @author Zoltan Ujhelyi
 * 
 */
public class IterateOverChildren extends SingleValueExtendOperation<EObject> {


    private int sourcePosition;
    private boolean transitive;

    /**
     * 
     * @param position the position of the variable storing the child elements
     * @param sourcePosition the position of the variable storing the parent root; must be bound
     * @param transitive if true, child elements are iterated over transitively
     */
    public IterateOverChildren(int position, int sourcePosition, boolean transitive) {
        super(position);
        this.sourcePosition = sourcePosition;
        this.transitive = transitive;
    }

    @Override
    public Iterator<EObject> getIterator(MatchingFrame frame, ISearchContext context) {
        Preconditions.checkState(frame.get(sourcePosition) instanceof EObject, "Only children of EObject elements are supported.");
        EObject source = (EObject) frame.get(sourcePosition);
        if(transitive) {
            return source.eAllContents();
        } else {
            return source.eContents().iterator();
        }
    }
    
    @Override
    public String toString() {
        return "extend    containment +"+sourcePosition+" <>--> -"+position+(transitive ? " transitively" : " directly");
    }
    
    @Override
    public List<Integer> getVariablePositions() {
        return Arrays.asList(position, sourcePosition);
    }

}
