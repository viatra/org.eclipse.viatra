/*******************************************************************************
 * Copyright (c) 2010-2013, Zoltan Ujhelyi, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi - initial API and implementation
 *   Marton Bur - local search adapter capability
 *******************************************************************************/
package org.eclipse.viatra.query.runtime.localsearch.matcher;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;

import org.eclipse.viatra.query.runtime.localsearch.MatchingFrame;
import org.eclipse.viatra.query.runtime.localsearch.exceptions.LocalSearchException;
import org.eclipse.viatra.query.runtime.localsearch.plan.IPlanDescriptor;
import org.eclipse.viatra.query.runtime.localsearch.plan.SearchPlanExecutor;
import org.eclipse.viatra.query.runtime.matchers.psystem.queries.PQuery;
import org.eclipse.viatra.query.runtime.matchers.tuple.ITuple;
import org.eclipse.viatra.query.runtime.matchers.tuple.Tuple;
import org.eclipse.viatra.query.runtime.matchers.tuple.TupleMask;
import org.eclipse.viatra.query.runtime.matchers.tuple.VolatileModifiableMaskedTuple;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.UnmodifiableIterator;

/**
 * @author Zoltan Ujhelyi
 * @noinstantiate This class is not intended to be instantiated by clients.
 */
public class LocalSearchMatcher implements ILocalSearchAdaptable {

    private ImmutableList<SearchPlanExecutor> plan;
    private IPlanDescriptor planDescriptor;
    private List<ILocalSearchAdapter> adapters = Lists.newLinkedList();

    public ImmutableList<SearchPlanExecutor> getPlan() {
        return plan;
    }
    
    @Override
    public List<ILocalSearchAdapter> getAdapters() {
        return Lists.newArrayList(adapters);
    }
    
    private abstract class PlanExecutionIterator extends UnmodifiableIterator<Tuple> {

        protected final UnmodifiableIterator<SearchPlanExecutor> planIterator;
        
        protected SearchPlanExecutor currentPlan;
        protected MatchingFrame frame;
        protected VolatileModifiableMaskedTuple parametersOfFrameView; 
        private boolean isNextMatchCalculated;
        
        public PlanExecutionIterator(final UnmodifiableIterator<SearchPlanExecutor> planIterator) {
            this.planIterator = planIterator;
            isNextMatchCalculated = false;
        }

        protected boolean selectNextPlan() {
            if(currentPlan !=null) {
                currentPlan.removeAdapters(adapters);
            }
            boolean validPlanSelected = false;
            
            SearchPlanExecutor nextPlan = null;
            
            while (!validPlanSelected && planIterator.hasNext()) {
                nextPlan = planIterator.next();
                nextPlan.addAdapters(adapters);
                nextPlan.resetPlan();
                
                validPlanSelected = initializeMatchingFrame(nextPlan);
            }
            
            if (validPlanSelected) {
                for (ILocalSearchAdapter adapter : adapters) {
                    adapter.planChanged(currentPlan, nextPlan);
                }
                currentPlan = nextPlan;
                return true;
            } else {
                currentPlan = null;
                return false;
            }
        }

        protected abstract boolean initializeMatchingFrame(SearchPlanExecutor nextPlan);

