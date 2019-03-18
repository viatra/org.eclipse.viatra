/*******************************************************************************
 * Copyright (c) 2010-2019, Tamas Szabo, Istvan Rath and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.query.runtime.rete.network.communication.ddf;

import org.eclipse.viatra.query.runtime.matchers.tuple.Tuple;
import org.eclipse.viatra.query.runtime.rete.index.IndexerListener;
import org.eclipse.viatra.query.runtime.rete.network.Direction;
import org.eclipse.viatra.query.runtime.rete.network.Node;
import org.eclipse.viatra.query.runtime.rete.network.ProductionNode;

/**
 * A differential proxy for another {@link IndexerListener}, which performs some preprocessing 
 * on the differential timestamps before passing it on to the real recipient. 
 * <p>
 * These proxies are used on edges leading into {@link ProductionNode}s. Because {@link ProductionNode}s 
 * never ask back the indexer for its contents, there is no need to also apply the proxy on that direction. 
 * 
 * @author Tamas Szabo
 * @since 2.2
 */
public class DifferentialIndexerListenerProxy implements IndexerListener {

    protected final DifferentialPreprocessor preprocessor;
    protected final IndexerListener wrapped;

    public DifferentialIndexerListenerProxy(final IndexerListener wrapped,
            final DifferentialPreprocessor preprocessor) {
        this.wrapped = wrapped;
        this.preprocessor = preprocessor;
    }

    @Override
    public Node getOwner() {
        return this.wrapped.getOwner();
    }

    @Override
    public void notifyIndexerUpdate(final Direction direction, final Tuple updateElement, final Tuple signature,
            final boolean change, final DifferentialTimestamp timestamp) {
        this.wrapped.notifyIndexerUpdate(direction, updateElement, signature, change, preprocessor.process(timestamp));
    }

    @Override
    public String toString() {
        return this.preprocessor.toString() + "_PROXY -> " + this.wrapped.toString();
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        } else if (obj == this) {
            return true;
        } else {
            final DifferentialIndexerListenerProxy that = (DifferentialIndexerListenerProxy) obj;
            return this.wrapped.equals(that.wrapped) && this.preprocessor == that.preprocessor;
        }
    }

    @Override
    public int hashCode() {
        int hash = 1;
        hash = hash * 17 + this.wrapped.hashCode();
        hash = hash * 31 + this.preprocessor.hashCode();
        return hash;
    }

}
