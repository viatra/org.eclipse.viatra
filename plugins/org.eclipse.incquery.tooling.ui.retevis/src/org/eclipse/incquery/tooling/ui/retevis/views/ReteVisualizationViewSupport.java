/*******************************************************************************
 * Copyright (c) 2010-2013, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Denes Harmath - initial API and implementation
 *******************************************************************************/
package org.eclipse.incquery.tooling.ui.retevis.views;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.Assert;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.gef4.layout.algorithms.SpringLayoutAlgorithm;
import org.eclipse.gef4.zest.core.viewers.GraphViewer;
import org.eclipse.incquery.runtime.api.AdvancedIncQueryEngine;
import org.eclipse.incquery.runtime.api.IModelConnectorTypeEnum;
import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.runtime.api.IncQueryMatcher;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.eclipse.incquery.runtime.matchers.backend.IQueryBackend;
import org.eclipse.incquery.runtime.rete.matcher.ReteBackendFactory;
import org.eclipse.incquery.runtime.rete.matcher.ReteEngine;
import org.eclipse.incquery.runtime.rete.network.Node;
import org.eclipse.incquery.runtime.rete.recipes.ReteNodeRecipe;
import org.eclipse.incquery.runtime.rete.traceability.ActiveNodeConflictTrace;
import org.eclipse.incquery.runtime.rete.traceability.PatternTraceInfo;
import org.eclipse.incquery.runtime.rete.traceability.RecipeTraceInfo;
import org.eclipse.incquery.runtime.rete.traceability.TraceInfo;
import org.eclipse.incquery.tooling.ui.queryexplorer.content.matcher.PatternMatcherContent;
import org.eclipse.incquery.viewers.runtime.extensions.ViewersComponentConfiguration;
import org.eclipse.incquery.viewers.runtime.model.IncQueryViewerDataModel;
import org.eclipse.incquery.viewers.runtime.model.ViewerState.ViewerStateFeature;
import org.eclipse.incquery.viewers.runtime.zest.extensions.IncQueryViewersZestViewSupport;
import org.eclipse.incquery.viewers.runtime.zest.sources.ZestContentWithIsolatedNodesProvider;
import org.eclipse.ui.IViewPart;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;

public class ReteVisualizationViewSupport extends IncQueryViewersZestViewSupport {

	public ReteVisualizationViewSupport(
			IViewPart _owner,
			ViewersComponentConfiguration _config,
			GraphViewer _graphViewer) {
		super(_owner, _config, IModelConnectorTypeEnum.RESOURCESET, _graphViewer);
	}

	@Override
	protected void init() {
		super.init();
		getGraphViewer().setLayoutAlgorithm(new SpringLayoutAlgorithm());
	}

	private Map<ReteNodeRecipe, Node> nodeTrace; // XXX NOOO mutable state
	
	@Override
    protected Notifier extractModelSource(List<Object> objects) {
        nodeTrace = computeNodeTrace(objects);
        return createRecipeModel(nodeTrace);
    }

    private Map<ReteNodeRecipe, Node> computeNodeTrace(List<Object> objects) {
        Map<ReteNodeRecipe, Node> nodeTrace = Maps.newHashMap();
        for (Object object : objects) {
            if (object instanceof PatternMatcherContent) {
                PatternMatcherContent patternMatcherContent = (PatternMatcherContent) object;
                try {
                    IncQueryMatcher<IPatternMatch> matcher = patternMatcherContent.getMatcher();
                    if (matcher == null) continue;
					final IQueryBackend reteEngine = ((AdvancedIncQueryEngine) matcher
                            .getEngine()).getQueryBackend(new ReteBackendFactory());
                    final Collection<Node> allNodes = ((ReteEngine) reteEngine).getReteNet().getHeadContainer()
                            .getAllNodes();
                    for (Node node : allNodes) {
                        for (TraceInfo traceInfo : node.getTraceInfos()) {
                            if (traceInfo instanceof RecipeTraceInfo) {
                                RecipeTraceInfo recipeTraceInfo = (RecipeTraceInfo) traceInfo;
                                if (patternMatcherContent.getPatternName().equals(getPatternName(recipeTraceInfo))) { 
                                	ReteNodeRecipe recipe = recipeTraceInfo.getRecipe();
                                	nodeTrace.put(recipe, node);                                    	
                                	
                                    ReteNodeRecipe shadowedRecipe = recipeTraceInfo.getShadowedRecipe();
                                    if (shadowedRecipe != null) {
                                    	nodeTrace.put(shadowedRecipe, node);
                                    } 
                                }
                            }
                        }
                    }
                } catch (IncQueryException e) {
                    throw new RuntimeException("Failed to get query backend", e);
                }
            }
        }
        return nodeTrace;
    }
	
	private String getPatternName(RecipeTraceInfo recipeTraceInfo) {
        if (recipeTraceInfo instanceof PatternTraceInfo) {
            PatternTraceInfo patternTraceInfo = (PatternTraceInfo) recipeTraceInfo;
            return patternTraceInfo.getPatternName();
        } else if (recipeTraceInfo instanceof ActiveNodeConflictTrace) {
            ActiveNodeConflictTrace activeNodeConflictTrace = (ActiveNodeConflictTrace) recipeTraceInfo;
            return getPatternName(activeNodeConflictTrace.getInactiveRecipeTrace());
        } else {
            return null;
        }
    }

    private Notifier createRecipeModel(Map<ReteNodeRecipe, Node> nodeTrace) {
        ResourceSet resourceSet = new ResourceSetImpl();
        Resource resource = resourceSet.createResource(URI.createURI("temp"));
        for (ReteNodeRecipe recipe : nodeTrace.keySet()) {
            EObject rootContainer = EcoreUtil.getRootContainer(recipe); // to avoid messing up containment hierarchy
            resource.getContents().add(rootContainer);
        }
        return resourceSet;
    }

    @Override
    protected void bindModel() {
        Assert.isNotNull(this.configuration);
        Assert.isNotNull(this.configuration.getPatterns());

        if (state != null && !state.isDisposed()) {
            state.dispose();
        }
        IncQueryEngine engine = getEngine();
        if (engine != null) {
            state = IncQueryViewerDataModel.newViewerState(engine, this.configuration.getPatterns(),
                    this.configuration.getFilter(),
                    ImmutableSet.of(ViewerStateFeature.EDGE, ViewerStateFeature.CONTAINMENT));
            GraphViewer viewer = (GraphViewer) jfaceViewer;
            viewer.setContentProvider(new ZestContentWithIsolatedNodesProvider());
            viewer.setLabelProvider(new ReteVisualizationLabelProvider(state, nodeTrace, viewer.getControl()
                    .getDisplay()));
            viewer.setInput(state);
        }
    }
	
}
