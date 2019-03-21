/*******************************************************************************
 * Copyright (c) 2004-2008 Gabor Bergmann and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/

package org.eclipse.viatra.query.runtime.rete.misc;

import java.util.Collection;
import java.util.LinkedList;

import org.eclipse.viatra.query.runtime.matchers.tuple.Tuple;
import org.eclipse.viatra.query.runtime.rete.network.Direction;
import org.eclipse.viatra.query.runtime.rete.network.ReteContainer;
import org.eclipse.viatra.query.runtime.rete.network.communication.Timestamp;

/**
 * @author Gabor Bergmann
 * 
 *         A bag is a container that tuples can be dumped into. Does NOT propagate updates! Optimized for small contents
 *         size OR positive updates only.
 */
public class Bag extends SimpleReceiver {

    public Collection<Tuple> contents;

    public Bag(ReteContainer reteContainer) {
        super(reteContainer);
        contents = new LinkedList<Tuple>();
    }

    @Override
    public void update(Direction direction, Tuple updateElement, Timestamp timestamp) {
        if (direction == Direction.INSERT)
            contents.add(updateElement);
        else
            contents.remove(updateElement);
    }
    
}
