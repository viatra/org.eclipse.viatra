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
package org.eclipse.incquery.runtime.localsearch.operations.extend.nobase;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.incquery.runtime.base.api.NavigationHelper;
import org.eclipse.incquery.runtime.localsearch.MatchingFrame;
import org.eclipse.incquery.runtime.localsearch.matcher.ISearchContext;
import org.eclipse.incquery.runtime.localsearch.matcher.integration.LocalSearchBackend;
import org.eclipse.incquery.runtime.localsearch.operations.extend.ExtendOperation;
import org.eclipse.incquery.runtime.matchers.backend.IQueryBackend;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.collect.Table;

/**
 * Iterates over all {@link EDataType} instances without using an {@link NavigationHelper EMF-IncQuery Base indexer}.
 * 
 */
public class IterateOverEDatatypeInstances extends ExtendOperation<Object> {

    private EDataType dataType;
    private Collection<Object> contents;

    public IterateOverEDatatypeInstances(int position, EDataType dataType, Collection<EObject> allModelContents, IQueryBackend backend) {
        super(position);
        this.dataType = dataType;

        for (EObject eObject : allModelContents) {
            EDataType type = IterateOverEDatatypeInstances.this.dataType;
            LocalSearchBackend lsBackend = (LocalSearchBackend) backend;
            Table<EDataType, EClass, Set<EAttribute>> cache = lsBackend.geteAttributesByTypeForEClass();
            if(!cache.contains(type, eObject.eClass())){
                EList<EAttribute> eAllAttributes = eObject.eClass().getEAllAttributes();
                for (EAttribute eAttribute : eAllAttributes) {
                    if (eAttribute.getEType().equals(type)) {
                        cache.put(type, eObject.eClass(), Sets.<EAttribute>newHashSet());
                    }
                }
            }
            Set<EAttribute> eAttributes = cache.get(type, eObject.eClass());
            for (EAttribute eAttribute : eAttributes) {                
                if (eAttribute.isMany()) {
                    contents.addAll((Collection<?>) eObject.eGet(eAttribute));
                } else {
                    contents.add(eObject.eGet(eAttribute));
                }
            }
        }
    }

    public EDataType getDataType() {
        return dataType;
    }

    @Override
    public void onInitialize(MatchingFrame frame, ISearchContext context) {
        it = contents.iterator();
    }
    
    
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        String name = dataType.getName();

        builder.append("extend ")
            .append(name);

        return builder.toString();
    }
    
    @Override
	public List<Integer> getVariablePositions() {
		return Lists.asList(position, new Integer[0]);
	}
    

}
