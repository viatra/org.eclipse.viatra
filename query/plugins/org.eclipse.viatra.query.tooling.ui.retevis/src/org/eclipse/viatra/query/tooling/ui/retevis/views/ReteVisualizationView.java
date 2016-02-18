/*******************************************************************************
 * Copyright (c) 2010-2012, Istvan Rath, Zoltan Ujhelyi and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Istvan Rath, Zoltan Ujhelyi - initial API and implementation
 *   Denes Harmath - rewrite based on Viewers
 *******************************************************************************/

package org.eclipse.viatra.query.tooling.ui.retevis.views;

import java.util.Collection;

import org.eclipse.gef4.zest.core.viewers.AbstractZoomableViewer;
import org.eclipse.gef4.zest.core.viewers.GraphViewer;
import org.eclipse.gef4.zest.core.viewers.IZoomableWorkbenchPart;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.viatra.addon.viewers.runtime.extensions.ViewersComponentConfiguration;
import org.eclipse.viatra.query.runtime.exception.ViatraQueryException;
import org.eclipse.viatra.query.runtime.matchers.psystem.queries.PQueryHeader;
import org.eclipse.viatra.query.tooling.ui.retevis.ReteVisualization;

import com.google.common.collect.Sets;

public class ReteVisualizationView extends ViewPart implements IZoomableWorkbenchPart {

    @Override
    public void createPartControl(Composite parent) {
        graphViewer = new GraphViewer(parent, SWT.BORDER);
        
        final Collection<String> queryNames = Sets.newHashSet();
        try {
            for (PQueryHeader query : ReteVisualization.instance().getSpecifications()) {
                queryNames.add(query.getFullyQualifiedName());
            }
        } catch (ViatraQueryException e) {
            throw new RuntimeException("Failed to get Rete Visualization query specifications", e);
        }
        viewSupport = new ReteVisualizationViewSupport(
                this, 
                ViewersComponentConfiguration.fromQuerySpecFQNs(queryNames),
                graphViewer);
        viewSupport.createPartControl(parent, graphViewer.getControl());
        viewSupport.createToolbar();
        viewSupport.createLayoutMenu();
    }

    private GraphViewer graphViewer;

    private ReteVisualizationViewSupport viewSupport;

    @Override
    public void dispose() {
        viewSupport.dispose();
        super.dispose();
    }

    @Override
    public AbstractZoomableViewer getZoomableViewer() {
        return graphViewer;
    }

    @Override
    public void setFocus() {
    }

}