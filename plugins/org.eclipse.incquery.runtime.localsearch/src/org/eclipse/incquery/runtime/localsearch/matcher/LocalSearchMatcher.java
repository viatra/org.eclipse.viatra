/*******************************************************************************
 * Copyright (c) 2010-2013, Zoltan Ujhelyi, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi - initial API and implementation
 *******************************************************************************/
package org.eclipse.incquery.runtime.localsearch.matcher;

import java.util.Collection;
import java.util.NoSuchElementException;

import org.eclipse.incquery.runtime.localsearch.MatchingFrame;
import org.eclipse.incquery.runtime.localsearch.MatchingTable;
import org.eclipse.incquery.runtime.localsearch.exceptions.LocalSearchException;
import org.eclipse.incquery.runtime.localsearch.plan.SearchPlanExecutor;
import org.eclipse.incquery.runtime.matchers.psystem.queries.PQuery;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterators;
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

    private static class PlanExecutionIterator extends UnmodifiableIterator<MatchingFrame> {

        private UnmodifiableIterator<SearchPlanExecutor> iterator;
        private SearchPlanExecutor currentPlan;
        private MatchingFrame frame;
        private boolean frameReturned;

        public PlanExecutionIterator(final ImmutableList<SearchPlanExecutor> plan, MatchingFrame initialFrame) {
            this.frame = initialFrame.clone();
            Preconditions.checkArgument(plan.size() > 0);
            iterator = plan.iterator();
            getNextPlan();
            frameReturned = true;
        }

        private void getNextPlan() {
            currentPlan = iterator.next();
            currentPlan.resetPlan();
        }

        @Override
        public boolean hasNext() {
            if (!frameReturned) {
                return true;
            }
            try {
                boolean foundMatch = currentPlan.execute(frame);
                while ((!foundMatch) && iterator.hasNext()) {
                    getNextPlan();
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

    public LocalSearchMatcher(PQuery query, SearchPlanExecutor plan, int keySize, int framesize) {
        this(query);
        this.keySize = keySize;
        this.plan = ImmutableList.of(plan);
        this.frameSize = framesize;
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
        return hasMatch(editableMatchingFrame());
    }

    public boolean hasMatch(final MatchingFrame initialFrame) throws LocalSearchException {
        PlanExecutionIterator it = new PlanExecutionIterator(plan, initialFrame);
        return it.hasNext();
    }

    public int countMatches() throws LocalSearchException {
        return countMatches(editableMatchingFrame());
    }

    public int countMatches(MatchingFrame initialFrame) throws LocalSearchException {
        PlanExecutionIterator it = new PlanExecutionIterator(plan, initialFrame);
        return Iterators.size(it);
    }

    public MatchingFrame getOneArbitraryMatch() throws LocalSearchException {
        return getOneArbitraryMatch(editableMatchingFrame());
    }

    public MatchingFrame getOneArbitraryMatch(final MatchingFrame initialFrame) throws LocalSearchException {
        PlanExecutionIterator it = new PlanExecutionIterator(plan, initialFrame);
        if (it.hasNext()) {
            return it.next();
        } else {
            return null;
        }
    }

    public Collection<MatchingFrame> getAllMatches() throws LocalSearchException {
        return getAllMatches(editableMatchingFrame());
    }

    public Collection<MatchingFrame> getAllMatches(final MatchingFrame initialFrame) throws LocalSearchException {
        PlanExecutionIterator it = new PlanExecutionIterator(plan, initialFrame);
        MatchingTable results = new MatchingTable();
        while (it.hasNext()) {
            final MatchingFrame frame = it.next();
            results.put(frame.getKey(), frame);
        }
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
