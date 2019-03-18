/*******************************************************************************
 * Copyright (c) 2010-2019, Tamas Szabo, Istvan Rath and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.query.runtime.rete.single.ddf;

import java.util.Iterator;
import java.util.Map;

import org.eclipse.viatra.query.runtime.matchers.tuple.Tuple;
import org.eclipse.viatra.query.runtime.rete.network.ProductionNode;
import org.eclipse.viatra.query.runtime.rete.network.ReteContainer;
import org.eclipse.viatra.query.runtime.rete.traceability.CompiledQuery;
import org.eclipse.viatra.query.runtime.rete.traceability.TraceInfo;
/**
 * Differential dataflow implementation of the Production node, based on {@link DifferentialUniquenessEnforcerNode}.
 *
 * @author Tamas Szabo
 * @noinstantiate This class is not intended to be instantiated by clients.
 * @since 2.2
 */
public class DifferentialProductionNode extends DifferentialUniquenessEnforcerNode implements ProductionNode {

    protected final Map<String, Integer> posMapping;

    public DifferentialProductionNode(final ReteContainer reteContainer, final Map<String, Integer> posMapping) {
        super(reteContainer, posMapping.size());
        this.posMapping = posMapping;
    }

    @Override
    public Map<String, Integer> getPosMapping() {
        return this.posMapping;
    }

    @Override
    public Iterator<Tuple> iterator() {
        return this.memory.keySet().iterator();
    }

    @Override
    public void acceptPropagatedTraceInfo(final TraceInfo traceInfo) {
        if (traceInfo.propagateToProductionNodeParentAlso()) {
            super.acceptPropagatedTraceInfo(traceInfo);
        }
    }

    @Override
    public String toString() {
        for (final TraceInfo traceInfo : this.traceInfos) {
            if (traceInfo instanceof CompiledQuery) {
                final String patternName = ((CompiledQuery) traceInfo).getPatternName();
                return String.format(this.getClass().getName() + "<%s>=%s", patternName, super.toString());
            }
        }
        return super.toString();
    }

}
