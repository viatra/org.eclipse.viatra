/*******************************************************************************
 * Copyright (c) 2004-2015, Marton Bur, Peter Lunk, Zoltan Ujhelyi and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Marton Bur, Peter Lunk - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.emf.runtime.changemonitor;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.incquery.runtime.api.IMatchProcessor;
import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.incquery.runtime.api.IQuerySpecification;
import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.runtime.api.IncQueryMatcher;
import org.eclipse.incquery.runtime.evm.api.ExecutionSchema;
import org.eclipse.incquery.runtime.evm.api.Job;
import org.eclipse.incquery.runtime.evm.api.RuleSpecification;
import org.eclipse.incquery.runtime.evm.api.event.ActivationState;
import org.eclipse.incquery.runtime.evm.specific.ExecutionSchemas;
import org.eclipse.incquery.runtime.evm.specific.Jobs;
import org.eclipse.incquery.runtime.evm.specific.Lifecycles;
import org.eclipse.incquery.runtime.evm.specific.Rules;
import org.eclipse.incquery.runtime.evm.specific.Schedulers;
import org.eclipse.incquery.runtime.evm.specific.event.IncQueryActivationStateEnum;
import org.eclipse.incquery.runtime.evm.specific.job.EnableJob;
import org.eclipse.incquery.runtime.evm.specific.job.StatelessJob;
import org.eclipse.incquery.runtime.evm.specific.scheduler.UpdateCompleteBasedScheduler.UpdateCompleteBasedSchedulerFactory;
import org.eclipse.incquery.runtime.exception.IncQueryException;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

/**
 * Class responsible for monitoring changes in a specified model. It uses IncQuery QuerySpecification objects or EMV
 * Rules defined by the user to achieve this.
 * 
 * Based on the instance model to be monitored, an IncQuery Engine should be initialized and handed over to this class.
 * An ExecutionSchema is initialized based on the IncQuery Engine, then the rules defined by the user are registered in
 * it.
 * 
 * By default the monitor accumulates the changes of the defined QuerySpecifications, this behavior, however can be
 * overridden via inheritance.
 * 
 * @author Lunk Péter
 *
 */
@SuppressWarnings("unchecked")
public class ChangeMonitor extends IChangeMonitor {
    private Multimap<IQuerySpecification<? extends IncQueryMatcher<IPatternMatch>>, EObject> appearBetweenCheckpoints;
    private Multimap<IQuerySpecification<? extends IncQueryMatcher<IPatternMatch>>, EObject> updateBetweenCheckpoints;
    private Multimap<IQuerySpecification<? extends IncQueryMatcher<IPatternMatch>>, EObject> disappearBetweenCheckpoints;
    private Multimap<IQuerySpecification<? extends IncQueryMatcher<IPatternMatch>>, EObject> appearAccumulator;
    private Multimap<IQuerySpecification<? extends IncQueryMatcher<IPatternMatch>>, EObject> updateAccumulator;
    private Multimap<IQuerySpecification<? extends IncQueryMatcher<IPatternMatch>>, EObject> disappearAccumulator;
    private Set<RuleSpecification<IPatternMatch>> rules;
    private Map<IQuerySpecification<?>, RuleSpecification<IPatternMatch>> specs;
    private Set<Job<?>> allJobs;

    private boolean deploymentBetweenCheckpointsChanged;
    private boolean changed;
    private boolean started;
    private ExecutionSchema executionSchema;

    /**
     * Constructor that creates a new ChangeMonitor instance based on the specified IncQuery engine. Note that to
     * monitor changes of a specific model instance, an IncQuery should be initialized on said model instance.
     * 
     * @param engine
     *            The IncQueryEngine the monitor is based on.
     */
    public ChangeMonitor(IncQueryEngine engine) {
        super(engine);
        this.appearBetweenCheckpoints = ArrayListMultimap.create();
        this.updateBetweenCheckpoints = ArrayListMultimap.create();
        this.disappearBetweenCheckpoints = ArrayListMultimap.create();
        this.appearAccumulator = ArrayListMultimap.create();
        this.updateAccumulator = ArrayListMultimap.create();
        this.disappearAccumulator = ArrayListMultimap.create();

        allJobs = new HashSet<Job<?>>();
        rules = new HashSet<RuleSpecification<IPatternMatch>>();
        specs = new HashMap<IQuerySpecification<?>, RuleSpecification<IPatternMatch>>();
        deploymentBetweenCheckpointsChanged = false;
        changed = false;
        started = false;

        UpdateCompleteBasedSchedulerFactory schedulerFactory = Schedulers.getIQEngineSchedulerFactory(engine);
        executionSchema = ExecutionSchemas.createIncQueryExecutionSchema(engine, schedulerFactory);

    }

