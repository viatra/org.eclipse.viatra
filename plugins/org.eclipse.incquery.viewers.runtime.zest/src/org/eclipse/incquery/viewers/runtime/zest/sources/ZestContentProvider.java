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
package org.eclipse.incquery.viewers.runtime.zest.sources;

import org.eclipse.gef4.zest.core.viewers.GraphViewer;
import org.eclipse.gef4.zest.core.viewers.IGraphContentProvider;
import org.eclipse.incquery.viewers.runtime.model.Edge;
import org.eclipse.incquery.viewers.runtime.model.ViewerState;
import org.eclipse.jface.viewers.Viewer;

import com.google.common.collect.Iterables;

/**
 * Content provider for Zest graphs. The implementation is more performant than
 * {@link ZestContentWithIsolatedNodesProvider}, but does not support displaying
 * isolated nodes.
 * 
 * @author Zoltan Ujhelyi
 * 
 */
public class ZestContentProvider extends AbstractZestContentProvider implements IGraphContentProvider {

    GraphViewer viewer;
    ViewerState state;
    boolean displayContainment;
    
    public ZestContentProvider() {
    	this(false);
    }
    
    public ZestContentProvider(boolean displayContainment) {
		this.displayContainment = displayContainment;
    }

    @Override
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        super.inputChanged(viewer, oldInput, newInput);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object[] getElements(Object inputElement) {
        if (state!=null) {
        	Iterable<Edge> it = (displayContainment) 
        			? Iterables.concat(state.getEdges(), state.getContainments())
        			: state.getEdges();        		
			return Iterables.toArray(it, Edge.class);
        }
        else return new Object[]{};
    }
    
    @Override
    public Object getSource(Object rel) {
    	return ((Edge)rel).getSource();
    }
    
    @Override
    public Object getDestination(Object rel) {
    	return ((Edge)rel).getTarget();
    }
    
    @Override
    public void edgeAppeared(Edge edge) {
        viewer.addRelationship(edge, edge.getSource(), edge.getTarget());
    }

    @Override
    public void edgeDisappeared(Edge edge) {
        viewer.removeRelationship(edge);
    }

    @Override
    public void dispose() {
    	super.dispose();
    }

}
