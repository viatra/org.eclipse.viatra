/*******************************************************************************
 * Copyright (c) 2010-2016, Zoltan Ujhelyi, IncQuery Labs Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.runtime.matchers.psystem.aggregations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.eclipse.viatra.query.runtime.matchers.aggregators.count;

/**
 * The aggregator type annotation describes the type constraints for the selected aggregator. In version 1.4, two kinds of
 * aggregators are supported:
 * 
 * <ol>
 * <li>An aggregator that does not consider any parameter value from the call ({@link count}), just calculates the
 * number of matches. This is represented by a single {@link Void} and a single corresponding return type.</li>
 * <li>An aggregator that considers a single parameter from the call, and executes some aggregate operations over it.
 * Such an aggregate operation can be defined over multiple types, where each possible parameter type has a corresponding return type declared.</li>
 * </ol>
 * 
 * <strong>Important!</strong> The parameterTypes and returnTypes arrays must have
 * <ul>
 * <li>The same number of classes defined each.</li>
 * <li>Items are corresponded by index.</li>
 * <li>Items should represent data types</li>
 * </ul>
 * 
 * @author Zoltan Ujhelyi
 * @since 1.4
 *
 */
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface AggregatorType {

    Class<?>[] parameterTypes();

    Class<?>[] returnTypes();
}
