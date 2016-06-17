/*******************************************************************************
 * Copyright (c) 2004-2008 Gabor Bergmann and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Gabor Bergmann - initial API and implementation
 *******************************************************************************/

package org.eclipse.viatra.query.runtime.rete.network;

import java.util.Map;
import java.util.Set;

import org.eclipse.viatra.query.runtime.matchers.context.IQueryRuntimeContext;
import org.eclipse.viatra.query.runtime.matchers.tuple.TupleMask;
import org.eclipse.viatra.query.runtime.matchers.util.CollectionsFactory;
import org.eclipse.viatra.query.runtime.rete.boundary.InputConnector;
import org.eclipse.viatra.query.runtime.rete.index.Indexer;
import org.eclipse.viatra.query.runtime.rete.index.OnetimeIndexer;
import org.eclipse.viatra.query.runtime.rete.index.ProjectionIndexer;
import org.eclipse.viatra.query.runtime.rete.recipes.IndexerRecipe;
import org.eclipse.viatra.query.runtime.rete.recipes.InputFilterRecipe;
import org.eclipse.viatra.query.runtime.rete.recipes.InputRecipe;
import org.eclipse.viatra.query.runtime.rete.recipes.ProjectionIndexerRecipe;
import org.eclipse.viatra.query.runtime.rete.recipes.RecipesFactory;
import org.eclipse.viatra.query.runtime.rete.recipes.ReteNodeRecipe;
import org.eclipse.viatra.query.runtime.rete.recipes.TransparentRecipe;
import org.eclipse.viatra.query.runtime.rete.recipes.helper.RecipeRecognizer;
import org.eclipse.viatra.query.runtime.rete.recipes.helper.RecipesHelper;
import org.eclipse.viatra.query.runtime.rete.remote.Address;
import org.eclipse.viatra.query.runtime.rete.remote.RemoteReceiver;
import org.eclipse.viatra.query.runtime.rete.remote.RemoteSupplier;
import org.eclipse.viatra.query.runtime.rete.traceability.ActiveNodeConflictTrace;
import org.eclipse.viatra.query.runtime.rete.traceability.RecipeTraceInfo;
import org.eclipse.viatra.query.runtime.rete.traceability.UserRequestTrace;
import org.eclipse.viatra.query.runtime.rete.util.Options;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

/**
 * Stores the internal parts of a rete network. Nodes are stored according to type and parameters.
 *
 * @author Gabor Bergmann
 */
public class NodeProvisioner {

    // boolean activeStorage = true;

    ReteContainer reteContainer;
    NodeFactory nodeFactory;
    ConnectionFactory connectionFactory;
    InputConnector inputConnector;
    IQueryRuntimeContext runtimeContext;

    // TODO as recipe?
    Map<Supplier, RemoteReceiver> remoteReceivers = CollectionsFactory.getMap();// new
                                                                                // HashMap<Supplier,
                                                                                // RemoteReceiver>();
    Map<Address<? extends Supplier>, RemoteSupplier> remoteSuppliers = CollectionsFactory.getMap();// new
                                                                                                   // HashMap<Address<?
                                                                                                   // extends
                                                                                                   // Supplier>,
                                                                                                   // RemoteSupplier>();

    private RecipeRecognizer recognizer = new RecipeRecognizer();

    /**
     * PRE: NodeFactory, ConnectionFactory must exist
     * 
     * @param reteContainer
     *            the ReteNet whose interior is to be mapped.
     */
    public NodeProvisioner(ReteContainer reteContainer) {
        super();
        this.reteContainer = reteContainer;
        this.nodeFactory = reteContainer.getNodeFactory();
        this.connectionFactory = reteContainer.getConnectionFactory();
        this.inputConnector = reteContainer.getInputConnectionFactory();
        runtimeContext = reteContainer.getNetwork().getEngine().getRuntimeContext();
    }

