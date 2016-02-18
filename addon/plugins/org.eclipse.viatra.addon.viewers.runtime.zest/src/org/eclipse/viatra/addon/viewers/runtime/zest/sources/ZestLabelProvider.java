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
package org.eclipse.viatra.addon.viewers.runtime.zest.sources;


import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.draw2d.BendpointConnectionRouter;
import org.eclipse.draw2d.ConnectionRouter;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.gef4.zest.core.viewers.IConnectionStyleProvider;
import org.eclipse.gef4.zest.core.viewers.IEntityStyleProvider;
import org.eclipse.gef4.zest.core.widgets.ZestStyles;
import org.eclipse.gef4.zest.core.widgets.decoration.IConnectionDecorator;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;
import org.eclipse.viatra.addon.viewers.runtime.model.ViewerState;
import org.eclipse.viatra.addon.viewers.runtime.notation.Containment;
import org.eclipse.viatra.addon.viewers.runtime.notation.Edge;
import org.eclipse.viatra.addon.viewers.runtime.notation.FormattableElement;
import org.eclipse.viatra.addon.viewers.runtime.notation.Item;
import org.eclipse.viatra.addon.viewers.runtime.sources.QueryLabelProvider;
import org.eclipse.viatra.addon.viewers.runtime.util.FormatParser;
import org.eclipse.viatra.query.runtime.rete.network.Node;

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

    public ZestLabelProvider(ViewerState state, Display display) {
    	super(state);
        this.display = display;

    }

    private Color getColorProperty(FormattableElement element, String property) {
        if (FormatParser.isFormatted(element)) {
            RGB color = FormatParser.getColorFormatProperty(element,property);
            if (color != null) {
                return getColor(color);
            }
        }
        return null;
    }

    private int getIntProperty(FormattableElement element, String property) {
        if (FormatParser.isFormatted(element)) {
            return FormatParser.getNumberProperty(element,property);
        }
        return -1;
    }
    
    private String getStringProperty(FormattableElement element, String property) {
    	if (FormatParser.isFormatted(element)) {
    		return FormatParser.getStringProperty(element, property);
    	}
    	return "";
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
            return getColorProperty((FormattableElement) entity, FormatParser.LINE_COLOR);
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
            return getIntProperty((FormattableElement) entity, FormatParser.LINE_WIDTH);
        }
        return -1;
	}

	@Override
	public Color getBackgroundColour(Object entity) {
        if (entity instanceof Item) {
            return getColorProperty((FormattableElement) entity, FormatParser.COLOR);
        }
        return null;
	}

	@Override
	public Color getForegroundColour(Object entity) {
        if (entity instanceof Item) {
            return getColorProperty((FormattableElement) entity, FormatParser.TEXT_COLOR);
        }
        return null;
	}

	@Override
	public IFigure getTooltip(Object entity) {
        if (!(entity instanceof Item)) {
            return null;
        }
        final Object entityObject = ((Item) entity).getParamObject();
        if (!(entityObject instanceof EObject)) {
            return null;
        }
		EObject eobj = (EObject) entityObject;
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

    @Override
    public int getConnectionStyle(Object rel) {
    	// handle containments specially
    	if (rel instanceof Containment) {
    		return ZestStyles.CONNECTIONS_DOT | ZestStyles.CONNECTIONS_DIRECTED;
    	}
    	// handle lineStyle property
    	if (rel instanceof FormattableElement) {
    		String lS = getStringProperty((FormattableElement) rel, FormatParser.LINE_STYLE);
    		if ("dashed".equalsIgnoreCase(lS)) {
    			return ZestStyles.CONNECTIONS_DASH | ZestStyles.CONNECTIONS_DIRECTED;
    		}
    		else if ("dotted".equalsIgnoreCase(lS)) {
    			return ZestStyles.CONNECTIONS_DOT | ZestStyles.CONNECTIONS_DIRECTED;
    		}
    		else if ("dashdot".equalsIgnoreCase(lS)) {
    			return ZestStyles.CONNECTIONS_DASH_DOT | ZestStyles.CONNECTIONS_DIRECTED;
    		}
    	}
    	// default
        return ZestStyles.CONNECTIONS_SOLID | ZestStyles.CONNECTIONS_DIRECTED;
    }

    @Override
    public Color getColor(Object rel) {
        if (rel instanceof Edge) {
            return getColorProperty((FormattableElement) rel, FormatParser.LINE_COLOR);
        }
        return null;
    }

    @Override
    public Color getHighlightColor(Object rel) {
        return null;
    }

    @Override
    public int getLineWidth(Object rel) {
        if (rel instanceof Edge) {
            return getIntProperty((FormattableElement) rel, FormatParser.LINE_WIDTH);
        }
        return -1;
    }

    @Override
    public ConnectionRouter getRouter(Object rel) {
        return new BendpointConnectionRouter();
    }

	@Override
	public IConnectionDecorator getConnectionDecorator(Object rel) {
		// TODO implement connection decorator
		return null;
	}

}
