/*******************************************************************************
 * Copyright (c) 2010-2014, Balint Lorand, Zoltan Ujhelyi, Abel Hegedus, Tamas Szabo, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi, Abel Hegedus, Tamas Szabo - original initial API and implementation
 *   Balint Lorand - revised API and implementation
 *******************************************************************************/

package org.eclipse.incquery.validation.core;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.incquery.runtime.api.IQuerySpecification;
import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.runtime.api.IncQueryMatcher;
import org.eclipse.incquery.runtime.emf.EMFScope;
import org.eclipse.incquery.runtime.evm.api.ExecutionSchema;
import org.eclipse.incquery.runtime.evm.api.Job;
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
import org.eclipse.incquery.validation.core.api.IConstraint;
import org.eclipse.incquery.validation.core.api.IConstraintSpecification;
import org.eclipse.incquery.validation.core.api.IValidationEngine;
import org.eclipse.incquery.validation.core.listeners.ValidationEngineListener;

import com.google.common.collect.ImmutableSet;

public class ValidationEngine implements IValidationEngine {

    private Logger logger;

    private IncQueryEngine incQueryEngine;

    protected IncQueryEngine getIncQueryEngine() {
        return incQueryEngine;
    }

    private ExecutionSchema executionSchema;

    protected ExecutionSchema getExecutionSchema() {
        return executionSchema;
    }

    public ValidationEngine(Notifier notifier, Logger logger) {

        this.logger = logger;
        this.constraintMap = new HashMap<IConstraintSpecification, Constraint>();
        this.listeners = new HashSet<ValidationEngineListener>();

        try {
            this.incQueryEngine = IncQueryEngine.on(new EMFScope(notifier));
            ISchedulerFactory schedulerFactory = Schedulers.getIQEngineSchedulerFactory(incQueryEngine);
            this.executionSchema = ExecutionSchemas.createIncQueryExecutionSchema(incQueryEngine, schedulerFactory);
        } catch (IncQueryException e) {
            IncQueryLoggingUtil.getDefaultLogger().error(
                    String.format("Exception occured when creating engine for validation: %s", e.getMessage()), e);
        }
    }

    @Override
    public void initialize() {
        executionSchema.startUnscheduledExecution();
    }

    @Override
    public void dispose() {
        executionSchema.dispose();
        constraintMap.clear();
        listeners.clear();
    }

    private Map<IConstraintSpecification, Constraint> constraintMap;

    @SuppressWarnings("unchecked")
    @Override
    public Set<IConstraint> getConstraints() {
        return (Set<IConstraint>) (Set<?>) ImmutableSet.copyOf(constraintMap.values());
    }

    @Override
    public IConstraint addConstraintSpecification(IConstraintSpecification constraintSpecification) {
        Constraint constraint = new Constraint(constraintSpecification, this, logger);
        if (constraintMap.put(constraintSpecification, constraint) == null) {
            notifyListenersConstraintRegistered(constraint);
        }
        return constraint;
    }

    @Override
    public IConstraint removeConstraintSpecification(IConstraintSpecification constraintSpecification) {
        Constraint constraint = constraintMap.get(constraintSpecification);
        removeRuleSpecificationFromExecutionSchema(constraint);
        if (constraintMap.remove(constraintSpecification) != null) {
            notifyListenersConstraintDeregistered(constraint);
        }
        return constraint;
    }

    @SuppressWarnings("unchecked")
    protected boolean addRuleSpecificationToExecutionSchema(Constraint constraint) {
        Set<Job<IPatternMatch>> jobs = ImmutableSet.of(Jobs.newErrorLoggingJob(Jobs.newStatelessJob(
                IncQueryActivationStateEnum.APPEARED, new MatchAppearanceJob(constraint, logger))), Jobs
                .newErrorLoggingJob(Jobs.newStatelessJob(IncQueryActivationStateEnum.DISAPPEARED,
                        new MatchDisappearanceJob(constraint, logger))), Jobs.newErrorLoggingJob(Jobs.newStatelessJob(
                IncQueryActivationStateEnum.UPDATED, new MatchUpdateJob(constraint, logger))));
        IQuerySpecification<? extends IncQueryMatcher<IPatternMatch>> querySpecification = (IQuerySpecification<? extends IncQueryMatcher<IPatternMatch>>) constraint
                .getSpecification().getQuerySpecification();
        RuleSpecification<IPatternMatch> rule = Rules.newMatcherRuleSpecification(querySpecification,
                DefaultActivationLifeCycle.DEFAULT, jobs);
        constraint.setRuleSpecification(rule);
        boolean added = executionSchema.addRule(rule);
        executionSchema.startUnscheduledExecution();
        return added;
    }

    protected boolean removeRuleSpecificationFromExecutionSchema(Constraint constraint) {
        RuleSpecification<IPatternMatch> ruleSpecification = constraint.getRuleSpecification();
        if (ruleSpecification != null) {
            constraint.setRuleSpecification(null);
            return executionSchema.removeRule(ruleSpecification);
        }
        return false;
    }

    private Set<ValidationEngineListener> listeners;

    @Override
    public Set<ValidationEngineListener> getListeners() {
        return ImmutableSet.copyOf(listeners);
    }

    @Override
    public boolean addListener(ValidationEngineListener listener) {
        return listeners.add(listener);
    }

    @Override
    public boolean removeListener(ValidationEngineListener listener) {
        return listeners.remove(listener);
    }

    protected void notifyListenersConstraintRegistered(Constraint constraint) {
        for (ValidationEngineListener listener : listeners) {
            listener.constraintRegistered(constraint);
        }
    }

    protected void notifyListenersConstraintDeregistered(Constraint constraint) {
        for (ValidationEngineListener listener : listeners) {
            listener.constraintDeregistered(constraint);
        }
    }

}