    public synchronized Address<? extends Node> getOrCreateNodeByRecipe(RecipeTraceInfo recipeTrace) {
        ReteNodeRecipe recipe = recipeTrace.getRecipe();
        Address<? extends Node> result = getNodesByRecipe().get(recipe);
        if (result != null) {
            // NODE ALREADY CONSTRUCTED FOR RECIPE, only needs to add trace
            if (getRecipeTraces().add(recipeTrace))
                result.getNodeCache().assignTraceInfo(recipeTrace);
        } else {
            // No node for this recipe object - but equivalent recipes still
            // reusable
            ReteNodeRecipe canonicalRecipe = recognizer.canonicalizeRecipe(recipe);
            if (canonicalRecipe != recipe) {
                // FOUND EQUIVALENT RECIPE
                result = getNodesByRecipe().get(canonicalRecipe);
                if (result != null) {
                    // NODE ALREADY CONSTRUCTED FOR EQUIVALENT RECIPE
                    recipeTrace.shadowWithEquivalentRecipe(canonicalRecipe);
                    getNodesByRecipe().put(recipe, result);
                    if (getRecipeTraces().add(recipeTrace))
                        result.getNodeCache().assignTraceInfo(recipeTrace);
                    // Bug 491922: ensure that recipe shadowing propagates to
                    // parent traces
                    // note that if equivalentRecipes() becomes more
                    // sophisticated
                    // and considers recipes with different parents, this might
                    // have to be changed
                    ensureParents(recipeTrace);
                } else {
                    // CONSTRUCTION IN PROGRESS FOR EQUIVALENT RECIPE
                    if (recipe instanceof IndexerRecipe) {
                        // this is allowed for indexers;
                        // go on with the construction, as the same indexer node
                        // will be obtained anyways
                    } else {
                        throw new IllegalStateException(
                                "This should not happen: " + "non-indexer nodes are are supposed to be constructed "
                                        + "as soon as they are designated as canonical recipes");
                    }
                }
            }
            if (result == null) {
                // MUST INSTANTIATE NEW NODE FOR RECIPE
                final Node freshNode = instantiateNodeForRecipe(recipeTrace, recipe);
                result = reteContainer.makeAddress(freshNode);
            }
        }
        return result;
    }

    private Set<RecipeTraceInfo> getRecipeTraces() {
        return reteContainer.network.recipeTraces;
    }

    private Node instantiateNodeForRecipe(RecipeTraceInfo recipeTrace, final ReteNodeRecipe recipe) {
        this.getRecipeTraces().add(recipeTrace);
        if (recipe instanceof IndexerRecipe) {

            // INSTANTIATE AND HOOK UP
            // (cannot delay hooking up, because parent determines indexer
            // implementation)
            ensureParents(recipeTrace);
            final ReteNodeRecipe parentRecipe = recipeTrace.getParentRecipeTraces().iterator().next().getRecipe();
            final Indexer result = nodeFactory.createIndexer(reteContainer, (IndexerRecipe) recipe,
                    asSupplier(
                            (Address<? extends Supplier>) reteContainer.network.getExistingNodeByRecipe(parentRecipe)),
                    recipeTrace);

            // REMEMBER
            if (Options.nodeSharingOption != Options.NodeSharingOption.NEVER) {
                getNodesByRecipe().put(recipe, reteContainer.makeAddress(result));
            }

            return result;
        } else {

            // INSTANTIATE
            Node result = nodeFactory.createNode(reteContainer, recipe, recipeTrace);

            // REMEMBER
            if (Options.nodeSharingOption == Options.NodeSharingOption.ALL) {
                getNodesByRecipe().put(recipe, reteContainer.makeAddress(result));
            }

            // HOOK UP
            // (recursion-tolerant due to this delayed order of initialization)
            if (recipe instanceof InputRecipe) {
                inputConnector.connectInput((InputRecipe) recipe, result);
            } else {
                if (recipe instanceof InputFilterRecipe)
                    inputConnector.connectInputFilter((InputFilterRecipe) recipe, result);
                ensureParents(recipeTrace);
                connectionFactory.connectToParents(recipeTrace, result);
            }
            return result;
        }
    }

    private Map<ReteNodeRecipe, Address<? extends Node>> getNodesByRecipe() {
        return reteContainer.network.nodesByRecipe;
    }

    private void ensureParents(RecipeTraceInfo recipeTrace) {
        for (RecipeTraceInfo parentTrace : recipeTrace.getParentRecipeTraces()) {
            getOrCreateNodeByRecipe(parentTrace);
        }
    }

