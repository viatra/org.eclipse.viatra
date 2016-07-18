/*******************************************************************************
 * Copyright (c) 2010-2014, Bergmann Gabor, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Bergmann Gabor - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.runtime.rete.network;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.viatra.query.runtime.matchers.tuple.Tuple;
import org.eclipse.viatra.query.runtime.rete.aggregation.IndexerBasedAggregatorNode;
import org.eclipse.viatra.query.runtime.rete.boundary.InputConnector;
import org.eclipse.viatra.query.runtime.rete.index.DualInputNode;
import org.eclipse.viatra.query.runtime.rete.index.Indexer;
import org.eclipse.viatra.query.runtime.rete.index.IterableIndexer;
import org.eclipse.viatra.query.runtime.rete.index.ProjectionIndexer;
import org.eclipse.viatra.query.runtime.rete.recipes.BetaRecipe;
import org.eclipse.viatra.query.runtime.rete.recipes.ConstantRecipe;
import org.eclipse.viatra.query.runtime.rete.recipes.ExpressionEnforcerRecipe;
import org.eclipse.viatra.query.runtime.rete.recipes.IndexerBasedAggregatorRecipe;
import org.eclipse.viatra.query.runtime.rete.recipes.IndexerRecipe;
import org.eclipse.viatra.query.runtime.rete.recipes.InputRecipe;
import org.eclipse.viatra.query.runtime.rete.recipes.MultiParentNodeRecipe;
import org.eclipse.viatra.query.runtime.rete.recipes.ProductionRecipe;
import org.eclipse.viatra.query.runtime.rete.recipes.ProjectionIndexerRecipe;
import org.eclipse.viatra.query.runtime.rete.recipes.ReteNodeRecipe;
import org.eclipse.viatra.query.runtime.rete.recipes.SingleColumnAggregatorRecipe;
import org.eclipse.viatra.query.runtime.rete.recipes.SingleParentNodeRecipe;
import org.eclipse.viatra.query.runtime.rete.recipes.TransitiveClosureRecipe;
import org.eclipse.viatra.query.runtime.rete.recipes.UniquenessEnforcerRecipe;
import org.eclipse.viatra.query.runtime.rete.remote.Address;
import org.eclipse.viatra.query.runtime.rete.single.TransitiveClosureNode;
import org.eclipse.viatra.query.runtime.rete.traceability.RecipeTraceInfo;

/**
 * Class responsible for connecting freshly instantiating Rete nodes to their parents. 
 * @author Bergmann Gabor
 *
 */
class ConnectionFactory {
	ReteContainer reteContainer;
	
	public ConnectionFactory(ReteContainer reteContainer) {
		super();
		this.reteContainer = reteContainer;
	}
	// TODO move to node implementation instead?
	private boolean isStateful(ReteNodeRecipe recipe) {
		return 
				recipe instanceof ProjectionIndexerRecipe ||
                recipe instanceof IndexerBasedAggregatorRecipe ||
                recipe instanceof SingleColumnAggregatorRecipe ||
				recipe instanceof ExpressionEnforcerRecipe ||
				recipe instanceof TransitiveClosureRecipe ||
				recipe instanceof ProductionRecipe ||
				recipe instanceof UniquenessEnforcerRecipe;
		
	}
	
	/**
	 * PRE: nodes for parent recipes must already be created and registered <p>
	 * PRE: must not be an input node (for which {@link InputConnector} is responsible) 
	 */
	public void connectToParents(RecipeTraceInfo recipeTrace, Node freshNode) {
		final ReteNodeRecipe recipe = recipeTrace.getRecipe();
		if (recipe instanceof ConstantRecipe) {
			// NO-OP
		} else if (recipe instanceof InputRecipe) {
			throw new IllegalArgumentException(
					ConnectionFactory.class.getSimpleName() + 
					" not intended for input connection: " + recipe);
		} else if (recipe instanceof SingleParentNodeRecipe) {
			final Receiver receiver = (Receiver) freshNode;
			ReteNodeRecipe parentRecipe = ((SingleParentNodeRecipe) recipe).getParent();
			connectToParent(recipe, receiver, parentRecipe);
		} else if (recipe instanceof MultiParentNodeRecipe) {
			final Receiver receiver = (Receiver) freshNode;
			List<ReteNodeRecipe> parentRecipes = ((MultiParentNodeRecipe) recipe).getParents();
			for (ReteNodeRecipe parentRecipe : parentRecipes) {
				connectToParent(recipe, receiver, parentRecipe);				
			}
		} else if (recipe instanceof BetaRecipe) {
			final DualInputNode beta = (DualInputNode) freshNode;
			final ArrayList<RecipeTraceInfo> parentTraces = 
					new ArrayList<RecipeTraceInfo>(recipeTrace.getParentRecipeTraces());		
//			final BetaRecipe betaRecipe = (BetaRecipe) recipe;
//	        final IterableIndexer leftParent = (IterableIndexer) resolveIndexer(betaRecipe.getLeftParent());
//	        final Indexer rightParent = resolveIndexer(betaRecipe.getRightParent());
			Slots slots = avoidActiveNodeConflict(parentTraces.get(0), parentTraces.get(1));
			beta.connectToIndexers(slots.primary, slots.secondary);
		} else if (recipe instanceof IndexerBasedAggregatorRecipe) {
			final IndexerBasedAggregatorNode aggregator = (IndexerBasedAggregatorNode) freshNode;
			final IndexerBasedAggregatorRecipe aggregatorRecipe = (IndexerBasedAggregatorRecipe) recipe;
			aggregator.initializeWith((ProjectionIndexer) resolveIndexer(aggregatorRecipe.getParent()));
		}
		// TODO Beta nodes are already connected?
	}

