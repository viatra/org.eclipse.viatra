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
package org.eclipse.incquery.viewers.runtime.zest.sources;


import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.draw2d.ConnectionRouter;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.gef4.zest.core.viewers.IConnectionStyleProvider;
import org.eclipse.gef4.zest.core.viewers.IEntityStyleProvider;
import org.eclipse.gef4.zest.core.widgets.ZestStyles;
import org.eclipse.incquery.runtime.rete.network.Node;
import org.eclipse.incquery.viewers.runtime.model.Edge;
import org.eclipse.incquery.viewers.runtime.model.FormatSpecification;
import org.eclipse.incquery.viewers.runtime.model.FormattableElement;
import org.eclipse.incquery.viewers.runtime.model.Item;
import org.eclipse.incquery.viewers.runtime.sources.QueryLabelProvider;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

import com.google.common.collect.Maps;


/**
 * Label provider for Zest graphs.
 * 
 * @author Zoltan Ujhelyi
 * 
 */
public class ZestLabelProvider extends QueryLabelProvider implements IEntityStyleProvider, IConnectionStyleProvider {

    private Display display;
    private Map<RGB, Color> colorMap = Maps.newHashMap();

    public ZestLabelProvider(Display display) {
        this.display = display;

    }

    private Color getColorProperty(FormattableElement element, String property) {
        if (element.isFormatted()) {
            RGB color = element.getColorFormatProperty(property);
            if (color != null) {
                return getColor(color);
            }
        }
        return null;
    }

    private int getIntProperty(FormattableElement element, String property) {
        if (element.isFormatted()) {
            return element.getNumberPropery(property);
        }
        return -1;
    }

    private Color getColor(RGB rgb) {
        if (!colorMap.containsKey(rgb)) {
            Color newColor = new Color(display, rgb);
            colorMap.put(rgb, newColor);
            return newColor;
        }
        return colorMap.get(rgb);
    }

	@Override
	public Color getNodeHighlightColor(Object entity) {
        return null;
	}

	@Override
    public Color getBorderColor(Object entity) {
        if (entity instanceof Item) {
            return getColorProperty((FormattableElement) entity, FormatSpecification.LINE_COLOR);
        }
        return null;
    }

	@Override
	public Color getBorderHighlightColor(Object entity) {
        return null;
	}

	@Override
	public int getBorderWidth(Object entity) {
        if (entity instanceof Node) {
            return getIntProperty((FormattableElement) entity, FormatSpecification.LINE_WIDTH);
        }
        return -1;
	}

	@Override
	public Color getBackgroundColour(Object entity) {
        if (entity instanceof Item) {
            return getColorProperty((FormattableElement) entity, FormatSpecification.COLOR);
        }
        return null;
	}

	@Override
	public Color getForegroundColour(Object entity) {
        if (entity instanceof Item) {
            return getColorProperty((FormattableElement) entity, FormatSpecification.TEXT_COLOR);
        }
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
	public boolean fisheyeNode(Object entity) {
		return false;
	}

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.incquery.querybasedui.runtime.sources.QueryLabelProvider#dispose()
     */
    @Override
    public void dispose() {
        for (Entry<RGB, Color> colorEntry : colorMap.entrySet()) {
            Color color = colorEntry.getValue();
            if (color != null && !color.isDisposed()) {
                color.dispose();
            }
        }
        super.dispose();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.gef4.zest.core.viewers.IConnectionStyleProvider#getConnectionStyle(java.lang.Object)
     */
    @Override
    public int getConnectionStyle(Object rel) {
        return ZestStyles.CONNECTIONS_DIRECTED;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.gef4.zest.core.viewers.IConnectionStyleProvider#getColor(java.lang.Object)
     */
    @Override
    public Color getColor(Object rel) {
        if (rel instanceof Edge) {
            return getColorProperty((FormattableElement) rel, FormatSpecification.COLOR);
        }
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.gef4.zest.core.viewers.IConnectionStyleProvider#getHighlightColor(java.lang.Object)
     */
    @Override
    public Color getHighlightColor(Object rel) {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.gef4.zest.core.viewers.IConnectionStyleProvider#getLineWidth(java.lang.Object)
     */
    @Override
    public int getLineWidth(Object rel) {
        if (rel instanceof Edge) {
            return getIntProperty((FormattableElement) rel, FormatSpecification.LINE_WIDTH);
        }
        return -1;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.gef4.zest.core.viewers.IConnectionStyleProvider#getRouter(java.lang.Object)
     */
    @Override
    public ConnectionRouter getRouter(Object rel) {
        // TODO Auto-generated method stub
        return null;
    }

}
