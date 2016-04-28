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

import java.util.List;

import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.progress.IWorkbenchSiteProgressService;
import org.eclipse.viatra.query.runtime.api.ViatraQueryEngine;
import org.eclipse.viatra.query.runtime.api.scope.QueryScope;
import org.eclipse.viatra.query.runtime.emf.EMFScope;
import org.eclipse.viatra.transformation.debug.model.ITransformationStateListener;
import org.eclipse.viatra.transformation.debug.model.TransformationState;
import org.eclipse.viatra.transformation.debug.model.TransformationThreadFactory;

import com.google.common.collect.Lists;

public class ModelInstanceViewer extends ViewPart implements ITransformationStateListener {
    public ModelInstanceViewer() {
    }

    public static final String ID = "org.eclipse.viatra.transformation.ui.debug.TransformationViewer";
    private TreeViewer treeViewer;
    private Composite composite;
    private ViatraQueryEngine engine;

    @Override
    public void createPartControl(Composite parent) {

        composite = new Composite(parent, SWT.NONE);
        composite.setLayout(new FillLayout(SWT.HORIZONTAL));
        treeViewer = new TreeViewer(composite, SWT.BORDER);
        
        
        
    }

    @Override
    public void setFocus() {
        treeViewer.getControl().setFocus();
    }
    
    //TODO change content based on the selected Adaptable EVM instance
    
    @Override
    public void transformationStateChanged(final TransformationState state, String id) {
        treeViewer.getControl().getDisplay().syncExec(new Runnable() {
            @Override
            public void run() {
                if(engine == null){
                    engine = state.getEngine();
                    treeViewer.setInput(getModel()); 
                }   
            }
        });
        

    }

    @Override
    public void transformationStateDisposed(TransformationState state, final String id) {
        treeViewer.getControl().getDisplay().syncExec(new Runnable() {
            @Override
            public void run() {
                engine = null;
                treeViewer.setInput(null);    
            }
        });

    }
    
    private List<EObject> getModel() {
        QueryScope scope = engine.getScope();
        if(scope instanceof EMFScope){
            List<EObject> input = Lists.newArrayList();
            if(((EMFScope) scope).getScopeRoots().size()>0){
                Notifier root = Lists.newArrayList(((EMFScope) scope).getScopeRoots()).get(0);
                if(root instanceof ResourceSet){
                    EList<Resource> resources = ((ResourceSet)root).getResources();
                    for (Resource resource : resources) {
                        input.addAll(((Resource) resource).getContents());
                    }
                }
            }
            
            
            return input;
        }
        return null;

    }
    
    public void registerToId(String id){
        TransformationThreadFactory.INSTANCE.registerListener(this, id);
    }
    
    
    @Override
    public void dispose() {
        super.dispose();
        engine = null;
        TransformationThreadFactory.INSTANCE.unRegisterListener(this);
    }
    
    public IWorkbenchSiteProgressService getProgressService() {
        IWorkbenchSiteProgressService service = null;
        Object siteService = getSite().getAdapter(IWorkbenchSiteProgressService.class);
        if (siteService != null) {
            service = (IWorkbenchSiteProgressService) siteService;
        }
        return service;
    }
    

}