    /**
     * Public method used for adding new rules to the monitor. This method can be used both before and after monitoring
     * has been started.
     * 
     * @param rule
     *            The rule to be added to the monitor
     */
    public void addRule(RuleSpecification<IPatternMatch> rule) {
        rules.add(rule);
        Multimap<ActivationState, Job<IPatternMatch>> jobs = rule.getJobs();
        if (started) {
            executionSchema.addRule(rule);
        }
        for (ActivationState state : jobs.keySet()) {
            for (Job<?> job : jobs.get(state)) {
                if (started) {
                    EnableJob<?> enableJob = (EnableJob<?>) job;
                    enableJob.setEnabled(true);
                } else {
                    allJobs.add(job);
                }
            }
        }
    }

    /**
     * Public method used for adding new rules to the monitor. Based on the QuerySpecification provided here, a new Rule
     * will be added to the monitor. This method can be used both before and after monitoring has been started.
     * 
     * @param spec
     *            QuerySpecification to be added to the Monitor
     */
    public void addRule(IQuerySpecification<?> spec) {
        RuleSpecification<IPatternMatch> rule = Rules.newMatcherRuleSpecification(
                (IQuerySpecification<? extends IncQueryMatcher<IPatternMatch>>) spec,
                Lifecycles.getDefault(true, true), createDefaultProcessorJobs());
        specs.put(spec, rule);
        addRule(rule);
    }

    /**
     * Public method used for removing rules from the Monitor. This method can be used both before and after monitoring
     * has been started.
     * 
     * @param rule
     *            Rule to be removed
     */
    public void removeRule(RuleSpecification<IPatternMatch> rule) {
        rules.remove(rule);
        executionSchema.removeRule(rule);
    }

    /**
     * Public method used for removing rules from the Monitor. This method can be used both before and after monitoring
     * has been started.
     * 
     * @param spec
     *            The rules based on this QuerySpecification will be removed
     */
    public void removeRule(IQuerySpecification<?> spec) {
        RuleSpecification<IPatternMatch> ruleSpecification = specs.get(spec);
        rules.remove(ruleSpecification);
        specs.remove(spec);
        executionSchema.removeRule(ruleSpecification);
    }

    /**
     * Creates a new checkpoint and returns the changes in the model so far. The ChangeDelta object returned contains
     * objects that have APPEARED, DISAPPEARED or have been UPDATED.
     * 
     * If this method is used, the history accumulated is erased, as a new checkpoint is created.
     * 
     */
    @Override
    public ChangeDelta createCheckpoint() {
        appearBetweenCheckpoints = appearAccumulator;
        updateBetweenCheckpoints = updateAccumulator;
        disappearBetweenCheckpoints = disappearAccumulator;
        appearAccumulator = ArrayListMultimap.create();
        updateAccumulator = ArrayListMultimap.create();
        disappearAccumulator = ArrayListMultimap.create();
        deploymentBetweenCheckpointsChanged = changed;

        return new ChangeDelta(appearBetweenCheckpoints, updateBetweenCheckpoints, disappearBetweenCheckpoints,
                deploymentBetweenCheckpointsChanged);
    }

    /**
     * Returns the changes in the model since the last checkpoint. The accumulated data is not erased.
     * 
     */
    @Override
    public ChangeDelta getDeltaSinceLastCheckpoint() {
        return new ChangeDelta(appearAccumulator, updateAccumulator, disappearAccumulator, changed);
    }

    /**
     * Adds the defined rules to the ExecutionSchema and enables them. Call this method after the rules have been added,
     * and the model instance to be monitored is initialized.
     * 
     */
    @Override
    public void startMonitoring() throws IncQueryException {

        for (RuleSpecification<IPatternMatch> rule : rules) {
            executionSchema.addRule(rule);
        }

        executionSchema.startUnscheduledExecution();
        // Enable the jobs to listen to changes
        for (Job<?> job : allJobs) {
            EnableJob<?> enableJob = (EnableJob<?>) job;
            enableJob.setEnabled(true);
        }
        started = true;
    }

