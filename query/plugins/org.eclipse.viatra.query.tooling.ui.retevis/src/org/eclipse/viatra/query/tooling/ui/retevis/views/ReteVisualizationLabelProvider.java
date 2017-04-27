/*******************************************************************************
 * Copyright (c) 2010-2012, Istvan Rath, Zoltan Ujhelyi and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Denes Harmath - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.tooling.ui.retevis.views;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.viatra.addon.viewers.runtime.model.ViewerState;
import org.eclipse.viatra.addon.viewers.runtime.notation.Item;
import org.eclipse.viatra.addon.viewers.runtime.zest.sources.ZestLabelProvider;
import org.eclipse.viatra.query.runtime.matchers.tuple.Tuple;
import org.eclipse.viatra.query.runtime.rete.boundary.ExternalInputEnumeratorNode;
import org.eclipse.viatra.query.runtime.rete.index.IndexerWithMemory;
import org.eclipse.viatra.query.runtime.rete.index.IterableIndexer;
import org.eclipse.viatra.query.runtime.rete.network.Node;
import org.eclipse.viatra.query.runtime.rete.recipes.IndexerRecipe;
import org.eclipse.viatra.query.runtime.rete.recipes.Mask;
import org.eclipse.viatra.query.runtime.rete.recipes.ReteNodeRecipe;
import org.eclipse.viatra.query.runtime.rete.single.UniquenessEnforcerNode;
import org.eclipse.viatra.query.runtime.rete.tuple.MaskedTupleMemory;

public class ReteVisualizationLabelProvider extends ZestLabelProvider {

    private final Map<ReteNodeRecipe, Node> nodeTrace;

    public ReteVisualizationLabelProvider(ViewerState state, Map<ReteNodeRecipe, Node> nodeTrace, Display display) {
        super(state, display);
        this.nodeTrace = nodeTrace;
    }

    @Override
    public Image getImage(Object element) {
        return null;
    }

    @Override
    public String getText(Object element) {
        @SuppressWarnings("restriction")
        String inherited = super.getText(element);
		StringBuilder text = new StringBuilder(inherited == null? "" : inherited);
        if (element instanceof Item) {
            Item item = (Item) element;
            Object paramObject = item.getParamEObject();
            if (paramObject instanceof ReteNodeRecipe) {
                ReteNodeRecipe recipe = (ReteNodeRecipe) paramObject;
                Node node = nodeTrace.get(recipe);
                if (node instanceof ExternalInputEnumeratorNode) {
                    ExternalInputEnumeratorNode input = (ExternalInputEnumeratorNode) node;
                    text.append(formatSize(input.getPulledContents().size()));
                } else if (node instanceof UniquenessEnforcerNode) {
                    UniquenessEnforcerNode uniquenessEnforcerNode = (UniquenessEnforcerNode) node;
                    text.append(formatSize(uniquenessEnforcerNode.getMemory().size()));
                } else if (node instanceof IndexerWithMemory) {
                    IndexerWithMemory indexerWithMemory = (IndexerWithMemory) node;
                    MaskedTupleMemory memory = indexerWithMemory.getMemory();
                    text.append(formatSizeWithBuckets(memory.getTotalSize(), memory.getKeysetSize()));
                } else if (node instanceof IterableIndexer) {
                    IterableIndexer iterableIndexer = (IterableIndexer) node;
                    Collection<Tuple> signatures = iterableIndexer.getSignatures();
                    boolean identityIndexer = false;
                    if (recipe instanceof IndexerRecipe) {
                        Mask mask = ((IndexerRecipe) recipe).getMask();
                        identityIndexer = (mask.getSourceArity() == 
                                new HashSet<Integer>(mask.getSourceIndices()).size());
                    }
                    int bucketCount = signatures.size();
                    int allTuples = 0;
                    if (identityIndexer) {
                        allTuples = bucketCount;
                    } else {
                        for (Tuple bucketSignature : signatures) {
                            allTuples += ((IterableIndexer) node).get(bucketSignature).size();
                        }
                    }
                    text.append(formatSizeWithBuckets(allTuples, bucketCount));
                }
            }
        }
        return text.toString();
    }
    
    private static String formatSize(int size) {
        return "\n [" + size + " tuples]";
    }
    private static String formatSizeWithBuckets(int size, int buckets) {
        return "\n [" + size + " tuples in " + buckets + " buckets]";
    }

}
