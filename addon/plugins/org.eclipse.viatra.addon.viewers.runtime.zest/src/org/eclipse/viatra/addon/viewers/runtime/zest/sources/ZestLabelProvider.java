/*******************************************************************************
 * Copyright (c) 2010-2013, Zoltan Ujhelyi, Istvan Rath and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.addon.viewers.runtime.zest.sources;


import java.util.HashMap;
import java.util.Map;

import org.eclipse.gef.zest.fx.ZestProperties;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.resource.LocalResourceManager;
import org.eclipse.jface.resource.ResourceManager;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.viatra.addon.viewers.runtime.notation.FormattableElement;
import org.eclipse.viatra.addon.viewers.runtime.notation.Item;
import org.eclipse.viatra.addon.viewers.runtime.sources.QueryLabelProvider;
import org.eclipse.viatra.addon.viewers.runtime.util.FormatParser;
import org.eclipse.viatra.integration.zest.viewer.IGraphAttributesProvider2;

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
    
    private ResourceManager resourceManager = new LocalResourceManager(JFaceResources.getResources());

    private Color getColorProperty(FormattableElement element, String property) {
        if (FormatParser.isFormatted(element)) {
            RGB color = FormatParser.getColorFormatProperty(element,property);
            if (color != null) {
                return resourceManager.createColor(color);
            }
        }
        return null;
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
        resourceManager.dispose();
        super.dispose();
    }

    @Override
    public Map<String, Object> getEdgeAttributes(Object sourceNode, Object targetNode) {
        Map<String, Object> attributes = new HashMap<>();
        attributes.put(ZestProperties.TARGET_DECORATION__E, new DiamondHead());
        return attributes;
    }
    
    @Override
    public Map<String, Object> getEdgeAttributes(Object edge) {
        Map<String, Object> attributes = new HashMap<>();
        attributes.put(ZestProperties.TARGET_DECORATION__E, new DiamondHead());
        return attributes;
    }

    @Override
    public Map<String, Object> getGraphAttributes() {
        return new HashMap<>();
    }

    @Override
    public Map<String, Object> getNestedGraphAttributes(Object nestingNode) {
        return new HashMap<>();
    }

    @Override
    public Map<String, Object> getNodeAttributes(Object node) {
        return new HashMap<>();
    }


}
