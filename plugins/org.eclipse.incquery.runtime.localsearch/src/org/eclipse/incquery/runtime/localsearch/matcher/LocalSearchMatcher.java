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

import org.eclipse.incquery.runtime.localsearch.MatchingFrame;
import org.eclipse.incquery.runtime.localsearch.MatchingTable;
import org.eclipse.incquery.runtime.localsearch.exceptions.LocalSearchException;
import org.eclipse.incquery.runtime.localsearch.plan.SearchPlan;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterators;
import com.google.common.collect.UnmodifiableIterator;

/**
 * @author Zoltan Ujhelyi
 *
 */
public class LocalSearchMatcher {

    private ImmutableList<SearchPlan> plan;
    private int frameSize;
    private int keySize;

    private static class PlanExecutionIterator extends UnmodifiableIterator<MatchingFrame> {

        private UnmodifiableIterator<SearchPlan> iterator;
        private SearchPlan currentPlan;
        private MatchingFrame frame;

        public PlanExecutionIterator(final ImmutableList<SearchPlan> plan, MatchingFrame initialFrame) {
            this.frame = initialFrame.clone();
            Preconditions.checkArgument(plan.size() > 0);
            iterator = plan.iterator();
            getNextPlan();
        }

        private void getNextPlan() {
            currentPlan = iterator.next();
            currentPlan.resetPlan();
        }

        @Override
        public boolean hasNext() {
            try {
                boolean foundMatch = currentPlan.execute(frame);
                while ((!foundMatch) && iterator.hasNext()) {
                    getNextPlan();
                    foundMatch = currentPlan.execute(frame);
                }
                return foundMatch;
            } catch (LocalSearchException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public MatchingFrame next() {
            return frame.clone();
        }

    }

    /**
     * If a descendant initializes a matcher using the default constructor, it is expected that it also calls the
     * {@link #setPlan(SearchPlan)} and {@link #setFramesize(int)} methods manually.
     */
    protected LocalSearchMatcher() {
    }

    public LocalSearchMatcher(SearchPlan plan, int keySize, int framesize) {
        super();
        this.keySize = keySize;
        this.plan = ImmutableList.of(plan);
        this.frameSize = framesize;
    }

    protected void setPlan(SearchPlan plan) {
        this.plan = ImmutableList.of(plan);
    }

    protected void setPlan(SearchPlan[] plan) {
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
}
