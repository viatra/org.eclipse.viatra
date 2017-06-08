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
package org.eclipse.viatra.query.runtime.localsearch.operations.extend.nobase;

import java.util.Collections;
import java.util.Iterator;

import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.viatra.query.runtime.emf.EMFScope;
import org.eclipse.viatra.query.runtime.localsearch.operations.extend.ExtendOperation;

import com.google.common.base.Function;
import com.google.common.collect.Iterators;

/**
 * This abstract class provides a utility method for extenders to iterate over the given scope.
 * 
 * @author Grill Balázs
 *
 * @param <T>
 */
public abstract class AbstractIteratingExtendOperation<T> extends ExtendOperation<T> {

    private final EMFScope scope;
    
    public AbstractIteratingExtendOperation(int position, EMFScope scope) {
        super(position);
        this.scope = scope;
    }
    
    protected Iterator<Notifier> getModelContents(){
        return Iterators.concat(Iterators.transform(scope.getScopeRoots().iterator(), new Function<Notifier, Iterator<? extends Notifier>>() {

            @Override
            public Iterator<? extends Notifier> apply(Notifier input) {
                if (input instanceof ResourceSet){
                    return ((ResourceSet) input).getAllContents();
                }
                if (input instanceof Resource){
                    return ((Resource) input).getAllContents();
                }
                if (input instanceof EObject){
                    return ((EObject) input).eAllContents();
                }
                return Collections.emptyIterator();
            }
        }));
    }

}
