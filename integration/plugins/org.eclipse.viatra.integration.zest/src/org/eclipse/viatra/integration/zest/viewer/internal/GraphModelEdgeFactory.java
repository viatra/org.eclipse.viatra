/*******************************************************************************
 * Copyright (c) 2024, Zoltan Ujhelyi, IncQuery Labs Ltd.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.integration.zest.viewer.internal;

import org.eclipse.viatra.integration.zest.viewer.IGraphEdgeContentProvider;
import org.eclipse.zest.core.viewers.internal.AbstractStructuredGraphViewer;
import org.eclipse.zest.core.viewers.internal.AbstractStylingModelFactory;
import org.eclipse.zest.core.widgets.Graph;

@SuppressWarnings("restriction")
public class GraphModelEdgeFactory extends AbstractStylingModelFactory {

	public GraphModelEdgeFactory(AbstractStructuredGraphViewer viewer) {
		super(viewer);
		if (!(viewer.getContentProvider() instanceof IGraphEdgeContentProvider)) {
			throw new IllegalArgumentException("Expected IGraphEdgeRelationshipContentProvider");
		}
	}

	public Graph createGraphModel(Graph model) {
		doBuildGraph(model);
		return model;
	}

	protected void doBuildGraph(Graph model) {
		super.doBuildGraph(model);
		Object[] nodes = getContentProvider().getElements(getViewer().getInput());
		nodes = filter(getViewer().getInput(), nodes);
		createModelNodes(model, nodes);
		createModelRelationships(model);
	}

	/**
	 * Creates all the model relationships. Assumes that all of the model nodes have
	 * been created in the graph model already. Runtime O(n^2) + O(r).
	 * 
	 * @param model the model to create the relationship on.
	 */
	private void createModelRelationships(Graph model) {
		IGraphEdgeContentProvider content = getCastedContent();
		for (Object relationship : content.getRelationships(getViewer().getInput())) {
		    Object source = content.getSource(relationship);
		    Object target = content.getDestination(relationship);
		    createConnection(model, relationship, source, target);
		}
	}

	/**
	 * Creates the model nodes for the given external nodes.
	 * 
	 * @param model the graph model.
	 * @param nodes the external nodes.
	 */
	private void createModelNodes(Graph model, Object[] nodes) {
		for (int i = 0; i < nodes.length; i++) {
			createNode(model, nodes[i]);
		}
	}

	public void refresh(Graph graph, Object element) {
		refresh(graph, element, false);
	}

	public void refresh(Graph graph, Object element, boolean updateLabels) {
		// with this kind of graph, it is just as easy and cost-effective to
		// rebuild the whole thing.
		refreshGraph(graph);
	}

	private IGraphEdgeContentProvider getCastedContent() {
		return (IGraphEdgeContentProvider) getContentProvider();
	}

}
