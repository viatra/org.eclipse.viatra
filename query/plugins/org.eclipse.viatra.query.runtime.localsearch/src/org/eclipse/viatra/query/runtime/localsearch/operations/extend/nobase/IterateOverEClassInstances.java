/*******************************************************************************
 * Copyright (c) 2010-2015, Marton Bur, Zoltan Ujhelyi, Akos Horvath, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Marton Bur, Zoltan Ujhelyi, Akos Horvath - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.runtime.localsearch.operations.extend.nobase;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.viatra.query.runtime.base.api.NavigationHelper;
import org.eclipse.viatra.query.runtime.emf.EMFScope;
import org.eclipse.viatra.query.runtime.emf.types.EClassTransitiveInstancesKey;
import org.eclipse.viatra.query.runtime.localsearch.MatchingFrame;
import org.eclipse.viatra.query.runtime.localsearch.matcher.ISearchContext;
import org.eclipse.viatra.query.runtime.localsearch.operations.IIteratingSearchOperation;
import org.eclipse.viatra.query.runtime.matchers.context.IInputKey;

/**
 * Iterates all available {@link EClass} instances without using an {@link NavigationHelper VIATRA Base indexer}.
 * 
 * @author Zoltan Ujhelyi
 */
public class IterateOverEClassInstances extends AbstractIteratingExtendOperation<Notifier> implements IIteratingSearchOperation{

    private EClass clazz;

    public IterateOverEClassInstances(int position, EClass clazz, EMFScope scope) {
        super(position, scope);
        this.clazz = clazz;
    }

    public EClass getClazz() {
        return clazz;
    }
    
    @Override
    public Iterator<? extends Notifier> getIterator(MatchingFrame frame, ISearchContext context) {
        final Class<?> ic = clazz.getInstanceClass();
        return getModelContents().filter(ic::isInstance).iterator();
    }
    
    @Override
    public String toString() {
        return "extend    "+clazz.getName()+"(-"+ position+") iterating";
    }
    
    @Override
    public List<Integer> getVariablePositions() {
        return Collections.singletonList(position);
    }

    /**
     * @since 1.4
     */
    @Override
    public IInputKey getIteratedInputKey() {
        return new EClassTransitiveInstancesKey(clazz);
    }
    
}
