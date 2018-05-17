/*******************************************************************************
 * Copyright (c) 2010-2013, Denes Harmath, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Denes Harmath - initial API and implementation
 *   Abel Hegedus - refactored version
 *******************************************************************************/
package org.eclipse.viatra.query.tooling.ui.retevis.views;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.core.runtime.Assert;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.gef.layout.algorithms.SpringLayoutAlgorithm;
import org.eclipse.ui.IViewPart;
import org.eclipse.viatra.addon.viewers.runtime.extensions.ViewersComponentConfiguration;
import org.eclipse.viatra.addon.viewers.runtime.model.ViatraViewerDataModel;
import org.eclipse.viatra.addon.viewers.runtime.model.ViewerState.ViewerStateFeature;
import org.eclipse.viatra.addon.viewers.runtime.zest.extensions.ViatraViewersZestViewSupport;
import org.eclipse.viatra.addon.viewers.runtime.zest.sources.ZestContentProvider;
import org.eclipse.viatra.integration.zest.viewer.ModifiableZestContentViewer;
import org.eclipse.viatra.query.runtime.api.AdvancedViatraQueryEngine;
import org.eclipse.viatra.query.runtime.api.IModelConnectorTypeEnum;
import org.eclipse.viatra.query.runtime.api.ViatraQueryEngine;
import org.eclipse.viatra.query.runtime.api.ViatraQueryMatcher;
import org.eclipse.viatra.query.runtime.base.api.BaseIndexOptions;
import org.eclipse.viatra.query.runtime.emf.EMFScope;
import org.eclipse.viatra.query.runtime.matchers.psystem.queries.PQuery;
import org.eclipse.viatra.query.runtime.rete.matcher.ReteBackendFactory;
import org.eclipse.viatra.query.runtime.rete.matcher.ReteEngine;
import org.eclipse.viatra.query.runtime.rete.network.Node;
import org.eclipse.viatra.query.runtime.rete.recipes.BetaRecipe;
import org.eclipse.viatra.query.runtime.rete.recipes.IndexerBasedAggregatorRecipe;
import org.eclipse.viatra.query.runtime.rete.recipes.MultiParentNodeRecipe;
import org.eclipse.viatra.query.runtime.rete.recipes.ProductionRecipe;
import org.eclipse.viatra.query.runtime.rete.recipes.ReteNodeRecipe;
import org.eclipse.viatra.query.runtime.rete.recipes.SingleParentNodeRecipe;
import org.eclipse.viatra.query.runtime.rete.traceability.RecipeTraceInfo;
import org.eclipse.viatra.query.tooling.ui.ViatraQueryGUIPlugin;
import org.eclipse.viatra.query.tooling.ui.retevis.preference.ReteVisualizationPreferenceConstants;
import org.eclipse.viatra.query.tooling.ui.util.IFilteredMatcherContent;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;

/**
 * 
 * @author Denes Harmath
 *
 */
public class ReteVisualizationViewSupport extends ViatraViewersZestViewSupport {

    public ReteVisualizationViewSupport(IViewPart _owner, ViewersComponentConfiguration _config,
            ModifiableZestContentViewer _graphViewer) {
        super(_owner, _config, IModelConnectorTypeEnum.RESOURCESET, _graphViewer);
    }

    @Override
    protected void init() {
        super.init();
        getGraphViewer().setLayoutAlgorithm(new SpringLayoutAlgorithm());
    }

    
    // XXX NOOO mutable state
    private Map<ReteNodeRecipe, Node> nodeTrace = new HashMap<>();
    
    @Override
    protected EMFScope extractModelSource(List<Object> objects) {
        Map<ReteNodeRecipe, Node> recipeToReteMap = new HashMap<>();
        // compute full Rete to Recipe map
        // compute recipe node set to display
        Set<ReteNodeRecipe> recipeSet = computeRecipeSet(objects, recipeToReteMap);
        nodeTrace = recipeToReteMap;

        // create temporary recipe model
        return createRecipeModel(recipeSet);
    }

    /**
     * Iterate on the objects and return the set of recipes that should be visualized for all matchers among the
     * objects. In addition, if at least one of the objects is a matcher, the input recipeToReteMap will be filled up
     * based on the Rete engine.
     */
    private Set<ReteNodeRecipe> computeRecipeSet(List<Object> objects, Map<ReteNodeRecipe, Node> recipeToReteMap) {

        Set<ReteNodeRecipe> recipeSet = new HashSet<>();
        
        for (IFilteredMatcherContent<?> patternMatcherContent : Iterables.filter(objects, IFilteredMatcherContent.class)) {
            ViatraQueryMatcher<?> matcher = patternMatcherContent.getMatcher();
            if (matcher == null)
                continue;
            final ReteEngine reteEngine = (ReteEngine) ((AdvancedViatraQueryEngine) matcher.getEngine())
                    .getQueryBackend(ReteBackendFactory.INSTANCE);

            // compute RecipeNode to ReteNode map once
            if (recipeToReteMap.isEmpty()) {
                Map<ReteNodeRecipe, Node> computeNodeTrace = computeNodeTrace(reteEngine);
                recipeToReteMap.putAll(computeNodeTrace);
            }

            PQuery pQuery = matcher.getSpecification().getInternalQueryRepresentation();
            // get root trace info from matcher
            RecipeTraceInfo traceInfo = reteEngine.getBoundary().accessProductionTrace(pQuery);

            // collect recipe nodes for pattern
            ReteNodeRecipe recipe = traceInfo.getRecipe();
            Set<ReteNodeRecipe> parents = getRecipeNodeParents(recipe);
            parents.add(recipe);

            // multiple patterns are selected
            recipeSet.addAll(parents);
        }
        return recipeSet;
    }

