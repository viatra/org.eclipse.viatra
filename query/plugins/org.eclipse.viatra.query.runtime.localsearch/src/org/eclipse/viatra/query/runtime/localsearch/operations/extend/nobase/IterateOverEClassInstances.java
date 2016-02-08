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

import java.util.Collection;
import java.util.List;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.viatra.query.runtime.base.api.NavigationHelper;
import org.eclipse.viatra.query.runtime.localsearch.MatchingFrame;
import org.eclipse.viatra.query.runtime.localsearch.matcher.ISearchContext;
import org.eclipse.viatra.query.runtime.localsearch.operations.extend.ExtendOperation;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;

/**
 * Iterates all available {@link EClass} instances without using an {@link NavigationHelper EMF-IncQuery Base indexer}.
 * 
 * @author Zoltan Ujhelyi
 */
public class IterateOverEClassInstances extends ExtendOperation<EObject> {

    private EClass clazz;
    private Collection<EObject> contents;

    public IterateOverEClassInstances(int position, EClass clazz, Collection<EObject> allModelContents) {
        super(position);
        this.clazz = clazz;
        contents = Collections2.filter(allModelContents, new Predicate<EObject>() {
            @Override
            public boolean apply(EObject input) {
                return IterateOverEClassInstances.this.clazz.isSuperTypeOf(input.eClass());
            }
        });
    }

    public EClass getClazz() {
        return clazz;
    }
    
    @Override
    public void onInitialize(MatchingFrame frame, ISearchContext context) {
        it = contents.iterator();
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

}
