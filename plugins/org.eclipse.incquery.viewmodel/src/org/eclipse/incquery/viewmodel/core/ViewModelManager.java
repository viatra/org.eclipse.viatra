/*******************************************************************************
 * Copyright (c) 2010-2015, Csaba Debreceni, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Csaba Debreceni - initial API and implementation
 *******************************************************************************/

package org.eclipse.incquery.viewmodel.core;

import java.util.Collection;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.runtime.base.api.NavigationHelper;
import org.eclipse.incquery.runtime.base.exception.IncQueryBaseException;
import org.eclipse.incquery.runtime.emf.EMFScope;
import org.eclipse.incquery.runtime.evm.api.ExecutionSchema;
import org.eclipse.incquery.runtime.evm.specific.ExecutionSchemas;
import org.eclipse.incquery.runtime.evm.specific.Schedulers;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.eclipse.incquery.runtime.matchers.psystem.queries.QueryInitializationException;
import org.eclipse.incquery.runtime.util.IncQueryLoggingUtil;
import org.eclipse.incquery.viewmodel.traceability.Traceability;
import org.eclipse.incquery.viewmodel.traceability.TraceabilityFactory;

/**
 * This class is responsible for initializing an execution schema and jobs to be executed. It also prepares the
 * {@link ResourceSet}.
 * 
 * @author Csaba Debreceni
 *
 */
public class ViewModelManager {

    private static final String TRACEBILITY_RESOURCE = "org.eclipse.incquery.viewmodel.traceability.TraceabilityResource";

    private IncQueryEngine engine;
    private ExecutionSchema executionSchema;
    private Collection<ViewModelRule> rules;

    private Traceability traceability = TraceabilityFactory.eINSTANCE.createTraceability();
    private String traceabilityId = UUID.randomUUID().toString();

    /**
     * Initialize the manager.
     * 
     * @throws IncQueryException
     * @throws QueryInitializationException 
     * @throws IncQueryBaseException 
     */
    public void initialize() throws IncQueryException, QueryInitializationException, IncQueryBaseException {

        prepareBaseNotifier();
        traceability.setId(traceabilityId);

        executionSchema = ExecutionSchemas.createIncQueryExecutionSchema(engine,
                Schedulers.getIQEngineSchedulerFactory(engine));
        
        for (ViewModelRule rule : rules) {
            rule.initialize(traceabilityId);
            rule.getTracedSpecification().getMatcher(engine);
            rule.getReferencedSpecification().getMatcher(engine);
            rule.getBaseSpecification().getMatcher(engine);
            rule.createRuleSpecification(executionSchema);
        }

        Logger.getLogger(getClass()).info("View model manager initialized");
        executionSchema.startUnscheduledExecution();
        Logger.getLogger(getClass()).info("Unscheduled execution started");
    }

    /**
     * Prepare the base notifier. Sets the ResourceSet and adds the traceability Resource.
     * @throws IncQueryBaseException 
     * @throws IncQueryException 
     */
    private void prepareBaseNotifier() throws IncQueryException, IncQueryBaseException {

        Collection<? extends Notifier> notifiers = getNotifiers();
        Resource resource = null;

        for(Notifier notifier : notifiers) {
        	if(notifier instanceof Resource) {
        		Resource r = (Resource) notifier;
        		if(checkTraceabilityResource(r)) {
        			resource = r;
        			break;
        		}
        	}
        	if(notifier instanceof ResourceSet) {
        		ResourceSet resourceSet = (ResourceSet) notifier;
        		for (Resource r : resourceSet.getResources()) {
        			if(checkTraceabilityResource(r)) {
            			resource = r;
            			break;
        			}
    	        }
        	}
	        
        }
        if (resource == null) {
            resource = addTraceabilityResource();
        }
        
        resource.getContents().add(traceability);
    }

    private boolean checkTraceabilityResource(Resource r) {
    	if (r.getURI().toString().equals(getTraceabilityResourceId()))
    		return true;
    	return false;
    }
    
    public IncQueryEngine getEngine() {
        return engine;
    }

    private Resource addTraceabilityResource() throws IncQueryException, IncQueryBaseException {
    	ResourceSet resourceSet = new ResourceSetImpl();
    	Resource resource = resourceSet.createResource(URI.createURI(getTraceabilityResourceId()));
    	NavigationHelper helper = EMFScope.extractUnderlyingEMFIndex(engine);
    	helper.addRoot(resourceSet);
    	
    	return resource;
    }
    
    public void setEngine(IncQueryEngine engine) {
        if (!(engine.getScope() instanceof EMFScope)) {
            IncQueryLoggingUtil.getLogger(ViewModelManager.class).error(
                    "Only EMFScope is supported currently for IncQueryEngine");
            return;
        }
        this.engine = engine;
    }

    private Collection<? extends Notifier> getNotifiers() {
        return ((EMFScope) engine.getScope()).getScopeRoots();
    }

    public void setRules(Collection<ViewModelRule> rules) {
        this.rules = rules;
    }

    public Traceability getTraceability() {
        return traceability;
    }

    public void dispose() {
        EcoreUtil.delete(traceability);
        executionSchema.dispose();
    }
    
    private String getTraceabilityResourceId() {
        return TRACEBILITY_RESOURCE;
    }
}