    /**
     * Creates the default EVM Jobs which are executed as a new match appears, disappears or is updated. Can be
     * overridden to specify domain specific functionality
     * 
     * @return
     */
    protected Set<Job<IPatternMatch>> createDefaultProcessorJobs() {
        // Define default MatchProcessors
        IMatchProcessor<IPatternMatch> appearProcessor = new IMatchProcessor<IPatternMatch>() {
            @Override
            public void process(IPatternMatch match) {

                registerAppear(match);
            }
        };
        IMatchProcessor<IPatternMatch> disappearProcessor = new IMatchProcessor<IPatternMatch>() {
            @Override
            public void process(IPatternMatch match) {

                registerDisappear(match);

            }
        };
        IMatchProcessor<IPatternMatch> updateProcessor = new IMatchProcessor<IPatternMatch>() {
            @Override
            public void process(IPatternMatch match) {
                registerUpdate(match);
            }
        };

        // Create Jobs
        Set<Job<IPatternMatch>> jobs = Sets.newHashSet();
        Job<IPatternMatch> appear = new StatelessJob<IPatternMatch>(IncQueryActivationStateEnum.APPEARED,
                appearProcessor);
        Job<IPatternMatch> disappear = new StatelessJob<IPatternMatch>(IncQueryActivationStateEnum.DISAPPEARED,
                disappearProcessor);
        Job<IPatternMatch> update = new StatelessJob<IPatternMatch>(IncQueryActivationStateEnum.UPDATED,
                updateProcessor);

        jobs.add(Jobs.newEnableJob(appear));
        jobs.add(Jobs.newEnableJob(disappear));
        jobs.add(Jobs.newEnableJob(update));
        allJobs.addAll(jobs);
        return jobs;
    }

    /**
     * Extracts updated elements from the given match
     * 
     * @param match
     */
    private void registerUpdate(IPatternMatch match) {
        IQuerySpecification<? extends IncQueryMatcher<? extends IPatternMatch>> specification = match.specification();
        Set<EObject> objects = new HashSet<EObject>();
        int i = 0;
        while (match.get(i) != null) {
            objects.add((EObject) match.get(i));
            i++;
        }
        Collection<EObject> updateElements = updateAccumulator
                .get((IQuerySpecification<? extends IncQueryMatcher<IPatternMatch>>) specification);
        updateElements.addAll(objects);
    }

    /**
     * Extracts appeared elements from the given match
     * 
     * @param match
     */
    private void registerAppear(IPatternMatch match) {
        IQuerySpecification<? extends IncQueryMatcher<? extends IPatternMatch>> specification = match.specification();
        Set<EObject> objects = new HashSet<EObject>();
        int i = 0;
        while (match.get(i) != null) {
            objects.add((EObject) match.get(i));
            i++;
        }
        Collection<EObject> appearElements = appearAccumulator
                .get((IQuerySpecification<? extends IncQueryMatcher<IPatternMatch>>) specification);
        appearElements.addAll(objects);
    }

    /**
     * Extracts disappeared elements from the given match
     * 
     * @param match
     */
    private void registerDisappear(IPatternMatch match) {
        IQuerySpecification<? extends IncQueryMatcher<? extends IPatternMatch>> specification = match.specification();
        Set<EObject> objects = new HashSet<EObject>();
        int i = 0;
        while (match.get(i) != null) {
            objects.add((EObject) match.get(i));
            i++;
        }
        Collection<EObject> appearElements = appearAccumulator
                .get((IQuerySpecification<? extends IncQueryMatcher<IPatternMatch>>) specification);
        Collection<EObject> updateElements = updateAccumulator
                .get((IQuerySpecification<? extends IncQueryMatcher<IPatternMatch>>) specification);
        Collection<EObject> disappearElements = disappearAccumulator
                .get((IQuerySpecification<? extends IncQueryMatcher<IPatternMatch>>) specification);
        for (EObject eObject : objects) {
            if (updateElements.contains(eObject))
                updateElements.remove(eObject);
            if (appearElements.contains(eObject))
                appearElements.remove(eObject);
        }
        disappearElements.addAll(objects);
    }
}
