/*******************************************************************************
 * Copyright (c) 2010-2013, Abel Hegedus, Peter Lunk, Istvan Rath and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.transformation.evm.api;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Predicate;

import org.eclipse.viatra.transformation.evm.api.resolver.ConflictSet;

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
    
    public ConflictSetIterator(ConflictSet conflictset) {
        this.conflictset = conflictset;
        breakCondition = input -> false;
    }

    /**
     * @since 2.0
     */
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
            return !breakCondition.test(nextActivation.getAtom());
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