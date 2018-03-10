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

import java.util.Optional;

import org.eclipse.viatra.query.runtime.localsearch.MatchingFrame;
import org.eclipse.viatra.query.runtime.localsearch.operations.ISearchOperation;
import org.eclipse.viatra.query.runtime.localsearch.plan.SearchPlan;


/**
 * @author Marton Bur
 *
 */
public interface ILocalSearchAdapter {

    /**
     * 
     * @since 1.2
     */
    default void adapterRegistered(ILocalSearchAdaptable adaptable) {};
    /**
     * 
     * @since 1.2
     */
    default void adapterUnregistered(ILocalSearchAdaptable adaptable) {};
    
    /**
     * Callback method to indicate the start of a matching process
     *
     * @param lsMatcher the local search matcher that starts the matching
     */
    default void patternMatchingStarted(LocalSearchMatcher lsMatcher) {};

    /**
     * Callback method to indicate the end of a matching process
     * </p>
     * <strong>WARNING</strong>: It is not guaranteed that this method will be called;
     * it is possible that a match process will end after a match is found and no other matches are accessed.
     * 
     * @param lsMatcher the local search matcher that finished
     * @since 2.0
     */
    default void noMoreMatchesAvailable(LocalSearchMatcher lsMatcher) {};

    /**
     * Callback method to indicate switching to a new plan during the execution of a pattern matching
     * 
     * @param oldPlan the plan that is finished. Value is null when the first plan is starting.
     * @param newPlan the plan that will begin execution
     * @since 2.0
     */
    default void planChanged(Optional<SearchPlan> oldPlan, Optional<SearchPlan> newPlan) {};
    
    /**
     * Callback method to indicate the selection of an operation to execute
     * 
     * @param plan the current plan executor
     * @param frame the current matching frame
     * @param isBacktrack if true, the selected operation was reached via backtracking
     * @since 2.0
     */
    default void operationSelected(SearchPlan plan, ISearchOperation operation, MatchingFrame frame, boolean isBacktrack) {};

    /**
     * Callback method to indicate that an operation is executed
     * 
     * @param plan the current plan
     * @param frame the current matching frame
     * @param isSuccessful if true, the operation executed successfully, or false if the execution failed and backtracking will happen
     * @since 2.0
     */
    default void operationExecuted(SearchPlan plan, ISearchOperation operation, MatchingFrame frame, boolean isSuccessful) {};

    /**
     * Callback that is used to indicate that a match has been found
     * 
     * @param plan the search plan executor that found the match
     * @param frame the frame that holds the substitutions of the variables that match 
     * @since 2.0
     */
    default void matchFound(SearchPlan plan, MatchingFrame frame) {};
    /**
     * Callback that is used to indicate that the previously reported match has been found as a duplicate, thus will be ignored from the match results.
     * 
     * @param plan the search plan executor that found the match
     * @param frame the frame that holds the substitutions of the variables that match 
     * @since 2.0
     */
    default void duplicateMatchFound(MatchingFrame frame) {};

    /**
     * Callback method to indicate that a search plan is initialized in an executor with the given frame and starting operation
     * 
     * @param searchPlan
     * @param frame
     * @since 2.0
     */
    default void executorInitializing(SearchPlan searchPlan, MatchingFrame frame) {};
}
