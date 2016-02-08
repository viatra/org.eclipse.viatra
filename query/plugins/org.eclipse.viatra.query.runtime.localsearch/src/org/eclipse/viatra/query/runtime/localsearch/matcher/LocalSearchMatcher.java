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
import org.eclipse.viatra.query.runtime.localsearch.plan.SearchPlanExecutor;
import org.eclipse.viatra.query.runtime.matchers.psystem.queries.PQuery;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.UnmodifiableIterator;

/**
 * @author Zoltan Ujhelyi
 *
 */
public class LocalSearchMatcher {

    private ImmutableList<SearchPlanExecutor> plan;
    private int frameSize;
    private int keySize;
    private PQuery query;
    private List<ILocalSearchAdapter> adapters = Lists.newLinkedList();

    public ImmutableList<SearchPlanExecutor> getPlan() {
        return plan;
    }
    
    public int getFrameSize() {
        return frameSize;
    }
    
    public int getKeySize() {
        return keySize;
    }
    
    public List<ILocalSearchAdapter> getAdapters() {
        return adapters;
    }
    
    private static class PlanExecutionIterator extends UnmodifiableIterator<MatchingFrame> {

        private UnmodifiableIterator<SearchPlanExecutor> iterator;
        private SearchPlanExecutor currentPlan;
        private MatchingFrame frame;
        private boolean frameReturned;
        private List<ILocalSearchAdapter> adapters = Lists.newLinkedList();
        
        public PlanExecutionIterator(final ImmutableList<SearchPlanExecutor> plan, MatchingFrame initialFrame,
                List<ILocalSearchAdapter> adapters) {
            this.adapters = adapters;
            this.frame = initialFrame.clone();
            Preconditions.checkArgument(plan.size() > 0);
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
            return frame.clone();
        }

    }

    /**
     * If a descendant initializes a matcher using the default constructor, it is expected that it also calls the
     * {@link #setPlan(SearchPlanExecutor)} and {@link #setFramesize(int)} methods manually.
     */
    protected LocalSearchMatcher(PQuery query) {
        Preconditions.checkArgument(query != null, "Cannot initialize matcher with null query.");
        this.query = query;
    }

    public LocalSearchMatcher(PQuery query, SearchPlanExecutor plan, int keySize, int frameSize) {
        this(query,ImmutableList.of(plan),keySize,frameSize);
    }
    
    public LocalSearchMatcher(PQuery query, SearchPlanExecutor[] plan, int keySize, int frameSize) {
        this(query,ImmutableList.copyOf(plan),keySize,frameSize);
    }
    
    public LocalSearchMatcher(PQuery query, Collection<SearchPlanExecutor> plan, int keySize, int frameSize) {
        this(query, ImmutableList.copyOf(plan), keySize, frameSize);
    }
    
    protected LocalSearchMatcher(PQuery query, ImmutableList<SearchPlanExecutor> plan, int keySize, int frameSize) {
        this(query);
        this.keySize = keySize;
        this.plan = plan;
        this.frameSize = frameSize;
        this.adapters = Lists.newLinkedList(adapters);
    }
    
    public void addAdapter(ILocalSearchAdapter adapter) {
        this.adapters.add(adapter);
    }

    public void removeAdapter(ILocalSearchAdapter adapter) {
    	this.adapters.remove(adapter);
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

    protected void setKeysize(int keySize) {
        this.keySize = keySize;
    }

    public MatchingFrame editableMatchingFrame() {
        return new MatchingFrame(null, keySize, frameSize);
    }

    public boolean hasMatch() throws LocalSearchException {
        boolean hasMatch = hasMatch(editableMatchingFrame());
		return hasMatch;
    }

    public boolean hasMatch(final MatchingFrame initialFrame) throws LocalSearchException {
    	matchingStarted();
        PlanExecutionIterator it = new PlanExecutionIterator(plan, initialFrame, adapters);
        boolean hasMatch = it.hasNext();
        matchingFinished();
		return hasMatch;
    }

    public int countMatches() throws LocalSearchException {
        int countMatches = countMatches(editableMatchingFrame());
		return countMatches;
    }

    public int countMatches(MatchingFrame initialFrame) throws LocalSearchException {
    	matchingStarted();
        PlanExecutionIterator it = new PlanExecutionIterator(plan, initialFrame, adapters);
        int result = Iterators.size(it);
        matchingFinished();
		return result;
    }

    public MatchingFrame getOneArbitraryMatch() throws LocalSearchException {
        MatchingFrame oneArbitraryMatch = getOneArbitraryMatch(editableMatchingFrame());
		return oneArbitraryMatch;
    }

    public MatchingFrame getOneArbitraryMatch(final MatchingFrame initialFrame) throws LocalSearchException {
    	matchingStarted();
        PlanExecutionIterator it = new PlanExecutionIterator(plan, initialFrame, adapters);
        MatchingFrame returnValue = null;
        if (it.hasNext()) {
			returnValue = it.next();
        }
        matchingFinished();
        return returnValue;
    }

    public Collection<MatchingFrame> getAllMatches() throws LocalSearchException {
        Collection<MatchingFrame> allMatches = getAllMatches(editableMatchingFrame());
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

	public Collection<MatchingFrame> getAllMatches(final MatchingFrame initialFrame) throws LocalSearchException {
        matchingStarted();
		PlanExecutionIterator it = new PlanExecutionIterator(plan, initialFrame, adapters);        
        
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
        return query;
    }
}
