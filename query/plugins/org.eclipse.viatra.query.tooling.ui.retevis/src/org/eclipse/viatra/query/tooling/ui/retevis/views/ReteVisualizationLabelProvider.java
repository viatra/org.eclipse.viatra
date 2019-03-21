/*******************************************************************************
 * Copyright (c) 2010-2012, Istvan Rath, Zoltan Ujhelyi and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.query.tooling.ui.retevis.views;

import java.util.HashSet;
import java.util.Map;

import org.eclipse.swt.graphics.Image;
import org.eclipse.viatra.addon.viewers.runtime.notation.Item;
import org.eclipse.viatra.addon.viewers.runtime.zest.sources.ZestLabelProvider;
import org.eclipse.viatra.query.runtime.matchers.memories.MaskedTupleMemory;
import org.eclipse.viatra.query.runtime.matchers.tuple.Tuple;
import org.eclipse.viatra.query.runtime.rete.boundary.ExternalInputEnumeratorNode;
import org.eclipse.viatra.query.runtime.rete.index.IndexerWithMemory;
import org.eclipse.viatra.query.runtime.rete.index.IterableIndexer;
import org.eclipse.viatra.query.runtime.rete.network.Node;
import org.eclipse.viatra.query.runtime.rete.recipes.IndexerRecipe;
import org.eclipse.viatra.query.runtime.rete.recipes.Mask;
import org.eclipse.viatra.query.runtime.rete.recipes.ReteNodeRecipe;
import org.eclipse.viatra.query.runtime.rete.single.AbstractUniquenessEnforcerNode;

public class ReteVisualizationLabelProvider extends ZestLabelProvider {

    private final Map<ReteNodeRecipe, Node> nodeTrace;

    public ReteVisualizationLabelProvider(Map<ReteNodeRecipe, Node> nodeTrace) {
        super();
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
                } else if (node instanceof AbstractUniquenessEnforcerNode) {
                    AbstractUniquenessEnforcerNode uniquenessEnforcerNode = (AbstractUniquenessEnforcerNode) node;
                    text.append(formatSize(uniquenessEnforcerNode.getMemory().size()));
                } else if (node instanceof IndexerWithMemory) {
                    IndexerWithMemory indexerWithMemory = (IndexerWithMemory) node;
                    MaskedTupleMemory memory = indexerWithMemory.getMemory();
                    text.append(formatSizeWithBuckets(memory.getTotalSize(), memory.getKeysetSize()));
                } else if (node instanceof IterableIndexer) {
                    IterableIndexer iterableIndexer = (IterableIndexer) node;
                    Iterable<Tuple> signatures = iterableIndexer.getSignatures();
                    int bucketCount = iterableIndexer.getBucketCount();
                    boolean identityIndexer = false;
                    if (recipe instanceof IndexerRecipe) {
                        Mask mask = ((IndexerRecipe) recipe).getMask();
                        identityIndexer = (mask.getSourceArity() == 
                                new HashSet<Integer>(mask.getSourceIndices()).size());
                    }
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
