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
package org.eclipse.incquery.runtime.localsearch.matcher;

import org.eclipse.incquery.runtime.localsearch.MatchingFrame;
import org.eclipse.incquery.runtime.localsearch.plan.SearchPlanExecutor;


/**
 * @author Marton Bur
 *
 */
public interface ILocalSearchAdapter {

    /**
     * Callback method to indicate the start of a matching process
     */
    void patternMatchingStarted(LocalSearchMatcher lsMatcher);

    /**
     * Callback method to indicate the end of a matching process
     */
    void patternMatchingFinished(LocalSearchMatcher lsMatcher);

    /**
     * Callback method to indicate the start of a local search plan execution
     * 
     * @param searchPlanExecutor the plan that is started
     * @param currentOperation the first operation to execute
     */
    void planStarted(SearchPlanExecutor searchPlanExecutor);

    /**
     * Callback method to indicate the end of a local search plan execution
     * 
     * @param searchPlanExecutor the plan that is started
     * @param currentOperation the last operation to execute
     */
    void planFinished(SearchPlanExecutor searchPlanExecutor);
    
    /**
     * Callback method to indicate the selection of an operation to execute
     * 
     * @param planExecutor the current plan executor
     * @param currentOperation the operation that is selected for execution
     * @param frame the current matching frame
     */
    void operationSelected(SearchPlanExecutor planExecutor, MatchingFrame frame);

    /**
     * Callback method to indicate that an operation is executed
     * 
     * @param planExecutor the current plan executor
     * @param currentOperation the last operation that is executed
     * @param frame the current matching frame
     */
    void operationExecuted(SearchPlanExecutor planExecutor, MatchingFrame frame);

}
