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
package org.eclipse.incquery.tooling.ui.retevis.views;

import java.util.Map;

import org.eclipse.gef4.zest.core.viewers.IConnectionStyleProvider;
import org.eclipse.gef4.zest.core.viewers.IEntityStyleProvider;
import org.eclipse.incquery.runtime.matchers.psystem.queries.BasePQuery;
import org.eclipse.incquery.runtime.rete.index.IndexerWithMemory;
import org.eclipse.incquery.runtime.rete.index.IterableIndexer;
import org.eclipse.incquery.runtime.rete.network.Node;
import org.eclipse.incquery.runtime.rete.recipes.ProductionRecipe;
import org.eclipse.incquery.runtime.rete.recipes.ReteNodeRecipe;
import org.eclipse.incquery.runtime.rete.single.UniquenessEnforcerNode;
import org.eclipse.incquery.viewers.runtime.model.Item;
import org.eclipse.incquery.viewers.runtime.model.ViewerState;
import org.eclipse.incquery.viewers.runtime.zest.sources.ZestLabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

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
        StringBuffer text = new StringBuffer(super.getText(element));
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
                }
            }
        }
        return text.toString();
    }
    
    private static String formatSize(int size) {
        return " [" + size + "]";
    }

}
