/*******************************************************************************
 * Copyright (c) 2010-2016, Gabor Bergmann, IncQueryLabs Ltd.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.query.runtime.rete.aggregation;

import org.eclipse.viatra.query.runtime.rete.index.Indexer;

/**
 * Expresses that aggregators expose specialized non-enumerable indexers for outer joining.
 * @author Gabor Bergmann
 *
 * @since 1.4
 * 
 */
public interface IAggregatorNode {

    Indexer getAggregatorOuterIndexer();

    Indexer getAggregatorOuterIdentityIndexer(int resultPositionInSignature);

}