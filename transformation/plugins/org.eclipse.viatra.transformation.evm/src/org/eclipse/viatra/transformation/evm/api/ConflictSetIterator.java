/*******************************************************************************
 * Copyright (c) 2010-2013, Abel Hegedus, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Peter Lunk - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.transformation.evm.api;

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.eclipse.viatra.transformation.evm.api.resolver.ConflictSet;

import com.google.common.base.Predicate;

/**
 * Iterator that iterates through the elements of a {@link ConflictSet}.
 * 
 * @author Peter Lunk
 *
 */
public class ConflictSetIterator implements Iterator<Activation<?>> {
    private ConflictSet conflictset;
    @SuppressWarnings("rawtypes")
    private Predicate breakCondition;
    private Activation<?> nextActivation;
    private boolean returned = true;
    
    @SuppressWarnings("rawtypes")
    public ConflictSetIterator(ConflictSet conflictset) {
        this.conflictset = conflictset;
        breakCondition = new Predicate() {
            @Override
            public boolean apply(Object input) {
                return false;
            }
        };
    }

    @SuppressWarnings("rawtypes")
    public ConflictSetIterator(ConflictSet conflictset, Predicate breakCondition) {
        this.conflictset = conflictset;
        this.breakCondition = breakCondition;
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public boolean hasNext() {
        if (returned) {
            nextActivation = conflictset.getNextActivation();
            returned = false;
        }
        if (nextActivation != null) {
            return !breakCondition.apply(nextActivation.getAtom());
        }
        return false;
    }

    @Override
    public Activation<?> next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        returned = true;
        return nextActivation;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("Deletion from conflict set is not supported.");

    }

}