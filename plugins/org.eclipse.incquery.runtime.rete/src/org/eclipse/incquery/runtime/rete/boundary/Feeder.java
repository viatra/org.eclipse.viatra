/*******************************************************************************
 * Copyright (c) 2004-2010 Gabor Bergmann and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Gabor Bergmann - initial API and implementation
 *******************************************************************************/

package org.eclipse.incquery.runtime.rete.boundary;

import org.eclipse.incquery.runtime.matchers.tuple.FlatTuple;
import org.eclipse.incquery.runtime.matchers.tuple.Tuple;
import org.eclipse.incquery.runtime.rete.matcher.IPatternMatcherRuntimeContext;
import org.eclipse.incquery.runtime.rete.network.Direction;
import org.eclipse.incquery.runtime.rete.network.Network;
import org.eclipse.incquery.runtime.rete.network.Receiver;
import org.eclipse.incquery.runtime.rete.remote.Address;

/**
 * @author Gabor Bergmann
 * 
 */
public abstract class Feeder {
    protected Address<? extends Receiver> receiver;
    protected IPatternMatcherRuntimeContext context;
    protected Network network;
    protected InputConnector inputConnector;

    public Feeder(Address<? extends Receiver> receiver, InputConnector inputConnector) {
        super();
        this.receiver = receiver;
        this.inputConnector = inputConnector;
        this.network = inputConnector.getNetwork();
        this.context = inputConnector.getNetwork().getContext();
    }

    public abstract void feed();

    protected void emit(Tuple tuple) {
        network.sendConstructionUpdate(receiver, Direction.INSERT, tuple);
    }

    protected IPatternMatcherRuntimeContext.ModelElementCrawler unaryCrawler() {
        return new IPatternMatcherRuntimeContext.ModelElementCrawler() {
            public void crawl(Object element) {
                emit(new FlatTuple(inputConnector.wrapElement(element)));
            }
        };
    }

    protected IPatternMatcherRuntimeContext.ModelElementPairCrawler pairCrawler() {
        return new IPatternMatcherRuntimeContext.ModelElementPairCrawler() {
            public void crawl(Object first, Object second) {
                emit(new FlatTuple(inputConnector.wrapElement(first), inputConnector.wrapElement(second)));
            }
        };
    }

    protected IPatternMatcherRuntimeContext.ModelElementCrawler ternaryCrawler() {
        return new IPatternMatcherRuntimeContext.ModelElementCrawler() {
            public void crawl(Object element) {
                Object relation = element;
                Object from = context.ternaryEdgeSource(relation);
                Object to = context.ternaryEdgeTarget(relation);
                emit(new FlatTuple(inputConnector.wrapElement(relation), inputConnector.wrapElement(from), inputConnector.wrapElement(to)));
            }
        };
    }

}
