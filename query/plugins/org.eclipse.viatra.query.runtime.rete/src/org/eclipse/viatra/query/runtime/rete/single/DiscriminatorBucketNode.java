/*******************************************************************************
 * Copyright (c) 2010-2016, Gabor Bergmann, IncQueryLabs Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Gabor Bergmann - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.runtime.rete.single;

import java.util.Collection;

import org.eclipse.viatra.query.runtime.matchers.tuple.Tuple;
import org.eclipse.viatra.query.runtime.rete.network.Direction;
import org.eclipse.viatra.query.runtime.rete.network.ReteContainer;
import org.eclipse.viatra.query.runtime.rete.network.Supplier;

/**
 * A bucket holds a filtered set of tuples of its parent {@link DiscriminatorDispatcherNode}. 
 * Exactly those that have the given bucket key at their discrimination column.
 * @author Gabor Bergmann
 * @since 1.5
 */
public class DiscriminatorBucketNode extends SingleInputNode {

    private Object bucketKey;

    public DiscriminatorBucketNode(ReteContainer reteContainer, Object bucketKey) {
        super(reteContainer);
        this.bucketKey = bucketKey;
    }

    @Override
    public void pullInto(Collection<Tuple> collector) {
       if (parent != null)
           getDispatcher().pullIntoFiltered(collector, bucketKey);
    }

    @Override
    public void update(Direction direction, Tuple updateElement) {
        propagateUpdate(direction, updateElement);
    }

    public Object getBucketKey() {
        return bucketKey;
    }
    
    @Override
    public void appendParent(Supplier supplier) {
        if (! (supplier instanceof DiscriminatorDispatcherNode))
            throw new IllegalArgumentException();
        super.appendParent(supplier);
    }
    
    public DiscriminatorDispatcherNode getDispatcher() {
        return (DiscriminatorDispatcherNode) parent;
    }
    
    @Override
    protected String toStringCore() {
        return String.format("%s<%s=='%s'>", 
                super.toStringCore(), 
                (getDispatcher() == null) ? "?" : getDispatcher().getDiscriminationColumnIndex(), 
                bucketKey);
    }
}
