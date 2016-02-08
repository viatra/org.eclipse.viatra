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
package org.eclipse.viatra.transformation.runtime.emf.changemonitor;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.viatra.query.runtime.api.IMatchProcessor;
import org.eclipse.viatra.query.runtime.api.IPatternMatch;
import org.eclipse.viatra.query.runtime.api.IQuerySpecification;
import org.eclipse.viatra.query.runtime.api.IncQueryEngine;
import org.eclipse.viatra.query.runtime.api.IncQueryMatcher;
import org.eclipse.viatra.query.runtime.exception.IncQueryException;
import org.eclipse.viatra.transformation.evm.api.ExecutionSchema;
import org.eclipse.viatra.transformation.evm.api.Job;
import org.eclipse.viatra.transformation.evm.api.RuleSpecification;
import org.eclipse.viatra.transformation.evm.api.event.ActivationState;
import org.eclipse.viatra.transformation.evm.specific.ExecutionSchemas;
import org.eclipse.viatra.transformation.evm.specific.Jobs;
import org.eclipse.viatra.transformation.evm.specific.Lifecycles;
import org.eclipse.viatra.transformation.evm.specific.Rules;
import org.eclipse.viatra.transformation.evm.specific.Schedulers;
import org.eclipse.viatra.transformation.evm.specific.event.IncQueryActivationStateEnum;
import org.eclipse.viatra.transformation.evm.specific.job.EnableJob;
import org.eclipse.viatra.transformation.evm.specific.job.StatelessJob;
import org.eclipse.viatra.transformation.evm.specific.scheduler.UpdateCompleteBasedScheduler.UpdateCompleteBasedSchedulerFactory;

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
    private Multimap<IQuerySpecification<? extends IncQueryMatcher<IPatternMatch>>, IPatternMatch> appearBetweenCheckpoints;
    private Multimap<IQuerySpecification<? extends IncQueryMatcher<IPatternMatch>>, IPatternMatch> updateBetweenCheckpoints;
    private Multimap<IQuerySpecification<? extends IncQueryMatcher<IPatternMatch>>, IPatternMatch> disappearBetweenCheckpoints;
    private Multimap<IQuerySpecification<? extends IncQueryMatcher<IPatternMatch>>, IPatternMatch> appearAccumulator;
    private Multimap<IQuerySpecification<? extends IncQueryMatcher<IPatternMatch>>, IPatternMatch> updateAccumulator;
    private Multimap<IQuerySpecification<? extends IncQueryMatcher<IPatternMatch>>, IPatternMatch> disappearAccumulator;
    private Set<RuleSpecification<IPatternMatch>> rules;
    private Map<IQuerySpecification<?>, RuleSpecification<IPatternMatch>> specs;
    private Set<Job<?>> allJobs;

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

        return new ChangeDelta(appearBetweenCheckpoints, updateBetweenCheckpoints, disappearBetweenCheckpoints);
    }

    /**
     * Returns the changes in the model since the last checkpoint. The accumulated data is not erased.
     * 
     */
    @Override
    public ChangeDelta getDeltaSinceLastCheckpoint() {
        return new ChangeDelta(appearAccumulator, updateAccumulator, disappearAccumulator);
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
     * Disposes the Change monitor's execution schema
     */
    public void dispose(){
        executionSchema.dispose();
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
    protected void registerUpdate(IPatternMatch match) {
        IQuerySpecification<? extends IncQueryMatcher<? extends IPatternMatch>> specification = match.specification();
        Collection<IPatternMatch> updateMatches = updateAccumulator
                .get((IQuerySpecification<? extends IncQueryMatcher<IPatternMatch>>) specification);        	
        updateMatches.add(match);
        
    }

    /**
     * Extracts appeared elements from the given match
     * 
     * @param match
     */
    protected void registerAppear(IPatternMatch match) {
        IQuerySpecification<? extends IncQueryMatcher<? extends IPatternMatch>> specification = match.specification();
        Collection<IPatternMatch> appearMatches = appearAccumulator
                .get((IQuerySpecification<? extends IncQueryMatcher<IPatternMatch>>) specification);
        appearMatches.add(match);
    }

    /**
     * Extracts disappeared elements from the given match
     * 
     * @param match
     */
    protected void registerDisappear(IPatternMatch match) {
        IQuerySpecification<? extends IncQueryMatcher<? extends IPatternMatch>> specification = match.specification();
        
        Collection<IPatternMatch> appearMatches = appearAccumulator
                .get((IQuerySpecification<? extends IncQueryMatcher<IPatternMatch>>) specification);
        Collection<IPatternMatch> updateMatches = updateAccumulator
                .get((IQuerySpecification<? extends IncQueryMatcher<IPatternMatch>>) specification);
        Collection<IPatternMatch> disappearMatches = disappearAccumulator
                .get((IQuerySpecification<? extends IncQueryMatcher<IPatternMatch>>) specification);
        
        if (updateMatches.contains(match))
            updateMatches.remove(match);
        
        if (appearMatches.contains(match)){
        	appearMatches.remove(match);
        }else{
        	disappearMatches.add(match);
        }
        
            
        
        
    }
}