        @Override
        public boolean hasNext() {
            if (isNextMatchCalculated) {
                return true;
            }
            if (currentPlan == null) {
                return false;
            }
            try {
                boolean foundMatch = currentPlan.execute(frame);
                while ((!foundMatch) && planIterator.hasNext()) {
                    foundMatch = selectNextPlan() && currentPlan.execute(frame);
                }
                isNextMatchCalculated = foundMatch;
                return foundMatch;
            } catch (LocalSearchException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public Tuple next() {
            if (!hasNext()) {
                throw new NoSuchElementException("No more matches available.");
            }
            isNextMatchCalculated = false;
            return parametersOfFrameView.toImmutable();
        }

    }

    private class PlanExecutionIteratorWithArrayParameters extends PlanExecutionIterator {
        
        private final Object[] parameterValues;
        
        public PlanExecutionIteratorWithArrayParameters(UnmodifiableIterator<SearchPlanExecutor> planIterator, final Object[] parameterValues) {
            super(planIterator);
            this.parameterValues = parameterValues;
            selectNextPlan();
        }
        
        protected boolean initializeMatchingFrame(SearchPlanExecutor nextPlan) {
            frame = new MatchingFrame(nextPlan.getVariableMapping().size());
            parametersOfFrameView = new VolatileModifiableMaskedTuple(frame, nextPlan.getParameterMask());
            for (int i = 0; i < parameterValues.length; i++) {
                Object valueToSet = parameterValues[i];
                if (valueToSet != null) {
                    Object oldValue = parametersOfFrameView.get(i);
                    if (oldValue == null) {
                        parametersOfFrameView.set(i, valueToSet);
                    } else if (!Objects.equals(valueToSet, oldValue)) {
                        // Initial value setting resulted in contradictory values. This can happen because two parameter
                        // variables have been unified but the call provides different values for the parameters.
                        return false;
                    }
                    // If oldValue is not null but equal to newValue, the setting can be ignored
                }
            }
            
            return true;
        }
    }
    private class PlanExecutionIteratorWithTupleParameters extends PlanExecutionIterator {
        
        private final ITuple parameterValues;
        final private TupleMask parameterSeedMask;
        
        public PlanExecutionIteratorWithTupleParameters(UnmodifiableIterator<SearchPlanExecutor> planIterator, final TupleMask parameterSeedMask, final ITuple parameterValues) {
            super(planIterator);
            this.parameterSeedMask = parameterSeedMask;
            this.parameterValues = parameterValues;
            selectNextPlan();
        }
        
        protected boolean initializeMatchingFrame(SearchPlanExecutor nextPlan) {
            frame = new MatchingFrame(nextPlan.getVariableMapping().size());
            parametersOfFrameView = new VolatileModifiableMaskedTuple(frame, nextPlan.getParameterMask());
            for (int i = 0; i < parameterSeedMask.getSize(); i++) {
                int index = parameterSeedMask.indices[i];
                Object valueToSet = parameterValues.get(i);
                if (valueToSet != null) {
                    Object oldValue = parametersOfFrameView.get(index);
                    if (oldValue == null) {
                        parametersOfFrameView.set(index, valueToSet);
                    } else if (!Objects.equals(valueToSet, oldValue)) {
                        // Initial value setting resulted in contradictory values. This can happen because two parameter
                        // variables have been unified but the call provides different values for the parameters.
                        return false;
                    }
                    // If oldValue is not null but equal to newValue, the setting can be ignored
                }
            }
            
            return true;
        }
    }
    
    /**
     * If a descendant initializes a matcher using the default constructor, it is expected that it also calls the
     * {@link #setPlan(SearchPlanExecutor)} and {@link #setFramesize(int)} methods manually.
     * @since 1.5
     */
    protected LocalSearchMatcher(IPlanDescriptor query) {
        Preconditions.checkArgument(query != null, "Cannot initialize matcher with null query.");
        this.planDescriptor = query;
    }

    /**
     * @since 1.7
     */
    public LocalSearchMatcher(IPlanDescriptor planDescriptor, SearchPlanExecutor plan) {
        this(planDescriptor, ImmutableList.of(plan));
    }
    
    /**
     * @since 1.7
     */
    public LocalSearchMatcher(IPlanDescriptor planDescriptor, SearchPlanExecutor[] plan) {
        this(planDescriptor, ImmutableList.copyOf(plan));
    }
    
    /**
     * @since 1.7
     */
    public LocalSearchMatcher(IPlanDescriptor planDescriptor, Collection<SearchPlanExecutor> plan) {
        this(planDescriptor, ImmutableList.copyOf(plan));
    }
    
    /**
     * @since 1.7
     */
    protected LocalSearchMatcher(IPlanDescriptor planDescriptor, ImmutableList<SearchPlanExecutor> plan) {
        this(planDescriptor);
        this.plan = plan;
        this.adapters = Lists.newLinkedList(adapters);
    }
    
    @Override
    public void addAdapter(ILocalSearchAdapter adapter) {
        addAdapters(Lists.newArrayList(adapter));
    }

    @Override
    public void removeAdapter(ILocalSearchAdapter adapter) {
        addAdapters(Lists.newArrayList(adapter));
    }
    
    @Override
    public void addAdapters(List<ILocalSearchAdapter> adapters) {
        this.adapters.addAll(adapters);
        for (ILocalSearchAdapter adapter : adapters) {
            adapter.adapterRegistered(this);
        }
    }

    @Override
    public void removeAdapters(List<ILocalSearchAdapter> adapters) {
        this.adapters.removeAll(adapters);
        for (ILocalSearchAdapter adapter : adapters) {
            adapter.adapterUnregistered(this);
        }
    }
    
    protected void setPlan(SearchPlanExecutor plan) {
        this.plan = ImmutableList.of(plan);
    }

    protected void setPlan(SearchPlanExecutor[] plan) {
        this.plan = ImmutableList.copyOf(plan);
    }

    public boolean hasMatch() {
        boolean hasMatch = hasMatch(new Object[0]);
        return hasMatch;
    }

    /**
     * @since 1.7
     */
    public boolean hasMatch(Object[] parameterValues) {
        matchingStarted();
        PlanExecutionIterator it = new PlanExecutionIteratorWithArrayParameters(plan.iterator(), parameterValues);
        boolean hasMatch = it.hasNext();
        matchingFinished();
        return hasMatch;
    }
    
    /**
     * @since 1.7
     */
    public boolean hasMatch(TupleMask parameterSeedMask, ITuple parameterValues) {
        matchingStarted();
        PlanExecutionIterator it = new PlanExecutionIteratorWithTupleParameters(plan.iterator(), parameterSeedMask, parameterValues);
        boolean hasMatch = it.hasNext();
        matchingFinished();
        return hasMatch;
    }

    public int countMatches() {
        int countMatches = countMatches(new Object[0]);
        return countMatches;
    }

    /**
     * @since 1.7
     */
    public int countMatches(Object[] parameterValues) {
        matchingStarted();
        PlanExecutionIterator it = new PlanExecutionIteratorWithArrayParameters(plan.iterator(), parameterValues);
        
        Set<Tuple> results = new HashSet<>();
        while (it.hasNext()) {
            results.add(it.next());
        }
        
        int result = results.size();
        
        matchingFinished();
        return result;
    }
    
    /**
     * @since 1.7
     */
    public int countMatches(TupleMask parameterSeedMask, ITuple parameterValues) {
        matchingStarted();
        PlanExecutionIterator it = new PlanExecutionIteratorWithTupleParameters(plan.iterator(), parameterSeedMask, parameterValues);
        
        Set<Tuple> results = new HashSet<>();
        while (it.hasNext()) {
            results.add(it.next());
        }
        
        int result = results.size();
        
        matchingFinished();
        return result;
    }
    
    public int getParameterCount() {
        return planDescriptor.getQuery().getParameters().size();
    }

    /**
     * @since 1.7
     */
    public Tuple getOneArbitraryMatch() {
        return getOneArbitraryMatch(new Object[0]);
    }

    /**
     * @since 1.7
     */
    public Tuple getOneArbitraryMatch(TupleMask parameterSeedMask, ITuple parameterValues) {
        matchingStarted();
        PlanExecutionIterator it = new PlanExecutionIteratorWithTupleParameters(plan.iterator(), parameterSeedMask, parameterValues);
        Tuple returnValue = null;
        if (it.hasNext()) {
            returnValue = it.next();
        }
        matchingFinished();
        return returnValue;
    }
    
    /**
     * @since 1.7
     */
    public Tuple getOneArbitraryMatch(Object[] parameterValues) {
        matchingStarted();
        PlanExecutionIterator it = new PlanExecutionIteratorWithArrayParameters(plan.iterator(), parameterValues);
        Tuple returnValue = null;
        if (it.hasNext()) {
            returnValue = it.next();
        }
        matchingFinished();
        return returnValue;
    }

    public Collection<Tuple> getAllMatches() {
        return getAllMatches(new Object[0]);
    }

    private void matchingStarted() {
        for (ILocalSearchAdapter adapter : adapters) {
            adapter.patternMatchingStarted(this);
        }
    }

    private void matchingFinished() {
        for (ILocalSearchAdapter adapter : adapters) {
            adapter.patternMatchingFinished(this);
        }		
    }

    /**
     * @since 1.7
     */
    public Collection<Tuple> getAllMatches(final Object[] parameterValues) {
        matchingStarted();
        PlanExecutionIterator it = new PlanExecutionIteratorWithArrayParameters(plan.iterator(), parameterValues);
        ImmutableSet<Tuple> results = ImmutableSet.copyOf(it);
        matchingFinished();
        return results;
    }
    
    /**
     * @since 1.7
     */
    public Iterable<Tuple> getAllMatches(TupleMask parameterSeedMask, final ITuple parameterValues) {
        matchingStarted();
        PlanExecutionIterator it = new PlanExecutionIteratorWithTupleParameters(plan.iterator(), parameterSeedMask, parameterValues);
        ImmutableSet<Tuple> results = ImmutableSet.copyOf(it);
        matchingFinished();
        return results;
    }
    
    /**
     * Returns the query specification this matcher used as source for the implementation
     * @return never null
     */
    public PQuery getQuerySpecification() {
        return planDescriptor.getQuery();
    }
    
    
    /**
     * @since 1.5
     */
    public IPlanDescriptor getPlanDescriptor() {
        return planDescriptor;
    }
}
