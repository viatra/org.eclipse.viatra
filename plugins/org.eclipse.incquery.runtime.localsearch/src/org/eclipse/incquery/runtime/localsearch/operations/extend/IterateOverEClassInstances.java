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
package org.eclipse.incquery.runtime.localsearch.operations.extend;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.incquery.runtime.base.api.NavigationHelper;
import org.eclipse.incquery.runtime.localsearch.MatchingFrame;
import org.eclipse.incquery.runtime.localsearch.matcher.ISearchContext;

/**
 * Iterates all available {@link EClass} instances using an {@link NavigationHelper EMF-IncQuery Base indexer}. It is
 * assumed that the base indexer has been registered for the selected type.
 * 
 * @author Zoltan Ujhelyi
 * 
 */
public class IterateOverEClassInstances extends ExtendOperation<EObject> {

    private EClass clazz;

    public IterateOverEClassInstances(int position, EClass clazz) {
        super(position);
        this.clazz = clazz;
    }

    @Override
    public void onInitialize(MatchingFrame frame, ISearchContext context) {
        it = context.getBaseIndex().getAllInstances(clazz).iterator();
    }
    
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        String name = clazz.getName();
        String packageNsUri = clazz.getEPackage().getNsURI();

        builder.append("IterateOverEClassInstances(")
            .append(position)
            .append( ", getClassifierLiteral(\"")
            .append(packageNsUri)
            .append("\", \"")
            .append(name)
            .append( "\"))");
        return builder.toString();
    }
    

}
