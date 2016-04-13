/*******************************************************************************
 * Copyright (c) 2004-2015, Peter Lunk, Zoltan Ujhelyi and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *   Peter Lunk - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.transformation.debug.ui.views;

import java.util.Set;

import org.eclipse.gef4.layout.algorithms.TreeLayoutAlgorithm;
import org.eclipse.gef4.zest.core.viewers.GraphViewer;
import org.eclipse.gef4.zest.core.widgets.ZestStyles;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.viatra.addon.viewers.runtime.model.ViatraViewerDataModel;
import org.eclipse.viatra.addon.viewers.runtime.model.ViewerDataFilter;
import org.eclipse.viatra.addon.viewers.runtime.model.ViewerState;
import org.eclipse.viatra.addon.viewers.runtime.model.ViewerState.ViewerStateFeature;
import org.eclipse.viatra.addon.viewers.runtime.zest.ViatraGraphViewers;
import org.eclipse.viatra.query.runtime.api.IQuerySpecification;
import org.eclipse.viatra.query.runtime.api.ViatraQueryEngine;
import org.eclipse.viatra.transformation.debug.model.ITransformationStateListener;
import org.eclipse.viatra.transformation.debug.model.TransformationState;
import org.eclipse.viatra.transformation.debug.model.TransformationThreadFactory;

import com.google.common.collect.ImmutableSet;

public class TransformationViewer extends ViewPart implements ITransformationStateListener {
    public TransformationViewer() {
    }

    public static final String ID = "org.eclipse.viatra.transformation.ui.debug.TransformationViewer";
    private GraphViewer graphViewer;
    private Composite composite;
    private ViatraQueryEngine engine;
    private ViewerState viewerState;

    @Override
    public void createPartControl(Composite parent) {

        composite = new Composite(parent, SWT.NONE);
        composite.setLayout(new GridLayout(4, false));

        graphViewer = new GraphViewer(composite, ZestStyles.NONE);
        graphViewer.getControl().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 4, 1));
        graphViewer.setLayoutAlgorithm(new TreeLayoutAlgorithm());
        
        TransformationThreadFactory.INSTANCE.addListener(this);

    }

    @Override
    public void setFocus() {
        graphViewer.getControl().setFocus();
    }

    @Override
    public void transformationStateChanged(final TransformationState state) {
        graphViewer.getControl().getDisplay().syncExec(new Runnable() {

            @Override
            public void run() {
                if (engine == null && state.getEngine() != null) {
                    engine = state.getEngine();
                    Set<IQuerySpecification<?>> queries = loadQueries();
                    viewerState = ViatraViewerDataModel.newViewerState(engine, queries,
                            ViewerDataFilter.UNFILTERED, ImmutableSet.of(ViewerStateFeature.EDGE));
                    ViatraGraphViewers.bind(graphViewer, viewerState);
                    graphViewer.setLayoutAlgorithm(new TreeLayoutAlgorithm(), true);
                    graphViewer.applyLayout();
                }
            }
        });

    }

    @Override
    public void transformationStateDisposed(TransformationState state) {
        graphViewer.getControl().getDisplay().syncExec(new Runnable() {

            @Override
            public void run() {
                engine = null;
                viewerState.dispose();    
            }
        });

    }

    private Set<IQuerySpecification<?>> loadQueries() {
        return engine.getRegisteredQuerySpecifications();

    }
    
    @Override
    public void dispose() {
        super.dispose();
        engine = null;
        TransformationThreadFactory.INSTANCE.unRegisterListener(this);
    }

}
