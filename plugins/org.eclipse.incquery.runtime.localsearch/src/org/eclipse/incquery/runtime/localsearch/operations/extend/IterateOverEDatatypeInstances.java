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

import org.eclipse.emf.ecore.EDataType;
import org.eclipse.incquery.runtime.base.api.NavigationHelper;
import org.eclipse.incquery.runtime.localsearch.MatchingFrame;
import org.eclipse.incquery.runtime.localsearch.matcher.ISearchContext;

/**
 * Iterates over all {@link EDataType} instances using an {@link NavigationHelper EMF-IncQuery Base indexer}. It is
 * assumed that the indexer is initialized for the selected {@link EDataType}.
 * 
 */
public class IterateOverEDatatypeInstances extends ExtendOperation<Object> {

    private EDataType dataType;

    /**
     * @param position
     * @param it
     */
    public IterateOverEDatatypeInstances(int position, EDataType dataType) {
        super(position);
        this.dataType = dataType;
    }

    /* (non-Javadoc)
     * @see org.eclipse.incquery.runtime.localsearch.operations.ISearchOperation#onInitialize(org.eclipse.incquery.runtime.localsearch.MatchingFrame)
     */
    @Override
    public void onInitialize(MatchingFrame frame, ISearchContext context) {
        it = context.getBaseIndex().getDataTypeInstances(dataType).iterator();
    }
    
    
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("IterateOverEDatatypeInstances(");
        builder.append(position + ", ");

        String name = dataType.getName();
        String packageNsUri = dataType.getEPackage().getNsURI();
        builder.append("getClassifierLiteral(\"" + packageNsUri + "\", \"" + name + "\")");

        builder.append(")");
        return builder.toString();
    }
    

}
