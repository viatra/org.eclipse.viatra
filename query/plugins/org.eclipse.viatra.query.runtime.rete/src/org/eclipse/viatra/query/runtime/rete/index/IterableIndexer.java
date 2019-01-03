/*******************************************************************************
 * Copyright (c) 2004-2009 Gabor Bergmann and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Gabor Bergmann - initial API and implementation
 *******************************************************************************/

package org.eclipse.viatra.query.runtime.rete.index;

import org.eclipse.viatra.query.runtime.matchers.tuple.Tuple;

/**
 * An indexer that allows the iteration of all retrievable tuple groups (or reduced groups).
 * 
 * @author Gabor Bergmann
 * 
 */
public interface IterableIndexer extends Indexer, Iterable<Tuple> {

    /**
     * A view consisting of exactly those signatures whose tuple group is not empty
     * @since 2.0
     */
    public Iterable<Tuple> getSignatures();

    /**
     * @return the number of signatures whose tuple group is not empty
     * @since 2.0
     */
    public int getBucketCount();
    
}
