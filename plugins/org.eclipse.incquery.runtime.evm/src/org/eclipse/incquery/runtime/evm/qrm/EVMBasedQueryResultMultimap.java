/*******************************************************************************
 * Copyright (c) 2010-2012, Abel Hegedus, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Abel Hegedus - initial API and implementation
 *******************************************************************************/
package org.eclipse.incquery.runtime.evm.qrm;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.incquery.runtime.api.IMatchProcessor;
import org.eclipse.incquery.runtime.api.IMatcherFactory;
import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.runtime.api.IncQueryMatcher;
import org.eclipse.incquery.runtime.base.api.QueryResultMultimap;
import org.eclipse.incquery.runtime.evm.api.ActivationState;
import org.eclipse.incquery.runtime.evm.api.EventDrivenVM;
import org.eclipse.incquery.runtime.evm.api.ExecutionSchema;
import org.eclipse.incquery.runtime.evm.api.Job;
import org.eclipse.incquery.runtime.evm.specific.Schedulers;
import org.eclipse.incquery.runtime.evm.specific.job.StatelessJob;
import org.eclipse.incquery.runtime.evm.specific.lifecycle.DefaultActivationLifeCycle;
import org.eclipse.incquery.runtime.evm.specific.rule.SimpleMatcherRuleSpecification;

/**
 * This {@link QueryResultMultimap} implementation uses the EVM to provide a query-based multimap.
 * 
 * The contents of the multimap will be updated when the activations are fired in the EVM.
 * 
 * @author Abel Hegedus
 * 
 */
public abstract class EVMBasedQueryResultMultimap<Match extends IPatternMatch, KeyType, ValueType> extends
        QueryResultMultimap<KeyType, ValueType> {

    private final Set<Job> jobs;

    private final ExecutionSchema schema;

    /**
     * Creates a multimap on top of the given execution schema.
     * 
     * @param schema
     */
    protected EVMBasedQueryResultMultimap(final ExecutionSchema schema) {
        super(schema.getEventSource().getLogger());
        this.schema = schema;
        this.jobs = new HashSet<Job>();
        jobs.add(new StatelessJob<Match>(ActivationState.APPEARED, new IMatchProcessor<Match>() {
            @Override
            public void process(final Match match) {
                KeyType key = getKeyFromMatch(match);
                ValueType value = getValueFromMatch(match);
                internalPut(key, value);
            }
        }));

        jobs.add(new StatelessJob<Match>(ActivationState.DISAPPEARED, new IMatchProcessor<Match>() {
            @Override
            public void process(final Match match) {
                KeyType key = getKeyFromMatch(match);
                ValueType value = getValueFromMatch(match);
                internalRemove(key, value);
            }
        }));
    }

    /**
     * Creates a new multimap on the given engine. It will use an execution schema that
     * is scheduled with IQBase update callbacks.
     * 
     */
    protected EVMBasedQueryResultMultimap(final IncQueryEngine engine) {
        this(EventDrivenVM.createExecutionSchema(engine,
                Schedulers.getIQBaseSchedulerFactory(engine)));
    }

    /**
     * Adds the given query into the results of the multimap. 
     * 
     * @param factory
     */
    public <Matcher extends IncQueryMatcher<Match>> void addMatcherToMultimapResults(
            final IMatcherFactory<Matcher> factory) {
        schema.addRule(new SimpleMatcherRuleSpecification<Match, Matcher>(factory,
                DefaultActivationLifeCycle.DEFAULT_NO_UPDATE, jobs));
    }

    /**
     * Processes the given match and returns the key to be used in the multimap.
     * 
     * @param match
     * @return the computed key
     */
    protected abstract KeyType getKeyFromMatch(final Match match);

    /**
     * Processes the given match and returns the value to be used in the mulitmap.
     * 
     * @param match
     * @return the computed value
     */
    protected abstract ValueType getValueFromMatch(final Match match);

}
