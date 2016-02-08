/*******************************************************************************
 * Copyright (c) 2004-2008 Gabor Bergmann and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Gabor Bergmann - initial API and implementation
 *******************************************************************************/

package org.eclipse.incquery.runtime.rete.misc;

import java.util.Collection;

import org.eclipse.incquery.runtime.matchers.tuple.Tuple;
import org.eclipse.incquery.runtime.rete.network.ReteContainer;
import org.eclipse.incquery.runtime.rete.network.StandardNode;

/**
 * Node that always contains a single constant Tuple
 * 
 * @author Gabor Bergmann
 */
public class ConstantNode extends StandardNode {

    protected Tuple constant;

    public ConstantNode(ReteContainer reteContainer, Tuple constant) {
        super(reteContainer);
        this.constant = reteContainer.getNetwork().getEngine().getRuntimeContext().wrapTuple(constant);
    }

    public void pullInto(Collection<Tuple> collector) {
        collector.add(constant);
    }

}
