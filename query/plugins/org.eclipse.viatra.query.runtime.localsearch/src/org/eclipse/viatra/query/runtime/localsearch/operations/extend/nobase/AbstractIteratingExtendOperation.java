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
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.viatra.query.runtime.emf.EMFScope;
import org.eclipse.viatra.query.runtime.localsearch.operations.extend.ExtendOperation;

/**
 * This abstract class provides a utility method for extenders to iterate over the given scope.
 * 
 * @author Grill Balázs
 *
 */
public abstract class AbstractIteratingExtendOperation extends ExtendOperation {

    private final EMFScope scope;
    
    public AbstractIteratingExtendOperation(int position, EMFScope scope) {
        super(position);
        this.scope = scope;
    }
    
    protected Stream<Notifier> getModelContents() {
        return scope.getScopeRoots().stream().map(input -> {
            if (input instanceof ResourceSet) {
                return ((ResourceSet) input).getAllContents();
            } else if (input instanceof Resource) {
                return ((Resource) input).getAllContents();
            } else if (input instanceof EObject) {
                return ((EObject) input).eAllContents();
            }
            return Collections.<Notifier> emptyIterator();
        }).map(i -> StreamSupport.stream(Spliterators.spliteratorUnknownSize(i, Spliterator.ORDERED), false))
                .flatMap(i -> i);
    }

}
