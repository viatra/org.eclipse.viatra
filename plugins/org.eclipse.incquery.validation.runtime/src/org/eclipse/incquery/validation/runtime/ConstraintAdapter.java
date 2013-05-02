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
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.incquery.runtime.api.IncQueryEngineManager;
import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.runtime.evm.api.ActivationState;
import org.eclipse.incquery.runtime.evm.api.RuleEngine;
import org.eclipse.incquery.runtime.evm.api.RuleSpecification;
import org.eclipse.incquery.runtime.evm.api.Scheduler.ISchedulerFactory;
import org.eclipse.incquery.runtime.evm.specific.ExecutionSchemas;
import org.eclipse.incquery.runtime.evm.specific.Jobs;
import org.eclipse.incquery.runtime.evm.specific.Rules;
import org.eclipse.incquery.runtime.evm.specific.Schedulers;
import org.eclipse.incquery.runtime.evm.specific.lifecycle.DefaultActivationLifeCycle;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.eclipse.ui.IEditorPart;

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
    private RuleEngine engine;

    public ConstraintAdapter(IEditorPart editorPart, Notifier notifier, Logger logger) {
        this.markerMap = new HashMap<IPatternMatch, IMarker>();

        Set<RuleSpecification> rules = Sets.newHashSet();

        for (Constraint<IPatternMatch> constraint : ValidationUtil.getConstraintsForEditorId(editorPart.getSite()
                .getId())) {

            rules.add(Rules.newSimpleMatcherRuleSpecification(constraint.getQuerySpecification(),
                    DefaultActivationLifeCycle.DEFAULT, Sets.newHashSet(
                            Jobs.newStatelessJob(ActivationState.APPEARED, new MarkerPlacerJob(this,constraint, logger)),
                            Jobs.newStatelessJob(ActivationState.DISAPPEARED, new MarkerEraserJob(this, logger)),
                            Jobs.newStatelessJob(ActivationState.UPDATED, new MarkerUpdaterJob(this,constraint, logger)))));
        }

        try {
            IncQueryEngine incQueryEngine = IncQueryEngineManager.getInstance().getIncQueryEngine(notifier);
            ISchedulerFactory schedulerFactory = Schedulers.getIQBaseSchedulerFactory(incQueryEngine);
            this.engine = ExecutionSchemas.createIncQueryExecutionSchema(incQueryEngine, schedulerFactory, rules);
        } catch (IncQueryException e) {
            IncQueryEngine.getDefaultLogger().error(
                    String.format("Exception occured when creating engine for validation: %s", e.getMessage()), e);
        }
    }

    public void dispose() {
        for (IMarker marker : markerMap.values()) {
            try {
                marker.delete();
            } catch (CoreException e) {
                engine.getEventSource().getLogger().error(
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
}
