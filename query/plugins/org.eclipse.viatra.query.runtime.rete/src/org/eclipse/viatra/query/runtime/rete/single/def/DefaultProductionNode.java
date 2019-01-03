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

package org.eclipse.viatra.query.runtime.rete.single.def;

import java.util.Iterator;
import java.util.Map;

import org.eclipse.viatra.query.runtime.matchers.context.IPosetComparator;
import org.eclipse.viatra.query.runtime.matchers.tuple.Tuple;
import org.eclipse.viatra.query.runtime.matchers.tuple.TupleMask;
import org.eclipse.viatra.query.runtime.rete.network.ProductionNode;
import org.eclipse.viatra.query.runtime.rete.network.ReteContainer;
import org.eclipse.viatra.query.runtime.rete.traceability.CompiledQuery;
import org.eclipse.viatra.query.runtime.rete.traceability.TraceInfo;

/**
 * Default implementation of the Production node, based on UniquenessEnforcerNode
 *
 * @author Gabor Bergmann
 * @noinstantiate This class is not intended to be instantiated by clients.
 */
public class DefaultProductionNode extends DefaultUniquenessEnforcerNode implements ProductionNode {

    protected final Map<String, Integer> posMapping;

    /**
     * @since 1.6
     */
    public DefaultProductionNode(final ReteContainer reteContainer, final Map<String, Integer> posMapping,
            final boolean deleteRederiveEvaluation) {
        this(reteContainer, posMapping, deleteRederiveEvaluation, null, null, null);
    }

    /**
     * @since 1.6
     */
    public DefaultProductionNode(final ReteContainer reteContainer, final Map<String, Integer> posMapping,
            final boolean deleteRederiveEvaluation, final TupleMask coreMask, final TupleMask posetMask,
            final IPosetComparator posetComparator) {
        super(reteContainer, posMapping.size(), deleteRederiveEvaluation, coreMask, posetMask, posetComparator);
        this.posMapping = posMapping;
    }

    @Override
    public Map<String, Integer> getPosMapping() {
        return posMapping;
    }

    @Override
    public Iterator<Tuple> iterator() {
        return memory.iterator();
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
