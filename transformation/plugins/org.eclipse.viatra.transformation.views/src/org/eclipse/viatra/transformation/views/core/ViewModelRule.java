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
package org.eclipse.viatra.transformation.views.core;

import org.apache.log4j.Logger;
import org.eclipse.viatra.query.runtime.api.GenericPatternMatch;
import org.eclipse.viatra.query.runtime.api.IPatternMatch;
import org.eclipse.viatra.query.runtime.api.IQuerySpecification;
import org.eclipse.viatra.query.runtime.matchers.psystem.queries.QueryInitializationException;
import org.eclipse.viatra.transformation.evm.api.ExecutionSchema;
import org.eclipse.viatra.transformation.evm.api.Job;
import org.eclipse.viatra.transformation.evm.api.RuleSpecification;
import org.eclipse.viatra.transformation.evm.api.event.EventFilter;
import org.eclipse.viatra.transformation.evm.specific.Jobs;
import org.eclipse.viatra.transformation.evm.specific.Rules;
import org.eclipse.viatra.transformation.evm.specific.event.IncQueryActivationStateEnum;
import org.eclipse.viatra.transformation.views.traceablilty.generic.AbstractQuerySpecificationDescriptor;
import org.eclipse.viatra.transformation.views.traceablilty.generic.GenericReferencedQuerySpecification;
import org.eclipse.viatra.transformation.views.traceablilty.generic.GenericTracedQuerySpecification;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;

/**
 * Common ancestor for defining rules for the view models. By default all the jobs are NopJobs.
 * 
 * @author Csaba Debreceni
 */
public abstract class ViewModelRule {

    private EventFilter<IPatternMatch> filter;
    private boolean isFilterInitialized = false;
    private AbstractQuerySpecificationDescriptor descriptor;
    
    protected Logger logger = Logger.getLogger(ViewModelRule.class);
    
    public ViewModelRule(AbstractQuerySpecificationDescriptor descriptor) {
        this.descriptor = descriptor;
    }

    // Getters for specifications
    public GenericTracedQuerySpecification getTracedSpecification() {
        return descriptor.getTracedSpecification();
    }

    public GenericReferencedQuerySpecification getReferencedSpecification() {
        return getTracedSpecification().getReferencedSpecification();
    }

    public IQuerySpecification<?> getBaseSpecification() {
        return getReferencedSpecification().getBaseSpecification();
    }
    // - Getters for specifications
    

    // Jobs
    protected Job<GenericPatternMatch> getAppearedJob() {
        return Jobs.newNopJob(IncQueryActivationStateEnum.APPEARED);
    }

    protected Job<GenericPatternMatch> getDisappearedJob() {
        return Jobs.newNopJob(IncQueryActivationStateEnum.DISAPPEARED);
    }

    protected Job<GenericPatternMatch> getUpdatedJob() {
        return Jobs.newNopJob(IncQueryActivationStateEnum.UPDATED);
    }
    // - Jobs
    
    
    public final void createRuleSpecification(ExecutionSchema executionSchema) {
        if (!isFilterInitialized) {
            filter = prepareFilterSuper();
        }
        registerReferencedSpecification(executionSchema);
        registerTraceabilitySpecification(executionSchema);
    }

    private void registerReferencedSpecification(ExecutionSchema executionSchema) {
        Builder<Job<GenericPatternMatch>> builder = ImmutableSet.builder();
        {
            builder.add(getAppearedJob());
            builder.add(Jobs.<GenericPatternMatch> newNopJob(IncQueryActivationStateEnum.DISAPPEARED));
            builder.add(Jobs.<GenericPatternMatch> newNopJob(IncQueryActivationStateEnum.UPDATED));
        }

        RuleSpecification<GenericPatternMatch> ruleSpecification = Rules.newMatcherRuleSpecification(
                getReferencedSpecification(), builder.build());
        if (isFiltered()) {
            executionSchema.addRule(ruleSpecification, filter);
        } else {
            executionSchema.addRule(ruleSpecification);
        }
    }

    private void registerTraceabilitySpecification(ExecutionSchema executionSchema) {
        Builder<Job<GenericPatternMatch>> builder = ImmutableSet.builder();
        {
            builder.add(Jobs.<GenericPatternMatch> newNopJob(IncQueryActivationStateEnum.APPEARED));
            builder.add(getDisappearedJob());
            builder.add(getUpdatedJob());
        }

        RuleSpecification<GenericPatternMatch> ruleSpecification = Rules.newMatcherRuleSpecification(
                getTracedSpecification(), builder.build());
        // if (isFiltered()) {
        // executionSchema.addRule(ruleSpecification, filter);
        // } else {
        executionSchema.addRule(ruleSpecification);
        // }
    }

    // public final GenericPatternMatch createFilteredMatchFromFilteredMatch(IPatternMatch match) {
    // int size = getSpecification().getTraceParameters().size();
    // List<Object> newMatchParams = Arrays.asList(match.toArray());
    // for (int i = 0; i < size; i++) {
    // newMatchParams.add(null);
    // }
    //
    // return getSpecification().newMatch(newMatchParams);
    //
    // }

    // Event filter
    private EventFilter<IPatternMatch> prepareFilterSuper() {
        isFilterInitialized = true;
        return prepareFilter();
    }
    
    protected abstract EventFilter<IPatternMatch> prepareFilter();
        
    public void setFilter(EventFilter<IPatternMatch> filter) {
        this.filter = filter;
    }

    public EventFilter<IPatternMatch> getFilter() {
        return filter;
    }

    public boolean isFiltered() {
        return filter != null;
    }
    // - Event filter

    public final void initialize(String traceabilityId) throws QueryInitializationException {
        descriptor.initialize(traceabilityId);
        filter = prepareFilter();
    }
}
