/*******************************************************************************
 * Copyright (c) 2010-2017, Zoltan Ujhelyi, IncQuery Labs Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.runtime.localsearch.operations;

import org.eclipse.viatra.query.runtime.localsearch.operations.util.CallInformation;

/**
 * Marker interface for pattern matcher call operations, such as positive and negative pattern calls or match aggregators.
 * 
 * @author Zoltan Ujhelyi
 * @since 1.7
 */
public interface IPatternMatcherOperation {

    /**
     * Returns the precomputed call information associated with the current operation
     * @since 2.0
     */
    CallInformation getCallInformation();
}
