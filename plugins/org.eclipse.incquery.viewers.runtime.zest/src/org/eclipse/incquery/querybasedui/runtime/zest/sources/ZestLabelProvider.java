/*******************************************************************************
 * Copyright (c) 2010-2013, Zoltan Ujhelyi, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi - initial API and implementation
 *******************************************************************************/
package org.eclipse.incquery.querybasedui.runtime.zest.sources;


import org.eclipse.draw2d.ConnectionRouter;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.gef4.zest.core.viewers.IEntityConnectionStyleProvider;
import org.eclipse.gef4.zest.core.viewers.IEntityStyleProvider;
import org.eclipse.incquery.querybasedui.runtime.sources.QueryLabelProvider;
import org.eclipse.swt.graphics.Color;


/**
 * Label provider for Zest graphs.
 * 
 * @author Zoltan Ujhelyi
 * 
 */
public class ZestLabelProvider extends QueryLabelProvider implements IEntityStyleProvider, IEntityConnectionStyleProvider {

    @Override
	public int getConnectionStyle(Object src, Object dest) {
        return 0;
	}

	@Override
	public Color getColor(Object src, Object dest) {
        return null;
	}

	@Override
	public Color getHighlightColor(Object src, Object dest) {
        return null;
	}

	@Override
	public int getLineWidth(Object src, Object dest) {
        return -1;
	}

	@Override
	public Color getNodeHighlightColor(Object entity) {
        return null;
	}

	@Override
	public Color getBorderColor(Object entity) {
        return null;
	}

	@Override
	public Color getBorderHighlightColor(Object entity) {
        return null;
	}

	@Override
	public int getBorderWidth(Object entity) {
        return -1;
	}

	@Override
	public Color getBackgroundColour(Object entity) {
        return null;
	}

	@Override
	public Color getForegroundColour(Object entity) {
        return null;
	}

	@Override
	public IFigure getTooltip(Object entity) {
        if (!(entity instanceof EObject)) {
            return null;
        }
		EObject eobj = (EObject) entity;
		String text = "";
		for (EStructuralFeature feature : eobj.eClass().getEAllAttributes()) {
			text = text.concat(feature.getName() + ": ");
			Object obj = eobj.eGet(feature);
			if (obj == null) {
				text = text.concat("\n");
			} else {
				text = text.concat(eobj.eGet(feature).toString() + "\n");
			}
		}
		Label label = new Label(text);
		return label;
	}

    @Override
    public IFigure getTooltip(Object src, Object dest) {
        return null;
    }

	@Override
	public boolean fisheyeNode(Object entity) {
		return false;
	}

    @Override
    public ConnectionRouter getRouter(Object src, Object dest) {
        return null;
    }
}
