/*******************************************************************************
 * Copyright (c) 2010-2017, Zoltan Ujhelyi, IncQuery Labs Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.runtime.localsearch.operations;

import java.util.Objects;

import org.eclipse.viatra.query.runtime.base.itc.alg.incscc.IncSCCAlg;
import org.eclipse.viatra.query.runtime.base.itc.graphimpl.Graph;
import org.eclipse.viatra.query.runtime.localsearch.exceptions.LocalSearchException;
import org.eclipse.viatra.query.runtime.localsearch.matcher.ISearchContext;
import org.eclipse.viatra.query.runtime.localsearch.operations.CallOperationHelper.PatternCall;
import org.eclipse.viatra.query.runtime.matchers.backend.IUpdateable;
import org.eclipse.viatra.query.runtime.matchers.psystem.queries.PQuery;
import org.eclipse.viatra.query.runtime.matchers.tuple.Tuple;
import org.eclipse.viatra.query.runtime.matchers.util.IProvider;

/**
 * Generic helper for calculating and accessing transitive closures of called incremental graph patterns. Such a graph
 * can be shared between all call adornments.
 * 
 * @author Zoltan Ujhelyi
 * @since 1.7
 */
public class TransitiveClosureGraph implements IUpdateable {

    private Graph<Object> graph = new Graph<>();
    private IncSCCAlg<Object> tcAlg = new IncSCCAlg<>(graph);
    private PatternCall call;

    private static final class TransitiveClosureGraphKey {
        private final PQuery query;

        private TransitiveClosureGraphKey(PQuery query) {
            this.query = query;
        }

        @Override
        public int hashCode() {
            return query.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            TransitiveClosureGraphKey other = (TransitiveClosureGraphKey) obj;
            return Objects.equals(query, other.query);
        }

    }

    public static TransitiveClosureGraph accessClosureGraph(final ISearchContext context,
            final CallOperationHelper helper) {
        return context.accessBackendLevelCache(new TransitiveClosureGraphKey(helper.getCalledQuery()),
                TransitiveClosureGraph.class, new IProvider<TransitiveClosureGraph>() {

                    @Override
                    public TransitiveClosureGraph get() {
                        try {
                            return new TransitiveClosureGraph(helper, context);
                        } catch (LocalSearchException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });
    }

    private TransitiveClosureGraph(CallOperationHelper helper, ISearchContext context) throws LocalSearchException {
        call = helper.createCall(context);
        call.registerChangeListener(this);

    }

    @Override
    public void update(Tuple updateElement, boolean isInsertion) {
        Object sourceNode = updateElement.get(0);
        Object targetNode = updateElement.get(1);
        if (isInsertion) {
            graph.insertNode(sourceNode); // Ensure source is added
            graph.insertNode(targetNode); // Ensure target is added
            graph.insertEdge(sourceNode, targetNode);
        } else {
            graph.deleteEdge(sourceNode, targetNode);

            if (tcAlg.isIsolated(sourceNode)) {
                graph.deleteNode(sourceNode);
            }
            if (!Objects.equals(sourceNode, targetNode) && tcAlg.isIsolated(targetNode)) {
                graph.deleteNode(targetNode);
            }
        }

    }

    public Iterable<Object> getAllSources(Object target) {
        return tcAlg.getAllReachableSources(target);
    }

    public Iterable<Object> getAllTargets(Object source) {
        return tcAlg.getAllReachableTargets(source);
    }

    public void dispose() {
        call.removeChangeListener(this);
        tcAlg.dispose();
    }
}
