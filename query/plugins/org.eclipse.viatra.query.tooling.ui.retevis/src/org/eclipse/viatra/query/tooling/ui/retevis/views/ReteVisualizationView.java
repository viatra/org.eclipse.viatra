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

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.viatra.addon.viewers.runtime.extensions.ViewersComponentConfiguration;
import org.eclipse.viatra.integration.zest.viewer.ModifiableZestContentViewer;
import org.eclipse.viatra.query.runtime.exception.ViatraQueryException;
import org.eclipse.viatra.query.tooling.ui.retevis.ReteVisualization;


public class ReteVisualizationView extends ViewPart {

    @Override
    public void createPartControl(Composite parent) {
        ModifiableZestContentViewer graphViewer =  new ModifiableZestContentViewer();
        graphViewer.createControl(parent, SWT.BORDER);
        
        try {
            viewSupport = new ReteVisualizationViewSupport(
                    this, 
                    ViewersComponentConfiguration.fromQuerySpecs(ReteVisualization.instance().getSpecifications()),
                    graphViewer);
            viewSupport.createPartControl(parent, graphViewer.getControl());
            viewSupport.createToolbar();
            viewSupport.createLayoutMenu();
        } catch (ViatraQueryException e) {
            throw new RuntimeException("Failed to get Rete Visualization query specifications", e);
        }
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