	private Indexer resolveIndexer(final IndexerRecipe indexerRecipe) {
		final Address<? extends Node> address = reteContainer.getNetwork().getExistingNodeByRecipe(indexerRecipe);
		return (Indexer) reteContainer.resolveLocal(address);
	}
	
	private void connectToParent(ReteNodeRecipe recipe, Receiver freshNode, ReteNodeRecipe parentRecipe) {
		final Address<? extends Supplier> parentAddress = (Address<? extends Supplier>) reteContainer.getNetwork().getExistingNodeByRecipe(parentRecipe);
		final Supplier parentSupplier = reteContainer.getProvisioner().asSupplier(parentAddress);
		
		// special synch
		if (freshNode instanceof TransitiveClosureNode) {
            Collection<Tuple> tuples = new ArrayList<Tuple>();
            parentSupplier.pullInto(tuples);
            ((TransitiveClosureNode) freshNode).reinitializeWith(tuples);
			reteContainer.connect(parentSupplier, freshNode); 
		} else { // default case
			// stateless nodes do not have to be synced with contents UNLESS they already have children (recursive corner case)
			if (isStateful(recipe) || ((freshNode instanceof Supplier) && !((Supplier)freshNode).getReceivers().isEmpty())) {
				reteContainer.connectAndSynchronize(parentSupplier, freshNode); 
			} else {
				// stateless node, no synch
				reteContainer.connect(parentSupplier, freshNode); 
			}
		}
	}
	
    /**
     * If two indexers share their active node, joining them via DualInputNode is error-prone. Exception: coincidence of
     * the two indexers is supported.
     *
     * @return a replacement for the secondary Indexers, if needed
     */
    private Slots avoidActiveNodeConflict(final RecipeTraceInfo primarySlot, final RecipeTraceInfo secondarySlot) {
        Slots result = new Slots() {
            {
                primary = (IterableIndexer) resolveIndexer((ProjectionIndexerRecipe) primarySlot.getRecipe());
                secondary = resolveIndexer((IndexerRecipe) secondarySlot.getRecipe());
            }
        };
        if (activeNodeConflict(result.primary, result.secondary))
            if (result.secondary instanceof IterableIndexer)
                result.secondary = resolveActiveIndexer(secondarySlot);
            else
                result.primary = (IterableIndexer) resolveActiveIndexer(primarySlot);
        return result;
    }
	private Indexer resolveActiveIndexer(final RecipeTraceInfo inactiveIndexerTrace) {
		final RecipeTraceInfo activeIndexerTrace = reteContainer.getProvisioner().accessActiveIndexer(inactiveIndexerTrace);
		reteContainer.getProvisioner().getOrCreateNodeByRecipe(activeIndexerTrace);
		return resolveIndexer((ProjectionIndexerRecipe) activeIndexerTrace.getRecipe());
	}

    private static class Slots {
        IterableIndexer primary;
        Indexer secondary;
    }


    /**
     * If two indexers share their active node, joining them via DualInputNode is error-prone. Exception: coincidence of
     * the two indexers is supported.
     *
     * @return true if there is a conflict of active nodes.
     */
    private boolean activeNodeConflict(Indexer primarySlot, Indexer secondarySlot) {
        return !primarySlot.equals(secondarySlot) && primarySlot.getActiveNode().equals(secondarySlot.getActiveNode());
    }

}