    //// Remoting - TODO eliminate?

    synchronized RemoteReceiver accessRemoteReceiver(Address<? extends Supplier> address) {
        throw new UnsupportedOperationException("Multi-container Rete not supported yet");
        // if (!reteContainer.isLocal(address))
        // return
        // address.getContainer().getProvisioner().accessRemoteReceiver(address);
        // Supplier localSupplier = reteContainer.resolveLocal(address);
        // RemoteReceiver result = remoteReceivers.get(localSupplier);
        // if (result == null) {
        // result = new RemoteReceiver(reteContainer);
        // reteContainer.connect(localSupplier, result); // stateless node, no
        // // synch required
        //
        // if (Options.nodeSharingOption != Options.NodeSharingOption.NEVER)
        // remoteReceivers.put(localSupplier, result);
        // }
        // return result;
    }

    /**
     * @pre: address is NOT local
     */
    synchronized RemoteSupplier accessRemoteSupplier(Address<? extends Supplier> address) {
        throw new UnsupportedOperationException("Multi-container Rete not supported yet");
        // RemoteSupplier result = remoteSuppliers.get(address);
        // if (result == null) {
        // result = new RemoteSupplier(reteContainer,
        // address.getContainer().getProvisioner()
        // .accessRemoteReceiver(address));
        // // network.connectAndSynchronize(supplier, result);
        //
        // if (Options.nodeSharingOption != Options.NodeSharingOption.NEVER)
        // remoteSuppliers.put(address, result);
        // }
        // return result;
    }

    /**
     * The powerful method for accessing any (supplier) Address as a local supplier.
     */
    public Supplier asSupplier(Address<? extends Supplier> address) {
        if (!reteContainer.isLocal(address))
            return accessRemoteSupplier(address);
        else
            return reteContainer.resolveLocal(address);
    }

    private Table<RecipeTraceInfo, TupleMask, UserRequestTrace> projectionIndexerUserRequests = HashBasedTable.create();

    // local version
    // TODO remove?
    public synchronized ProjectionIndexer accessProjectionIndexer(RecipeTraceInfo productionTrace, TupleMask mask) {
        UserRequestTrace indexerTrace = projectionIndexerUserRequests.get(productionTrace, mask);
        if (indexerTrace == null) {
            final org.eclipse.viatra.query.runtime.rete.recipes.ProjectionIndexerRecipe projectionIndexerRecipe = projectionIndexerRecipe(
                    productionTrace, mask);
            indexerTrace = new UserRequestTrace(projectionIndexerRecipe, productionTrace);
            projectionIndexerUserRequests.put(productionTrace, mask, indexerTrace);
        }
        final Address<? extends Node> address = getOrCreateNodeByRecipe(indexerTrace);
        return (ProjectionIndexer) reteContainer.resolveLocal(address);
    }

    // local version
    public synchronized ProjectionIndexer accessProjectionIndexerOnetime(RecipeTraceInfo supplierTrace,
            TupleMask mask) {
        if (Options.nodeSharingOption != Options.NodeSharingOption.NEVER)
            return accessProjectionIndexer(supplierTrace, mask);

        final Address<? extends Node> supplierAddress = getOrCreateNodeByRecipe(supplierTrace);
        Supplier supplier = (Supplier) reteContainer.resolveLocal(supplierAddress);

        reteContainer.flushUpdates();
        OnetimeIndexer result = new OnetimeIndexer(reteContainer, mask);
        reteContainer.sendConstructionUpdates(result, Direction.INSERT, reteContainer.pullContents(supplier));
        reteContainer.flushUpdates();

        return result;
    }

    // local, read-only version
    public synchronized ProjectionIndexer peekProjectionIndexer(RecipeTraceInfo supplierTrace, TupleMask mask) {
        final Address<? extends Node> address = getNodesByRecipe().get(projectionIndexerRecipe(supplierTrace, mask));
        return address == null ? null : (ProjectionIndexer) reteContainer.resolveLocal(address);
    }

