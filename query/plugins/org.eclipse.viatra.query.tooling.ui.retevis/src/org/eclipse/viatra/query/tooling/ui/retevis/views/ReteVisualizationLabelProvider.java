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

import java.util.Map;

import org.eclipse.gef4.zest.core.viewers.IConnectionStyleProvider;
import org.eclipse.gef4.zest.core.viewers.IEntityStyleProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.viatra.addon.viewers.runtime.model.ViewerState;
import org.eclipse.viatra.addon.viewers.runtime.notation.Item;
import org.eclipse.viatra.addon.viewers.runtime.zest.sources.ZestLabelProvider;
import org.eclipse.viatra.query.runtime.matchers.psystem.aggregations.IMultisetAggregationOperator;
import org.eclipse.viatra.query.runtime.rete.index.IndexerWithMemory;
import org.eclipse.viatra.query.runtime.rete.index.IterableIndexer;
import org.eclipse.viatra.query.runtime.rete.network.Node;
import org.eclipse.viatra.query.runtime.rete.recipes.ReteNodeRecipe;
import org.eclipse.viatra.query.runtime.rete.recipes.SingleColumnAggregatorRecipe;
import org.eclipse.viatra.query.runtime.rete.single.UniquenessEnforcerNode;

public class ReteVisualizationLabelProvider extends ZestLabelProvider implements IEntityStyleProvider, IConnectionStyleProvider {

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
        String inherited = super.getText(element);
		StringBuffer text = new StringBuffer(inherited == null? "" : inherited);
        if (element instanceof Item) {
            Item item = (Item) element;
            Object paramObject = item.getParamEObject();
            if (paramObject instanceof ReteNodeRecipe) {
                ReteNodeRecipe recipe = (ReteNodeRecipe) paramObject;
                Node node = nodeTrace.get(recipe);
                if (node instanceof UniquenessEnforcerNode) {
                    UniquenessEnforcerNode uniquenessEnforcerNode = (UniquenessEnforcerNode) node;
                    text.append(formatSize(uniquenessEnforcerNode.getMemory().size()));
                } else if (node instanceof IndexerWithMemory) {
                    IndexerWithMemory indexerWithMemory = (IndexerWithMemory) node;
                    text.append(formatSize(indexerWithMemory.getMemory().getKeysetSize()));
                } else if (node instanceof IterableIndexer) {
                    IterableIndexer iterableIndexer = (IterableIndexer) node;
                    text.append(formatSize(iterableIndexer.getSignatures().size()));
                } else if (paramObject instanceof SingleColumnAggregatorRecipe) {
                	IMultisetAggregationOperator<?, ?, ?> operator = ((SingleColumnAggregatorRecipe) paramObject).getMultisetAggregationOperator();
					if (operator == null) {
						text.append("Undefined operator");
					} else {
						text.append(operator.getName());
					}
                }
            }
        }
        return text.toString();
    }
    
    private static String formatSize(int size) {
        return " [" + size + "]";
    }

}
