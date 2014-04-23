/*******************************************************************************
 * Copyright (c) 2010-2012, Zoltan Ujhelyi, Abel Hegedus, Tamas Szabo, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi, Abel Hegedus, Tamas Szabo - initial API and implementation
 *******************************************************************************/

package org.eclipse.incquery.validation.runtime;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.runtime.evm.api.ExecutionSchema;
import org.eclipse.incquery.runtime.evm.api.RuleSpecification;
import org.eclipse.incquery.runtime.evm.api.Scheduler.ISchedulerFactory;
import org.eclipse.incquery.runtime.evm.specific.ExecutionSchemas;
import org.eclipse.incquery.runtime.evm.specific.Jobs;
import org.eclipse.incquery.runtime.evm.specific.Rules;
import org.eclipse.incquery.runtime.evm.specific.Schedulers;
import org.eclipse.incquery.runtime.evm.specific.event.IncQueryActivationStateEnum;
import org.eclipse.incquery.runtime.evm.specific.lifecycle.DefaultActivationLifeCycle;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.eclipse.incquery.runtime.util.IncQueryLoggingUtil;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

/**
 * The constraint adapter class is used to collect the constraints and deal with their maintenance for a given EMF
 * instance model. If the validation framework is initialized an instance of this class will be created which handles
 * the creation of the appropriate rules and their jobs.
 * 
 * @author Tamas Szabo
 */
public class ConstraintAdapter {

    private Map<IPatternMatch, IMarker> markerMap;
    private ExecutionSchema engine;
    private IResource resourceForEditor;

    public ConstraintAdapter(IEditorPart editorPart, Notifier notifier, Logger logger) {
        resourceForEditor = getIResourceForEditor(editorPart);
        this.markerMap = new HashMap<IPatternMatch, IMarker>();

        Set<RuleSpecification<?>> rules = Sets.newHashSet();

        for (Constraint<IPatternMatch> constraint : ValidationUtil.getConstraintsForEditorId(editorPart.getSite()
                .getId())) {

            rules.add(Rules.newMatcherRuleSpecification(constraint.getQuerySpecification(),
                    DefaultActivationLifeCycle.DEFAULT, ImmutableSet.of(
                            Jobs.newErrorLoggingJob(Jobs.newStatelessJob(IncQueryActivationStateEnum.APPEARED, new MarkerPlacerJob(this,constraint, logger))),
                            Jobs.newErrorLoggingJob(Jobs.newStatelessJob(IncQueryActivationStateEnum.DISAPPEARED, new MarkerEraserJob(this, logger))),
                            Jobs.newErrorLoggingJob(Jobs.newStatelessJob(IncQueryActivationStateEnum.UPDATED, new MarkerUpdaterJob(this,constraint, logger))))));
        }

        try {
            IncQueryEngine incQueryEngine = IncQueryEngine.on(notifier);
            ISchedulerFactory schedulerFactory = Schedulers.getIQEngineSchedulerFactory(incQueryEngine);
            this.engine = ExecutionSchemas.createIncQueryExecutionSchema(incQueryEngine, schedulerFactory, rules);
            this.engine.startUnscheduledExecution();
        } catch (IncQueryException e) {
            IncQueryLoggingUtil.getLogger(getClass()).error(
                    String.format("Exception occured when creating engine for validation: %s", e.getMessage()), e);
        }
    }

    private IResource getIResourceForEditor(IEditorPart editorPart) {
        // get resource for editor input (see org.eclipse.ui.ide.ResourceUtil.getResource)
        IEditorInput input = editorPart.getEditorInput();
        IResource resource = null;
        if(input != null) {
            Object o = input.getAdapter(IFile.class);
            if (o instanceof IResource) {
                resource = (IResource) o;
            }
        }
        return resource;
    }

    public void dispose() {
        for (IMarker marker : markerMap.values()) {
            try {
                marker.delete();
            } catch (CoreException e) {
                engine.getLogger().error(
                        String.format("Exception occured when removing a marker on dispose: %s", e.getMessage()), e);
            }
        }
        engine.dispose();
    }

    public IMarker getMarker(IPatternMatch match) {
        return this.markerMap.get(match);
    }

    public IMarker addMarker(IPatternMatch match, IMarker marker) {
        return this.markerMap.put(match, marker);
    }

    public IMarker removeMarker(IPatternMatch match) {
        return this.markerMap.remove(match);
    }
    
    protected IResource getResourceForEditor() {
        return resourceForEditor;
    }
}
