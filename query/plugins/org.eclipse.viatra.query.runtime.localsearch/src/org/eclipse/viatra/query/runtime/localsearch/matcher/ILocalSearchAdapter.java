/*******************************************************************************
 * Copyright (c) 2010-2014, Marton Bur, Akos Horvath, Zoltan Ujhelyi, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Marton Bur - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.runtime.localsearch.matcher;

import org.eclipse.viatra.query.runtime.localsearch.MatchingFrame;
import org.eclipse.viatra.query.runtime.localsearch.plan.SearchPlanExecutor;


/**
 * @author Marton Bur
 *
 */
public interface ILocalSearchAdapter {

    /**
     * 
     * @since 1.2
     */
    void adapterRegistered(ILocalSearchAdaptable adaptable);
    /**
     * 
     * @since 1.2
     */
    void adapterUnregistered(ILocalSearchAdaptable adaptable);
    
    /**
     * Callback method to indicate the start of a matching process
     *
     * @param lsMatcher the local search matcher that starts the matching
     */
    void patternMatchingStarted(LocalSearchMatcher lsMatcher);

    /**
     * Callback method to indicate the end of a matching process
     * 
     * @param lsMatcher the local search matcher that finished
     */
    void patternMatchingFinished(LocalSearchMatcher lsMatcher);

    /**
     * Callback method to indicate switching to a new plan during the execution of a pattern matching
     * 
     * @param oldPlanExecutor the plan that is finished. Value is null when the first plan is starting.
     * @param newPlanExecutor the plan that will begin execution
     */
    void planChanged(SearchPlanExecutor oldPlanExecutor, SearchPlanExecutor newPlanExecutor);
    
    /**
     * Callback method to indicate the selection of an operation to execute
     * 
     * @param planExecutor the current plan executor
     * @param frame the current matching frame
     */
    void operationSelected(SearchPlanExecutor planExecutor, MatchingFrame frame);

    /**
     * Callback method to indicate that an operation is executed
     * 
     * @param planExecutor the current plan executor
     * @param frame the current matching frame
     */
    void operationExecuted(SearchPlanExecutor planExecutor, MatchingFrame frame);

    /**
     * Callback that is used to indicate that a match has been found
     * 
     * @param planExecutor the search plan executor that found the match
     * @param frame the frame that holds the substitutions of the variables that match
     */
    void matchFound(SearchPlanExecutor planExecutor, MatchingFrame frame);

    /**
     * Callback method to indicate that a search plan executor is initialized with the given frame and starting operation
     * 
     * @param searchPlanExecutor
     * @param frame
     */
    void executorInitializing(SearchPlanExecutor searchPlanExecutor, MatchingFrame frame);
}
