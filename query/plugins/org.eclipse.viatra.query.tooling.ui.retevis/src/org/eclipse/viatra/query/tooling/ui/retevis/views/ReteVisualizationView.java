/*******************************************************************************
 * Copyright (c) 2010-2012, Istvan Rath, Zoltan Ujhelyi, Denes Harmath and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/

package org.eclipse.viatra.query.tooling.ui.retevis.views;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.viatra.addon.viewers.runtime.extensions.ViewersComponentConfiguration;
import org.eclipse.viatra.integration.zest.viewer.ViatraGraphViewer;
import org.eclipse.viatra.query.tooling.ui.retevis.ReteVisualization;
import org.eclipse.zest.core.widgets.ZestStyles;


public class ReteVisualizationView extends ViewPart {

    @Override
    public void createPartControl(Composite parent) {
        ViatraGraphViewer graphViewer =  new ViatraGraphViewer(parent, SWT.BORDER);
        graphViewer.setNodeStyle(ZestStyles.NODES_NO_LAYOUT_RESIZE);
        getSite().setSelectionProvider(graphViewer);
        
        viewSupport = new ReteVisualizationViewSupport(
                this, 
                ViewersComponentConfiguration.fromQuerySpecs(ReteVisualization.instance().getSpecifications()),
                graphViewer);
        viewSupport.createPartControl(parent, graphViewer.getControl());
        viewSupport.createToolbar();
        viewSupport.createLayoutMenu();
    }

    private ReteVisualizationViewSupport viewSupport;

    @Override
    public void dispose() {
        if (viewSupport != null) {
            viewSupport.dispose();
            viewSupport = null;
        }
        super.dispose();
    }

    @Override
    public void setFocus() {
    }

}