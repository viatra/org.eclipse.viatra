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
import java.util.List;
import java.util.NoSuchElementException;

import org.eclipse.viatra.query.runtime.localsearch.MatchingFrame;
import org.eclipse.viatra.query.runtime.localsearch.MatchingTable;
import org.eclipse.viatra.query.runtime.localsearch.exceptions.LocalSearchException;
import org.eclipse.viatra.query.runtime.localsearch.plan.IPlanDescriptor;
import org.eclipse.viatra.query.runtime.localsearch.plan.SearchPlanExecutor;
import org.eclipse.viatra.query.runtime.matchers.psystem.queries.PQuery;
import org.eclipse.viatra.query.runtime.matchers.tuple.Tuple;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.UnmodifiableIterator;

/**
 * @author Zoltan Ujhelyi
 * @noinstantiate This class is not intended to be instantiated by clients.
 */
public class LocalSearchMatcher implements ILocalSearchAdaptable {

    private ImmutableList<SearchPlanExecutor> plan;
    private int frameSize;
    private IPlanDescriptor planDescriptor;
    private List<ILocalSearchAdapter> adapters = Lists.newLinkedList();

    public ImmutableList<SearchPlanExecutor> getPlan() {
        return plan;
    }
    
    public int getFrameSize() {
        return frameSize;
    }
    
    @Override
    public List<ILocalSearchAdapter> getAdapters() {
        return Lists.newArrayList(adapters);
    }
    
    private class PlanExecutionIterator extends UnmodifiableIterator<MatchingFrame> {

        private UnmodifiableIterator<SearchPlanExecutor> iterator;
        private SearchPlanExecutor currentPlan;
        private MatchingFrame frame;
        private boolean frameReturned;
        
        public PlanExecutionIterator(final ImmutableList<SearchPlanExecutor> plan, MatchingFrame initialFrame) {
            this.frame = new MatchingFrame(initialFrame);
            Preconditions.checkArgument(!plan.isEmpty());
            iterator = plan.iterator();
            getNextPlan();
            frameReturned = true;
        }

        private void getNextPlan() {
            if(currentPlan !=null) {
                currentPlan.removeAdapters(adapters);
            }
            SearchPlanExecutor nextPlan = iterator.next();
            nextPlan.addAdapters(adapters);
            nextPlan.resetPlan();
            for (ILocalSearchAdapter adapter : adapters) {
                adapter.planChanged(currentPlan, nextPlan);
            }
            currentPlan = nextPlan;
        }

        @Override
        public boolean hasNext() {
            if (!frameReturned) {
                return true;
            }
            try {
                boolean foundMatch = currentPlan.execute(frame);
                while ((!foundMatch) && iterator.hasNext()) {
                    // here ends the previous plan
                    getNextPlan();
                    // here starts the new plan
                    foundMatch = currentPlan.execute(frame);
                }
                if (foundMatch) {
                    frameReturned = false;
                }
                return foundMatch;
            } catch (LocalSearchException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public MatchingFrame next() {
            if (!hasNext()) {
                throw new NoSuchElementException("No more matches available.");
            }
            frameReturned = true;
            return new MatchingFrame(frame);
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
     * @since 1.5
     */
    public LocalSearchMatcher(IPlanDescriptor planDescriptor, SearchPlanExecutor plan, int frameSize) {
        this(planDescriptor, ImmutableList.of(plan), frameSize);
    }
    
    /**
     * @since 1.5
     */
    public LocalSearchMatcher(IPlanDescriptor planDescriptor, SearchPlanExecutor[] plan, int frameSize) {
        this(planDescriptor, ImmutableList.copyOf(plan), frameSize);
    }
    
    /**
     * @since 1.5
     */
    public LocalSearchMatcher(IPlanDescriptor planDescriptor, Collection<SearchPlanExecutor> plan, int frameSize) {
        this(planDescriptor, ImmutableList.copyOf(plan), frameSize);
    }
    
    /**
     * @since 1.5
     */
    protected LocalSearchMatcher(IPlanDescriptor planDescriptor, ImmutableList<SearchPlanExecutor> plan, int frameSize) {
        this(planDescriptor);
        this.plan = plan;
        this.frameSize = frameSize;
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

    protected void setFramesize(int frameSize) {
        this.frameSize = frameSize;
    }

    public MatchingFrame editableMatchingFrame() {
        return new MatchingFrame(null, frameSize);
    }

    public boolean hasMatch() {
        boolean hasMatch = hasMatch(editableMatchingFrame());
        return hasMatch;
    }

    public boolean hasMatch(final MatchingFrame initialFrame) {
        matchingStarted();
        PlanExecutionIterator it = new PlanExecutionIterator(plan, initialFrame);
        boolean hasMatch = it.hasNext();
        matchingFinished();
        return hasMatch;
    }

    public int countMatches() {
        int countMatches = countMatches(editableMatchingFrame());
        return countMatches;
    }

    public int countMatches(MatchingFrame initialFrame) {
        matchingStarted();
        PlanExecutionIterator it = new PlanExecutionIterator(plan, initialFrame);
        
        MatchingTable results = new MatchingTable();
        while (it.hasNext()) {
            final MatchingFrame frame = it.next();
            results.put(frame.getKey(), frame);
        }
        
        int result = results.size();
        
        matchingFinished();
        return result;
    }
    
    public int getParameterCount() {
        return planDescriptor.getQuery().getParameters().size();
    }

    public MatchingFrame getOneArbitraryMatch() {
        MatchingFrame oneArbitraryMatch = getOneArbitraryMatch(editableMatchingFrame());
        return oneArbitraryMatch;
    }

    public MatchingFrame getOneArbitraryMatch(final MatchingFrame initialFrame) {
        matchingStarted();
        PlanExecutionIterator it = new PlanExecutionIterator(plan, initialFrame);
        MatchingFrame returnValue = null;
        if (it.hasNext()) {
            returnValue = it.next();
        }
        matchingFinished();
        return returnValue;
    }

    public Collection<Tuple> getAllMatches() {
        Collection<Tuple> allMatches = getAllMatches(editableMatchingFrame());
        return allMatches;
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

    public Collection<Tuple> getAllMatches(final MatchingFrame initialFrame) {
        matchingStarted();
        PlanExecutionIterator it = new PlanExecutionIterator(plan, initialFrame);        
        
        MatchingTable results = new MatchingTable();
        while (it.hasNext()) {
            final MatchingFrame frame = it.next();
            results.put(frame.getKey(), frame);
        }
        matchingFinished();
        return ImmutableList.copyOf(results.iterator());
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
