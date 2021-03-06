/** 
 * Copyright (c) 2010-2016, Grill Balázs, IncQuery Labs Ltd.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.viatra.query.runtime.localsearch.planner.cost.impl;

import java.util.Optional;

import org.eclipse.viatra.query.runtime.localsearch.planner.cost.IConstraintEvaluationContext;
import org.eclipse.viatra.query.runtime.matchers.context.IInputKey;
import org.eclipse.viatra.query.runtime.matchers.tuple.TupleMask;
import org.eclipse.viatra.query.runtime.matchers.util.Accuracy;

/**
 * Cost function which calculates cost based on the cardinality of items in the runtime model, provided by the base indexer
 * 
 * @author Grill Balázs
 * @since 1.4
 */
public class IndexerBasedConstraintCostFunction extends StatisticsBasedConstraintCostFunction {
    
    
    

    /**
     * 
     */
    public IndexerBasedConstraintCostFunction() {
        super();
    }

    /**
     * @param inverseNavigationPenalty
     * @since 2.1
     */
    public IndexerBasedConstraintCostFunction(double inverseNavigationPenalty) {
        super(inverseNavigationPenalty);
    }

    @Override
    public Optional<Long> projectionSize(IConstraintEvaluationContext input, IInputKey supplierKey, TupleMask groupMask, Accuracy requiredAccuracy) {
        return input.getRuntimeContext().estimateCardinality(supplierKey, groupMask, requiredAccuracy);
    }
    
}
