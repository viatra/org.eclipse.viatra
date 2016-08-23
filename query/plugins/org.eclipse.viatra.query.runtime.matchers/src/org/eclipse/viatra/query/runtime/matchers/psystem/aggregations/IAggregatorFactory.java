/*******************************************************************************
 * Copyright (c) 2010-2016, Zoltan Ujhelyi, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Tamas Szabo - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.runtime.matchers.psystem.aggregations;

/**
 * 
 * Describes an aggregation operator keyword, potentially with type polymorphism. The actual runtime
 * {@link IMultisetAggregationOperator} that implements the aggregation logic may depend on the type context.
 * 
 * <p>
 * Implementors are suggested to use lower-case classnames (as it will end up in the language) and are required use the
 * annotation {@link AggregatorType} to indicate type inference rules.
 * 
 * <p>
 * <strong>Important!</strong> Implemented aggregators must be (1) deterministic (2) pure and (3)support incremental
 * value updates in the internal operation.
 * 
 * @author Zoltan Ujhelyi
 * @since 1.4
 */

public interface IAggregatorFactory {

    /**
     * Given type parameters selected from {@link AggregatorType} annotations, returns a run-time aggregator operator
     * that is bound to the actual types.
     * 
     * @param domainClass
     *            Java type of the values that are being aggregated
     * @return the actual run-time aggregator logic, with type bindings
     */
    public BoundAggregator getAggregatorLogic(Class<?> domainClass);

}
