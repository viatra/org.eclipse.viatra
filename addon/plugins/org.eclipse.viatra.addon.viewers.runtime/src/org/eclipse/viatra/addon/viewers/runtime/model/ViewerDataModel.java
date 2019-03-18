/*******************************************************************************
 * Copyright (c) 2010-2013, Csaba Debreceni, Zoltan Ujhelyi, Istvan Rath and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.addon.viewers.runtime.model;

import java.util.Collection;

import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.viatra.addon.viewers.runtime.notation.NotationFactory;
import org.eclipse.viatra.addon.viewers.runtime.notation.NotationModel;
import org.eclipse.viatra.query.runtime.api.IQuerySpecification;
import org.eclipse.viatra.query.runtime.api.ViatraQueryEngine;
import org.eclipse.viatra.query.runtime.base.api.NavigationHelper;
import org.eclipse.viatra.query.runtime.emf.EMFScope;
import org.eclipse.viatra.query.runtime.util.ViatraQueryLoggingUtil;
import org.eclipse.viatra.transformation.views.core.ViewModelManager;

/**
 * Data model collecting input from multiple query results, and returns them as {@link ObservableSet} instances.
 * 
 * 
 * @author Zoltan Ujhelyi
 * @author Istvan Rath
 * @author Csaba Debreceni
 * 
 */
public abstract class ViewerDataModel {

    private static final String NOTATION_RESOURCE = "org.eclipse.viatra.addon.viewers.notation.NotationResource";
    
    protected NotationModel model;
    protected ViatraQueryEngine engine;

    public ViewerDataModel(ResourceSet notifier) {
        this(ViatraQueryEngine.on(new EMFScope(notifier)));
    }

    public ViewerDataModel(ViatraQueryEngine engine) {
        if (!(engine.getScope() instanceof EMFScope)) {
            ViatraQueryLoggingUtil.getLogger(ViewModelManager.class).error(
                    "Only EMFScope is supported currently for ViatraQueryEngine");
            return;
        }
        
        model = NotationFactory.eINSTANCE.createNotationModel();
        this.engine = engine;
        prepareBaseNotifier();
    }
    
    private void prepareBaseNotifier() {

        Collection<? extends Notifier> notifiers = getNotifiers();
        Resource resource = null;

        for(Notifier notifier : notifiers) {
            if(notifier instanceof Resource) {
                Resource r = (Resource) notifier;
                if(checkNotationResource(r)) {
                    resource = r;
                    break;
                }
            }
            if(notifier instanceof ResourceSet) {
                ResourceSet resourceSet = (ResourceSet) notifier;
                for (Resource r : resourceSet.getResources()) {
                    if(checkNotationResource(r)) {
                        resource = r;
                        break;
                    }
                }
            }
            
        }
        if (resource == null) {
            resource = addNotationResource();
        }

//        resource.getContents().clear();
        resource.getContents().add(model);
    }
    
    private String getNotationResourceId() {
        return NOTATION_RESOURCE;
    }

    public NotationModel getNotationModel() {
        return model;
    }

    public ViatraQueryEngine getEngine() {
        return engine;
    }
    
    public void dispose() {
        EcoreUtil.delete(model);
    }

    public abstract Collection<IQuerySpecification<?>> getPatterns();

    private Resource addNotationResource() {
        ResourceSet resourceSet = new ResourceSetImpl();
        Resource resource = resourceSet.createResource(URI.createURI(getNotationResourceId()));
        NavigationHelper helper = EMFScope.extractUnderlyingEMFIndex(engine);
        helper.addRoot(resourceSet);
        
        return resource;
    }
    
    private boolean checkNotationResource(Resource r) {
        return r.getURI().toString().equals(getNotationResourceId());
    }
    
    private Collection<? extends Notifier> getNotifiers() {
        return ((EMFScope) engine.getScope()).getScopeRoots();
    }
}