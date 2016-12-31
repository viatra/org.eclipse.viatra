/*******************************************************************************
 * Copyright (c) 2010-2016, Tamas Szabo, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Tamas Szabo - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.runtime.matchers.context;

import org.eclipse.viatra.query.runtime.matchers.tuple.Tuple;

/**
 * Implementations of this interface aid the query engine with the ordering of poset elements. 
 * This information is particularly important in the delete and re-derive evaluation mode 
 * because they let the engine identify monotone change pairs.
 * 
 * @author Tamas Szabo
 *
 */
public interface IPosetComparator {

    /**
     * Returns true if the 'left' tuple of poset elements is smaller or equal than the 'right' tuple of poset elements. 
     *  
     * @param left the first tuple of poset elements
     * @param right the second tuple of poset elements
     * @return true if left is smaller or equal to right, false otherwise
     */
    public boolean isLessOrEqual(Tuple left, Tuple right);
    
}
