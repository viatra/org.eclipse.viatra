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
package org.eclipse.viatra.addon.viewers.runtime.zest.sources;

import java.util.Collection;
import java.util.Set;

import org.eclipse.gef4.zest.core.viewers.IGraphEntityRelationshipContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.viatra.addon.viewers.runtime.model.ViewerState;
import org.eclipse.viatra.addon.viewers.runtime.notation.Containment;
import org.eclipse.viatra.addon.viewers.runtime.notation.Edge;
import org.eclipse.viatra.addon.viewers.runtime.notation.Item;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Sets;
import com.google.common.collect.Table;

/**
 * Content provider for Zest graphs. The implementation is less performant than
 * {@link ZestContentProvider}, but supports displaying isolated nodes.
 * @author Zoltan Ujhelyi
 *
 */
public class ZestContentWithIsolatedNodesProvider extends
		AbstractZestContentProvider implements
		IGraphEntityRelationshipContentProvider{

    Table<Item, Item, Set<Edge>> edgeTable;
    
    public ZestContentWithIsolatedNodesProvider() {
    	super(false);
    }
    
    public ZestContentWithIsolatedNodesProvider(boolean displayContainment) {
		super(displayContainment);
    }
    
	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		super.inputChanged(viewer, oldInput, newInput);
		if (this.state != null) {
			initializeEdgeTable(this.state);
		}
	}

	void initializeEdgeTable(ViewerState state) {
		edgeTable = HashBasedTable.create();
		addEdges(state.getEdges());
		if (displayContainment) {
		    addContainments(state.getContainments());
		}
	}

	private void addContainments(Collection<Containment> edges) {
        for (Object _edge : edges) {
            Edge edge = (Edge)_edge;
            addEdge(edge);
        }
    }
	
	private void addEdges(Collection<Edge> edges) {
		for (Object _edge : edges) {
			Edge edge = (Edge)_edge;
			addEdge(edge);
		}
	}

	private void addEdge(Edge edge) {
		Item source = edge.getSource();
		Item target = edge.getTarget();
		
		Set<Edge> edgeSet = null;
		if (edgeTable.contains(source, target)) {
			edgeSet = edgeTable.get(source, target);
		} else {
			edgeSet = Sets.<Edge>newHashSet();
			edgeTable.put(source, target, edgeSet);				
		}
		
		edgeSet.add(edge);
	}
	
	private void removeEdge(Edge edge) {
		Item source = edge.getSource();
		Item target = edge.getTarget();
		
		if (edgeTable.contains(source, target)) {
			Set<Edge> edgeSet = edgeTable.get(source, target);
			if (edgeSet.size() == 1) {
				edgeTable.remove(source, target);
			} else {
				edgeSet.remove(edge);
			}
		} 
	}
	
	@Override
	public Object[] getElements(Object inputElement) {
		if (state == null) {
			return new Object[0];
		}
		Collection<Item> items = state.getItems();
		return items.toArray(new Object[items.size()]);
	}


	@Override
	public Object[] getRelationships(Object source, Object dest) {
		if (edgeTable.contains(source, dest)) {
			Set<Edge> edgeSet = edgeTable.get(source, dest);
			return edgeSet.toArray(new Edge[edgeSet.size()]);
		} else {
			return new Edge[0];
		}
	}
	
	@Override
	public void edgeAppeared(Edge edge) {
		addEdge(edge);
		viewer.addRelationship(edge, edge.getSource(), edge.getTarget());
	}

	@Override
	public void edgeDisappeared(Edge edge) {
		removeEdge(edge);
		viewer.removeRelationship(edge);
	}

}
