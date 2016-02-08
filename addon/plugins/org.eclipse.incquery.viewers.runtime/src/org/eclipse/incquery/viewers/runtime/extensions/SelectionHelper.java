/*******************************************************************************
 * Copyright (c) 2010-2013, istvanrath, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   istvanrath - initial API and implementation
 *******************************************************************************/
package org.eclipse.incquery.viewers.runtime.extensions;

import java.util.List;
import java.util.Set;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.incquery.viewers.runtime.model.Edge;
import org.eclipse.incquery.viewers.runtime.model.Item;
import org.eclipse.incquery.viewers.runtime.model.ViewerState;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 * Helper class for bidirectional selection synchronization support for
 * IncQuery Viewers components.
 * 
 * @author istvanrath
 *
 */
public class SelectionHelper {

	public Set<ISelectionChangedListener> selectionChangedListeners = Sets.newHashSet();
	
	public ISelectionChangedListener trickyListener = new ISelectionChangedListener() {
		
		@Override
		public void selectionChanged(SelectionChangedEvent event) {
			for (ISelectionChangedListener l : selectionChangedListeners) {
				l.selectionChanged(new SelectionChangedEvent(event.getSelectionProvider(), unwrapElements_ViewersElementsToEObjects(event.getSelection())));
			}
		}
	};
	
	private Object getSourceObject(Item i) {
	    if (i.getParamEObject() != null) {
	        return i.getParamEObject();
	    } else if (i.getParamObject() != null) {
	        return i.getParamObject();
	    } else {
	        throw new IllegalStateException("Invalid Item selected - no source model element available.");
	    }
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public ISelection unwrapElements_ViewersElementsToEObjects(ISelection sel) {
    	List proxy = Lists.newArrayList();
    	if (sel instanceof IStructuredSelection) {
	    	for (Object e : ((IStructuredSelection)sel).toArray()) {
	    		if (e instanceof Item) {
	    			proxy.add(getSourceObject((Item)e));
	    		}
	    		else if (e instanceof Edge) {
	    		    proxy.add(getSourceObject(((Edge) e).getSource()));
	    		    proxy.add(getSourceObject(((Edge) e).getTarget()));
	    		}
	    	}
    	}
    	return new StructuredSelection(proxy);
    }
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public ISelection unwrapElements_EObjectsToViewersElements(ISelection sel, ViewerState state) {
		List proxy = Lists.newArrayList();
			if (state!=null) {
				if (sel instanceof IStructuredSelection) {
		    	for (Object e : ((IStructuredSelection)sel).toArray()) {
		    		if (e instanceof EObject) {
		    			proxy.addAll(state.getItemsFor(e));
		    		}
		    	}
	    	}
		}
    	return new StructuredSelection(proxy);
    }
}
