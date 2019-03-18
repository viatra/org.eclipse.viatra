/*******************************************************************************
 * Copyright (c) 2010-2017, Zoltan Ujhelyi, IncQuery Labs Ltd.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.query.runtime.localsearch.planner.compiler;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.viatra.query.runtime.localsearch.matcher.CallWithAdornment;
import org.eclipse.viatra.query.runtime.localsearch.matcher.MatcherReference;
import org.eclipse.viatra.query.runtime.localsearch.operations.ISearchOperation;
import org.eclipse.viatra.query.runtime.matchers.ViatraQueryRuntimeException;
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
     * @throws ViatraQueryRuntimeException 
     */
    List<ISearchOperation> compile(SubPlan plan, Set<PParameter> boundParameters);

    /**
     * Replaces previous method returning {@link MatcherReference}
     * @since 2.1
     */
    Set<CallWithAdornment> getDependencies();

    /**
     * @return the cached variable bindings for the previously created plan
     */
    Map<PVariable, Integer> getVariableMappings();

}