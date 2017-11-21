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
import org.eclipse.jface.viewers.LabelProviderChangedEvent;
import org.eclipse.swt.widgets.Display;
import org.eclipse.viatra.addon.viewers.runtime.model.ViewerState;
import org.eclipse.viatra.addon.viewers.runtime.model.listeners.ViewerLabelListenerAdapter;
import org.eclipse.viatra.addon.viewers.runtime.model.listeners.IViewerLabelListener;
import org.eclipse.viatra.addon.viewers.runtime.notation.Edge;
import org.eclipse.viatra.addon.viewers.runtime.notation.Item;

/**
 * @author Zoltan Ujhelyi
 *
 */
public class QueryLabelProvider extends LabelProvider {
    
    private IViewerLabelListener labelListener = new ViewerLabelListenerAdapter() {

        @Override
        public void labelUpdated(final Item item, String newLabel) {
            display.asyncExec(() -> fireLabelProviderChanged(new LabelProviderChangedEvent(QueryLabelProvider.this, item)));
        }

        @Override
        public void labelUpdated(final Edge edge, String newLabel) {
            display.asyncExec(() -> fireLabelProviderChanged(new LabelProviderChangedEvent(QueryLabelProvider.this, edge)));
        }
        
    };
    
    private ViewerState state;

    protected final Display display; 

    public QueryLabelProvider(ViewerState state, Display display) {
        this.state = state;
        this.display = display;
        state.addLabelListener(labelListener);
    }
    
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
        state.removeLabelListener(labelListener);
        super.dispose();
    }

}