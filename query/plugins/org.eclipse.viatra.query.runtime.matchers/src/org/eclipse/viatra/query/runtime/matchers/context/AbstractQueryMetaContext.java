/*******************************************************************************
 * Copyright (c) 2010-2016, Grill Balázs, IncQuery Labs Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Grill Balázs - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.runtime.matchers.context;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;

/**
 * Common abstract class for implementers of {@link IQueryMetaContext}
 * 
 * @author Grill Balázs
 * @since 1.3
 *
 */
public abstract class AbstractQueryMetaContext implements IQueryMetaContext {
    
    /**
     * @since 1.6
     */
    @Override
    public SetMultimap<InputKeyImplication, InputKeyImplication> getConditionalImplications(IInputKey implyingKey) {
        return HashMultimap.create(0, 0);
    }

    /**
     * @since 1.6
     */
    @Override
    public boolean canLeadOutOfScope(IInputKey key) {
        return key.getArity() > 1;
    }
    
    /**
     * @since 1.6
     */
    @Override
    public Comparator<IInputKey> getSuggestedEliminationOrdering() {
        return new Comparator<IInputKey>() {
            @Override
            public int compare(IInputKey o1, IInputKey o2) {
                return 0;
            }
        };
    }
    
    /**
     * @since 1.6
     */
    @Override
    public Collection<InputKeyImplication> getWeakenedAlternatives(IInputKey implyingKey) {
        return Collections.emptySet();
    }    

    @Override
    public boolean isPosetKey(IInputKey key) {
        return false;
    }

    @Override
    public IPosetComparator getPosetComparator(Iterable<IInputKey> key) {
        return null;
    }

}