    private org.eclipse.viatra.query.runtime.rete.recipes.ProjectionIndexerRecipe projectionIndexerRecipe(
            RecipeTraceInfo parentTrace, TupleMask mask) {
        final ReteNodeRecipe parentRecipe = parentTrace.getRecipe();
        ProjectionIndexerRecipe projectionIndexerRecipe = resultSeedRecipes.get(parentRecipe, mask);
        if (projectionIndexerRecipe == null) {
            projectionIndexerRecipe = RecipesHelper.projectionIndexerRecipe(parentRecipe,
                    RecipesHelper.mask(mask.sourceWidth, mask.indices));
            resultSeedRecipes.put(parentRecipe, mask, projectionIndexerRecipe);
        }
        return projectionIndexerRecipe;
    }

    private Table<ReteNodeRecipe, TupleMask, org.eclipse.viatra.query.runtime.rete.recipes.ProjectionIndexerRecipe> resultSeedRecipes = HashBasedTable
            .create();

    // public synchronized Address<? extends Supplier>
    // accessValueBinderFilterNode(
    // Address<? extends Supplier> supplierAddress, int bindingIndex, Object
    // bindingValue) {
    // Supplier supplier = asSupplier(supplierAddress);
    // Object[] paramsArray = { supplier.getNodeId(), bindingIndex, bindingValue
    // };
    // Tuple params = new FlatTuple(paramsArray);
    // ValueBinderFilterNode result = valueBinderFilters.get(params);
    // if (result == null) {
    // result = new ValueBinderFilterNode(reteContainer, bindingIndex,
    // bindingValue);
    // reteContainer.connect(supplier, result); // stateless node, no synch
    // // required
    //
    // if (Options.nodeSharingOption == Options.NodeSharingOption.ALL)
    // valueBinderFilters.put(params, result);
    // }
    // return reteContainer.makeAddress(result);
    // }

    /**
     * Returns a copy of the given indexer that is an active node by itself (created if does not exist). (Convention:
     * attached with same mask to a transparent node that is attached to parent node.) Node is created if it does not
     * exist yet.
     *
     * @return an identical but active indexer
     */
    // TODO rethink traceability
    RecipeTraceInfo accessActiveIndexer(RecipeTraceInfo inactiveIndexerRecipeTrace) {
        final RecipeTraceInfo parentRecipeTrace = inactiveIndexerRecipeTrace.getParentRecipeTraces().iterator().next();
        final org.eclipse.viatra.query.runtime.rete.recipes.ProjectionIndexerRecipe inactiveIndexerRecipe = (org.eclipse.viatra.query.runtime.rete.recipes.ProjectionIndexerRecipe) inactiveIndexerRecipeTrace
                .getRecipe();

        final TransparentRecipe transparentRecipe = RecipesFactory.eINSTANCE.createTransparentRecipe();
        transparentRecipe.setParent(parentRecipeTrace.getRecipe());
        final ActiveNodeConflictTrace transparentRecipeTrace = new ActiveNodeConflictTrace(transparentRecipe,
                parentRecipeTrace, inactiveIndexerRecipeTrace);

        final org.eclipse.viatra.query.runtime.rete.recipes.ProjectionIndexerRecipe activeIndexerRecipe = RecipesFactory.eINSTANCE
                .createProjectionIndexerRecipe();
        activeIndexerRecipe.setParent(transparentRecipe);
        activeIndexerRecipe.setMask(inactiveIndexerRecipe.getMask());
        final ActiveNodeConflictTrace activeIndexerRecipeTrace = new ActiveNodeConflictTrace(activeIndexerRecipe,
                transparentRecipeTrace, inactiveIndexerRecipeTrace);

        return activeIndexerRecipeTrace;
    }

    // /**
    // * @param parent
    // * @return
    // */
    // private TransparentNode accessTransparentNodeInternal(Supplier parent) {
    // nodeFactory.
    // return null;
    // }

    // public synchronized void registerSpecializedProjectionIndexer(Node node,
    // ProjectionIndexer indexer) {
    // if (Options.nodeSharingOption != Options.NodeSharingOption.NEVER) {
    // Object[] paramsArray = { node.getNodeId(), indexer.getMask() };
    // Tuple params = new FlatTuple(paramsArray);
    // projectionIndexers.put(params, indexer);
    // }
    // }

}