    /**
     * This method prepares a full map between all Recipe objects and corresponding Rete nodes, including shadowed
     * recipes.
     */
    private Map<ReteNodeRecipe, Node> computeNodeTrace(final ReteEngine reteEngine) {
        Map<ReteNodeRecipe, Node> recipeToNodeMap = new HashMap<>();
        Set<RecipeTraceInfo> recipeTraces = reteEngine.getReteNet().getRecipeTraces();
        for (RecipeTraceInfo info : recipeTraces) {
            Node node = info.getNode();
            ReteNodeRecipe recipe = info.getRecipe();
            ReteNodeRecipe shadowedRecipe = info.getShadowedRecipe();
            recipeToNodeMap.put(recipe, node);
            if (shadowedRecipe != null) {
                recipeToNodeMap.put(shadowedRecipe, node);
            }
        }
        return recipeToNodeMap;
    }

    /**
     * Create a temporary resource and put in all root containers of the recipes collected for visualization.
     */
    private EMFScope createRecipeModel(Set<ReteNodeRecipe> recipeSet) {
        if (recipeSet.isEmpty()) {
            return null;
        }
        ResourceSet resourceSet = new ResourceSetImpl();
        Resource resource = resourceSet.createResource(URI.createURI("temp"));
        resource.getContents().addAll(recipeSet.stream().map(EcoreUtil::getRootContainer).collect(Collectors.toSet()));
        return new EMFScope(resourceSet, new BaseIndexOptions());
    }

    /**
     * Collect the set of recipes that are the parents of the input recipe. Parents of a recipe are recipes that are
     * required to prepare it.
     * 
     */
    private Set<ReteNodeRecipe> getRecipeNodeParents(ReteNodeRecipe recipe) {
        Set<ReteNodeRecipe> parents = new HashSet<>();
        collectRecipeNodeParents(recipe, parents, true);
        return parents;
    }

    /**
     * Collects the parents of the given recipe into the given set.
     * 
     * @param recipe
     *            the recipe whose parents will be returned
     * @param parents
     *            the results are added to this set
     * @param isRootRecipe
     *            whether the recipe is a root to indicate that it has to be traversed even if it is a production recipe
     */
    private void collectRecipeNodeParents(ReteNodeRecipe recipe, Set<ReteNodeRecipe> parents, boolean isRootRecipe) {
        Set<ReteNodeRecipe> nextParentsToCollect = getImmediateParentsOfRecipe(recipe, isRootRecipe);
        for (ReteNodeRecipe reteNodeRecipe : nextParentsToCollect) {
            boolean added = parents.add(reteNodeRecipe);
            // avoid infinite recursion
            if (added) {
                collectRecipeNodeParents(reteNodeRecipe, parents, false);
            }
        }
    }

    /**
     * Based on the recipe metamodel, return the immediate parents of a given recipe
     */
    private Set<ReteNodeRecipe> getImmediateParentsOfRecipe(ReteNodeRecipe recipe, boolean isRootRecipe) {
        Set<ReteNodeRecipe> nextParentsToCollect = new HashSet<>();
        if (recipe instanceof IndexerBasedAggregatorRecipe) {
            ReteNodeRecipe aggregatorParent = ((IndexerBasedAggregatorRecipe) recipe).getParent();
            nextParentsToCollect.add(aggregatorParent);
        } else if (recipe instanceof BetaRecipe) {
            ReteNodeRecipe leftParent = ((BetaRecipe) recipe).getLeftParent();
            nextParentsToCollect.add(leftParent);
            ReteNodeRecipe rightParent = ((BetaRecipe) recipe).getRightParent();
            nextParentsToCollect.add(rightParent);
        } else if (recipe instanceof MultiParentNodeRecipe) {
            boolean isProductionRecipe = recipe instanceof ProductionRecipe;
            boolean traverseSubpatternCallMode = ViatraQueryGUIPlugin.getDefault().getPreferenceStore()
                    .getBoolean(ReteVisualizationPreferenceConstants.DISPLAY_CALLED_NETWORKS_MODE);
            boolean traverseProductionRecipe = isRootRecipe || traverseSubpatternCallMode;
            if (!isProductionRecipe || traverseProductionRecipe) {
                EList<ReteNodeRecipe> multiParentNodeParents = ((MultiParentNodeRecipe) recipe).getParents();
                nextParentsToCollect.addAll(multiParentNodeParents);
            }
        } else if (recipe instanceof SingleParentNodeRecipe) {
            ReteNodeRecipe reteNodeRecipeParent = ((SingleParentNodeRecipe) recipe).getParent();
            nextParentsToCollect.add(reteNodeRecipeParent);
        }
        return nextParentsToCollect;
    }

    @Override
    protected void bindModel() {
        Assert.isNotNull(this.configuration);
        Assert.isNotNull(this.configuration.getPatterns());

        if (state != null && !state.isDisposed()) {
            state.dispose();
        }
        ViatraQueryEngine engine = getEngine();
        if (engine != null) {
            state = ViatraViewerDataModel.newViewerState(engine, this.configuration.getPatterns(),
                    this.configuration.getFilter(),
                    ImmutableSet.of(ViewerStateFeature.EDGE, ViewerStateFeature.CONTAINMENT));
            ModifiableZestContentViewer viewer = (ModifiableZestContentViewer) jfaceViewer;
            viewer.setContentProvider(new ZestContentProvider());
            viewer.setLabelProvider(
                    new ReteVisualizationLabelProvider(state, nodeTrace, viewer.getControl().getDisplay()));
            viewer.setInput(state);
        }
    }

}
