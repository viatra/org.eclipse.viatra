/*******************************************************************************
 * Copyright (c) 2010-2013, Zoltan Ujhelyi, Akos Horvath, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi, Akos Horvath - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.runtime.localsearch.operations;

import java.util.List;

import org.eclipse.viatra.query.runtime.localsearch.MatchingFrame;
import org.eclipse.viatra.query.runtime.localsearch.matcher.ISearchContext;
import org.eclipse.viatra.query.runtime.matchers.ViatraQueryRuntimeException;

/**
 * @author Zoltan Ujhelyi
 * 
 */
public interface ISearchOperation {

    /**
     * During the execution of the corresponding plan, the onInitialize callback is evaluated before the execution of
     * the operation may begin. Operations may use this method to initialize its internal data structures.
     * 
     * @throws ViatraQueryRuntimeException
     */
    void onInitialize(MatchingFrame frame, ISearchContext context);

    /**
     * After the execution of the operation failed and {@link #execute(MatchingFrame, ISearchContext)} returns false, the onBacktrack
     * callback is evaluated. Operations may use this method to clean up any temporary structures, and make the
     * operation ready for a new execution.
     * 
     * @throws ViatraQueryRuntimeException
     */
    void onBacktrack(MatchingFrame frame, ISearchContext context);

    /**
     * 
     * @param frame
     * @param context
     * @return true if successful, or false if backtracking needed
     * @throws ViatraQueryRuntimeException
     */
    boolean execute(MatchingFrame frame, ISearchContext context);
    
    /**
     * 
     * @return the ordered list of the variable numbers that are affected by the search operation
     */
    List<Integer> getVariablePositions();

}
