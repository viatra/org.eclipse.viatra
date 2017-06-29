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
package org.eclipse.viatra.query.runtime.localsearch.planner.compiler;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.viatra.query.runtime.localsearch.matcher.MatcherReference;
import org.eclipse.viatra.query.runtime.localsearch.operations.ISearchOperation;
import org.eclipse.viatra.query.runtime.matchers.planning.QueryProcessingException;
import org.eclipse.viatra.query.runtime.matchers.planning.SubPlan;
import org.eclipse.viatra.query.runtime.matchers.psystem.PVariable;
import org.eclipse.viatra.query.runtime.matchers.psystem.queries.PParameter;

/**
 * An operation compiler is responsible for creating executable search plans from the subplan structure.
 * 
 * @author Zoltan Ujhelyi
 * @since 1.7
 *
 */
public interface IOperationCompiler {

    /**
     * Compiles a plan of <code>POperation</code>s to a list of type <code>List&ltISearchOperation></code>
     * 
     * @param plan
     * @param boundParameters
     * @return an ordered list of POperations that make up the compiled search plan
     * @throws QueryProcessingException 
     */
    List<ISearchOperation> compile(SubPlan plan, Set<PParameter> boundParameters) throws QueryProcessingException;

    Set<MatcherReference> getDependencies();

    /**
     * @return the cached variable bindings for the previously created plan
     */
    Map<PVariable, Integer> getVariableMappings();

}