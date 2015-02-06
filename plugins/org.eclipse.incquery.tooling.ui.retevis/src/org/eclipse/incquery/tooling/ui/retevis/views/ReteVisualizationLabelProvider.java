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

import org.eclipse.gef4.zest.core.viewers.IConnectionStyleProvider;
import org.eclipse.gef4.zest.core.viewers.IEntityStyleProvider;
import org.eclipse.incquery.runtime.matchers.psystem.queries.BasePQuery;
import org.eclipse.incquery.runtime.rete.recipes.ProductionRecipe;
import org.eclipse.incquery.viewers.runtime.model.Item;
import org.eclipse.incquery.viewers.runtime.model.ViewerState;
import org.eclipse.incquery.viewers.runtime.zest.sources.ZestLabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

public class ReteVisualizationLabelProvider extends ZestLabelProvider implements IEntityStyleProvider, IConnectionStyleProvider {

    public ReteVisualizationLabelProvider(ViewerState state, Display display) {
        super(state, display);
    }

    @Override
    public Image getImage(Object element) {
        return null;
    }

    @Override
    public String getText(Object element) {
        if (element instanceof Item) {
            Item item = (Item) element;
            Object paramObject = item.getParamObject();
            if (paramObject instanceof ProductionRecipe) {
                ProductionRecipe productionRecipe = (ProductionRecipe) paramObject;
                return getProductionRecipeLabel(productionRecipe);
            }
        }
        return super.getText(element);
    }

    private String getProductionRecipeLabel(ProductionRecipe productionRecipe) {
        Object pattern = productionRecipe.getPattern();
        if (pattern instanceof BasePQuery) {
            BasePQuery pQuery = (BasePQuery) pattern;
            return "Production: " + pQuery.getFullyQualifiedName();
        } else { // shouldn't happen
            return "Production";
        }
    }

}
