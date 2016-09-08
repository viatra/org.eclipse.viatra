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

import java.util.Iterator;
import java.util.List;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.viatra.query.runtime.base.api.NavigationHelper;
import org.eclipse.viatra.query.runtime.emf.EMFScope;
import org.eclipse.viatra.query.runtime.emf.types.EClassTransitiveInstancesKey;
import org.eclipse.viatra.query.runtime.localsearch.MatchingFrame;
import org.eclipse.viatra.query.runtime.localsearch.matcher.ISearchContext;
import org.eclipse.viatra.query.runtime.localsearch.operations.IIteratingSearchOperation;
import org.eclipse.viatra.query.runtime.matchers.context.IInputKey;

import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;

/**
 * Iterates all available {@link EClass} instances without using an {@link NavigationHelper VIATRA Base indexer}.
 * 
 * @author Zoltan Ujhelyi
 */
public class IterateOverEClassInstances extends AbstractIteratingExtendOperation<EObject> implements IIteratingSearchOperation{

    private EClass clazz;

    public IterateOverEClassInstances(int position, EClass clazz, EMFScope scope) {
        super(position, scope);
        this.clazz = clazz;
    }

    public EClass getClazz() {
        return clazz;
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public void onInitialize(MatchingFrame frame, ISearchContext context) {
        // The resulting iterator can be safely casted to EObject iterator as its content is filtered to an EClass
        it = (Iterator<EObject>) Iterators.filter(getModelContents(), clazz.getInstanceClass()) ;
    }
    
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        String name = clazz.getName();

        builder.append("extend ")
        .append(name);

        return builder.toString();
    }
    
    @Override
	public List<Integer> getVariablePositions() {
		return Lists.asList(position, new Integer[0]);
	}

    /**
     * @since 1.4
     */
    @Override
    public IInputKey getIteratedInputKey() {
        return new EClassTransitiveInstancesKey(clazz);
    }
    
}
