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

import org.eclipse.gef.zest.fx.ZestProperties;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;
import org.eclipse.viatra.addon.viewers.runtime.model.ViewerState;
import org.eclipse.viatra.addon.viewers.runtime.notation.FormattableElement;
import org.eclipse.viatra.addon.viewers.runtime.notation.Item;
import org.eclipse.viatra.addon.viewers.runtime.sources.QueryLabelProvider;
import org.eclipse.viatra.addon.viewers.runtime.util.FormatParser;
import org.eclipse.viatra.integration.zest.viewer.IGraphAttributesProvider2;

import com.google.common.collect.Maps;

import javafx.scene.shape.Polygon;


/**
 * Label provider for Zest graphs.
 * 
 * @author Zoltan Ujhelyi
 * 
 */
public class ZestLabelProvider extends QueryLabelProvider implements IColorProvider, IGraphAttributesProvider2 {

    static class DiamondHead extends Polygon {
        public DiamondHead() {
            super(-15.0, 0.0, -7.5, -3.75, -7.5, 3.75, -15.0, 0.0);
        }
    }
    
    private Map<RGB, Color> colorMap = Maps.newHashMap();

    public ZestLabelProvider(ViewerState state, Display display) {
        super(state, display);

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

    private Color getColor(RGB rgb) {
        if (!colorMap.containsKey(rgb)) {
            Color newColor = new Color(display, rgb);
            colorMap.put(rgb, newColor);
            return newColor;
        }
        return colorMap.get(rgb);
    }

    @Override
    public Color getBackground(Object entity) {
        if (entity instanceof Item) {
            return getColorProperty((FormattableElement) entity, FormatParser.COLOR);
        }
        return null;
    }

    @Override
    public Color getForeground(Object entity) {
        if (entity instanceof Item) {
            return getColorProperty((FormattableElement) entity, FormatParser.TEXT_COLOR);
        }
        return null;
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
    public Map<String, Object> getEdgeAttributes(Object sourceNode, Object targetNode) {
        Map<String, Object> attributes = Maps.newHashMap();
        attributes.put(ZestProperties.TARGET_DECORATION__E, new DiamondHead());
        return attributes;
    }
    
    @Override
    public Map<String, Object> getEdgeAttributes(Object edge) {
        Map<String, Object> attributes = Maps.newHashMap();
        attributes.put(ZestProperties.TARGET_DECORATION__E, new DiamondHead());
        return attributes;
    }

    @Override
    public Map<String, Object> getGraphAttributes() {
        return Maps.newHashMap();
    }

    @Override
    public Map<String, Object> getNestedGraphAttributes(Object nestingNode) {
        return Maps.newHashMap();
    }

    @Override
    public Map<String, Object> getNodeAttributes(Object node) {
        return Maps.newHashMap();
    }


}
