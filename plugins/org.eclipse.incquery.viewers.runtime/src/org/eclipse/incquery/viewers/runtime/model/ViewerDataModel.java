/*******************************************************************************
 * Copyright (c) 2010-2013, Csaba Debreceni, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Istvan Rath - initial API and implementation
 *   Zoltan Ujhelyi - initial API and implementation
 *   Csaba Debreceni - remove dependency from observable collections
 *******************************************************************************/
package org.eclipse.incquery.viewers.runtime.model;

import java.util.Collection;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.incquery.runtime.api.IQuerySpecification;
import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.runtime.emf.EMFScope;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.eclipse.incquery.runtime.util.IncQueryLoggingUtil;
import org.eclipse.incquery.viewmodel.core.ViewModelManager;

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

    private final String NOTATION_RESOURCE = "org.eclipse.incquery.viewers.notation.NotationResource";
    
    protected NotationModel model;
    protected IncQueryEngine engine;
    protected ResourceSet resourceSet;

    public ViewerDataModel(ResourceSet notifier) throws IncQueryException {
        this(IncQueryEngine.on(new EMFScope(notifier)));
    }

    public ViewerDataModel(IncQueryEngine engine) {
        if (!(engine.getScope() instanceof EMFScope)) {
            IncQueryLoggingUtil.getLogger(ViewModelManager.class).error(
                    "Only EMFScope is supported currently for IncQueryEngine");
            return;
        }
        if (!(((EMFScope) engine.getScope()).getScopeRoot() instanceof ResourceSet)) {
            IncQueryLoggingUtil.getLogger(ViewModelManager.class).error(
                    "Only ResourceSet is supported currently for EMFScope");
            return;
        }
        
        model = NotationFactory.eINSTANCE.createNotationModel();
        this.engine = engine;
        prepareBaseNotifier();
    }
    
    private void prepareBaseNotifier() {

        ResourceSet resourceSet = (ResourceSet)((EMFScope) engine.getScope()).getScopeRoot();
        Resource resource = null;

        for (Resource r : resourceSet.getResources()) {
            if (r.getURI().toString().equals(getNotationResourceId())) {
                resource = r;
                break;
            }
        }

        if (resource == null) {
            resource = resourceSet.createResource(URI.createURI(getNotationResourceId()));
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

    public IncQueryEngine getEngine() {
        return engine;
    }
    
    public void dispose() {
        EcoreUtil.delete(model);
    }

    public abstract Collection<IQuerySpecification<?>> getPatterns();

}