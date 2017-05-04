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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.viatra.query.runtime.matchers.context.IQueryRuntimeContext;
import org.eclipse.viatra.query.runtime.matchers.tuple.Tuple;
import org.eclipse.viatra.query.runtime.rete.boundary.ReteBoundary;
import org.eclipse.viatra.query.runtime.rete.network.Direction;
import org.eclipse.viatra.query.runtime.rete.network.Receiver;
import org.eclipse.viatra.query.runtime.rete.network.ReteContainer;

/**
 * Node that sends tuples off to different buckets (attached as children of type {@link DiscriminatorBucketNode}), 
 *  based on the value of a given column.
 *  
 * <p> Tuple contents and bucket keys have already been wrapped using {@link IQueryRuntimeContext#wrapElement(Object)}
 * 
 * @author Gabor Bergmann
 * @since 1.5
 */
public class DiscriminatorDispatcherNode extends SingleInputNode {

    private int discriminationColumnIndex;
    private Map<Object, DiscriminatorBucketNode> buckets = new HashMap<Object, DiscriminatorBucketNode>();

    /**
     * @param reteContainer
     */
    public DiscriminatorDispatcherNode(ReteContainer reteContainer, int discriminationColumnIndex) {
        super(reteContainer);
        this.discriminationColumnIndex = discriminationColumnIndex;
    }

    @Override
    public void update(Direction direction, Tuple updateElement) {
        Object dispatchKey = updateElement.get(discriminationColumnIndex);
        DiscriminatorBucketNode bucket = buckets.get(dispatchKey);
        if (bucket != null)
            reteContainer.sendUpdateInternal(bucket, direction, updateElement);
    }

    public int getDiscriminationColumnIndex() {
        return discriminationColumnIndex;
    }
    
    @Override
    public void pullInto(Collection<Tuple> collector) {
        propagatePullInto(collector);
    }
    
    public void pullIntoFiltered(Collection<Tuple> collector, Object bucketKey) {
        ArrayList<Tuple> unfiltered = new ArrayList<Tuple>();
        propagatePullInto(unfiltered);
        for (Tuple tuple : unfiltered) {
            if (bucketKey.equals(tuple.get(discriminationColumnIndex)))
                collector.add(tuple);
        }
    }

    @Override
    public void appendChild(Receiver receiver) {
        super.appendChild(receiver);
        if (receiver instanceof DiscriminatorBucketNode) {
            DiscriminatorBucketNode bucket = (DiscriminatorBucketNode) receiver;
            DiscriminatorBucketNode old = buckets.put(bucket.getBucketKey(), bucket);
            if (old != null) throw new IllegalStateException();
        }
    }
    
    @Override
    public void removeChild(Receiver receiver) {
        super.removeChild(receiver);
        if (receiver instanceof DiscriminatorBucketNode) {
            DiscriminatorBucketNode bucket = (DiscriminatorBucketNode) receiver;
            DiscriminatorBucketNode old = buckets.remove(bucket.getBucketKey());
            if (old != bucket) throw new IllegalStateException();
        }
    }
    
    @Override
    protected String toStringCore() {
        return super.toStringCore() + '<' + discriminationColumnIndex + '>';
    }
    
}
