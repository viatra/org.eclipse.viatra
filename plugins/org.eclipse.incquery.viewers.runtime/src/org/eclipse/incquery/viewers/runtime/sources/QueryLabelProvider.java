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
package org.eclipse.incquery.viewers.runtime.sources;

import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.incquery.viewers.runtime.ViewersRuntimePlugin;
import org.eclipse.incquery.viewers.runtime.model.Containment;
import org.eclipse.incquery.viewers.runtime.model.Edge;
import org.eclipse.incquery.viewers.runtime.model.Item;
import org.eclipse.incquery.viewers.runtime.model.ViewerState;
import org.eclipse.incquery.viewers.runtime.model.listeners.AbstractViewerLabelListener;
import org.eclipse.incquery.viewers.runtime.model.listeners.IViewerLabelListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.LabelProviderChangedEvent;
import org.eclipse.swt.graphics.Image;

/**
 * @author Zoltan Ujhelyi
 *
 */
public class QueryLabelProvider extends LabelProvider {
    
    private IViewerLabelListener labelListener = new AbstractViewerLabelListener() {

		@Override
		public void labelUpdated(Item item, String newLabel) {
			fireLabelProviderChanged(new LabelProviderChangedEvent(QueryLabelProvider.this, item));
		}

		@Override
		public void labelUpdated(Edge edge, String newLabel) {
			fireLabelProviderChanged(new LabelProviderChangedEvent(QueryLabelProvider.this, edge));			
		}
    	
	};
	
	private ViewerState state; 

    private Image itemIcon;
    private Image edgeIcon; 
    private Image contIcon;
	
	public QueryLabelProvider(ViewerState state) {
		this.state = state;
		state.addLabelListener(labelListener);
		itemIcon = ViewersRuntimePlugin.imageDescriptorFromPlugin(ViewersRuntimePlugin.PLUGIN_ID, "icons/item.gif").createImage();
		edgeIcon = ViewersRuntimePlugin.imageDescriptorFromPlugin(ViewersRuntimePlugin.PLUGIN_ID, "icons/edge.gif").createImage();
		contIcon = ViewersRuntimePlugin.imageDescriptorFromPlugin(ViewersRuntimePlugin.PLUGIN_ID, "icons/containment.gif").createImage();
	}
	
    @Override
    public String getText(Object element) {
        if (element instanceof Item) {
            IObservableValue value = ((Item) element).getLabel();
            return value.getValue().toString();
        } else if (element instanceof Edge) {
            IObservableValue value = ((Edge) element).getLabel();
            return value.getValue().toString();
    	}
    	return "";
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.LabelProvider#getImage(java.lang.Object)
     */
    @Override
    public Image getImage(Object element) {
    	if (element instanceof Item) {
            return itemIcon;
        } else if (element instanceof Containment) {
    		return contIcon;
    	} else if (element instanceof Edge) {
            return edgeIcon;
    	} 
    	return super.getImage(element);
    }


    
    @Override
    public void dispose() {
    	itemIcon.dispose();
    	edgeIcon.dispose();
    	contIcon.dispose();
    	state.removeLabelListener(labelListener);
    	super.dispose();
    }

}