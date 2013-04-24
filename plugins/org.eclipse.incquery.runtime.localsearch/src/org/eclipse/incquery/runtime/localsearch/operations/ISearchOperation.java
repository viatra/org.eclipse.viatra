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
package org.eclipse.incquery.runtime.localsearch.operations;

import org.eclipse.incquery.runtime.localsearch.MatchingFrame;
import org.eclipse.incquery.runtime.localsearch.exceptions.LocalSearchException;

/**
 * @author Zoltan Ujhelyi
 * 
 */
public interface ISearchOperation {

    /**
     * During the execution of the corresponding plan, the onInitialize callback is evaluated before the execution of
     * the operation may begin. Operations may use this method to initialize its internal data structures.
     * 
     * @param frame
     */
    void onInitialize(MatchingFrame frame) throws LocalSearchException;

    /**
     * After the execution of the operation failed and {@link #execute(MatchingFrame)} returns false, the onBacktrack
     * callback is evaluated. Operations may use this method to clean up any temporary structures, and make the
     * operation ready for a new execution.
     * 
     * @param frame
     */
    void onBacktrack(MatchingFrame frame) throws LocalSearchException;

    /**
     * 
     * @param frame
     * @return true if successful, or false if backtracking needed
     */
    boolean execute(MatchingFrame frame) throws LocalSearchException;

}
