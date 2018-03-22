/*******************************************************************************
 * Copyright (c) 2010-2018, Zoltan Ujhelyi, IncQuery Labs Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.runtime.matchers.aggregators;

/**
 * @since 2.0
 */
class AverageAccumulator<Domain> {
    Domain value;
    long count;

    public AverageAccumulator(Domain value, long count) {
        super();
        this.value = value;
        this.count = count;
    }
    
}