/*******************************************************************************
 * Copyright (c) 2010-2013, Zoltan Ujhelyi, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.addon.viewers.runtime.sources;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.widgets.Display;
import org.eclipse.viatra.addon.viewers.runtime.model.ViewerState;
import org.eclipse.viatra.addon.viewers.runtime.notation.Edge;
import org.eclipse.viatra.addon.viewers.runtime.notation.Item;

/**
 * @author Zoltan Ujhelyi
 *
 */
public class QueryLabelProvider extends LabelProvider {

    /**
     * @deprecated The parameters are ignored starting with version 2.1; use parameterless constructor instead
     */
    @Deprecated
    public QueryLabelProvider(ViewerState state, Display display) {}
    
    /**
     * Starting with VIATRA 2.1 the label provider did not require accessing the original ViewerState object and a
     * Display, making it easier to reuse label providers in different viewers.
     * 
     * @since 2.1
     */
    public QueryLabelProvider() {}
    
    @Override
    public String getText(Object element) {
        if (element instanceof Item) {
            String value = ((Item) element).getLabel();
            return value;
        } else if (element instanceof Edge) {
            String value = ((Edge) element).getLabel();
            return value;
        }
        return "";
    }
    
    @Override
    public void dispose() {
        super.dispose();
    }

}