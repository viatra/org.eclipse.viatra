/*******************************************************************************
 * Copyright (c) 2010-2016, Grill Balázs, IncQuery Labs Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Grill Balázs - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.runtime.localsearch.plan;

import java.util.Set;

import org.eclipse.viatra.query.runtime.localsearch.planner.util.SearchPlanForBody;
import org.eclipse.viatra.query.runtime.matchers.context.IInputKey;
import org.eclipse.viatra.query.runtime.matchers.psystem.queries.PParameter;
import org.eclipse.viatra.query.runtime.matchers.psystem.queries.PQuery;

/**
 * Denotes an executable plan
 * 
 * @author Grill Balázs
 * @since 1.4
 *
 */
public interface IPlanDescriptor {

    /**
     * The query which this plan implements
     * @return
     */
    public PQuery getQuery();
    
    /**
     * The iterator of executable search plans for each body in the query
     * @return
     */
    public Iterable<SearchPlanForBody> getPlan();
    
    /**
     * The set of parameters this plan assumes to be bound
     * @return
     */
    public Set<PParameter> getAdornment();
    
    /**
     * The collection of {@link IInputKey}s which needs to be iterated during the execution of this plan. For optimal
     * performance, instances of these keys might be indexed.
     * @return
     */
    public Set<IInputKey> getIteratedKeys();
    
}
