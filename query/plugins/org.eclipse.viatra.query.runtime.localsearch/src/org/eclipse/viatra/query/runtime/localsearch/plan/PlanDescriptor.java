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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.viatra.query.runtime.localsearch.operations.IIteratingSearchOperation;
import org.eclipse.viatra.query.runtime.localsearch.operations.ISearchOperation;
import org.eclipse.viatra.query.runtime.localsearch.planner.util.SearchPlanForBody;
import org.eclipse.viatra.query.runtime.matchers.context.IInputKey;
import org.eclipse.viatra.query.runtime.matchers.psystem.queries.PParameter;
import org.eclipse.viatra.query.runtime.matchers.psystem.queries.PQuery;

/**
 * @author Grill Balázs
 * @since 1.4
 *
 */
public class PlanDescriptor implements IPlanDescriptor {

    private final PQuery pquery;
    private final List<SearchPlanForBody> plan;
    private final Set<PParameter> adornment;
    private Set<IInputKey> iteratedKeys = null;
    
    public PlanDescriptor(PQuery pquery, Collection<SearchPlanForBody> plan, Set<PParameter> adornment) {
        this.pquery = pquery;
        this.plan = new ArrayList<>(plan);
        this.adornment = adornment;
    }

    @Override
    public PQuery getQuery() {
        return pquery;
    }

    @Override
    public Iterable<SearchPlanForBody> getPlan() {
        return plan;
    }

    @Override
    public Set<PParameter> getAdornment() {
        return adornment;
    }

    @Override
    public Set<IInputKey> getIteratedKeys() {
        if (iteratedKeys == null){
            Set<IInputKey> keys = new HashSet<>();
            for(SearchPlanForBody bodyPlan : plan){
                for(ISearchOperation operation : bodyPlan.getCompiledOperations()){
                    if (operation instanceof IIteratingSearchOperation){
                        keys.add(((IIteratingSearchOperation) operation).getIteratedInputKey());
                    }
                }
            }
            iteratedKeys = Collections.unmodifiableSet(keys);
        }
        return iteratedKeys;
    }
    
    @Override
    public String toString() {
        return new StringBuilder().append("Plan for ").append(pquery.getFullyQualifiedName()).append("(")
        .append(adornment.stream().map(PParameter::getName).collect(Collectors.joining(",")))
        .append("{")
        .append(plan.stream().map(Object::toString).collect(Collectors.joining("}\n{")))
        .append("}")
        .toString();
    }

}
