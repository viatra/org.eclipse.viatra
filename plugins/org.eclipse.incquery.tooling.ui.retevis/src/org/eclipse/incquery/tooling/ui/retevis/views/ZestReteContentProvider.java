/*******************************************************************************
 * Copyright (c) 2010-2012, Istvan Rath, Zoltan Ujhelyi and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Istvan Rath, Zoltan Ujhelyi - initial API and implementation
 *******************************************************************************/

package org.eclipse.incquery.tooling.ui.retevis.views;

import java.util.Vector;

import org.eclipse.gef4.zest.core.viewers.IGraphEntityContentProvider;
import org.eclipse.incquery.runtime.rete.boundary.PredicateEvaluatorNode;
import org.eclipse.incquery.runtime.rete.boundary.ReteBoundary;
import org.eclipse.incquery.runtime.rete.index.Indexer;
import org.eclipse.incquery.runtime.rete.index.IndexerListener;
import org.eclipse.incquery.runtime.rete.index.MemoryIdentityIndexer;
import org.eclipse.incquery.runtime.rete.index.MemoryNullIndexer;
import org.eclipse.incquery.runtime.rete.index.StandardIndexer;
import org.eclipse.incquery.runtime.rete.network.Node;
import org.eclipse.incquery.runtime.rete.network.ReteContainer;
import org.eclipse.incquery.runtime.rete.network.Supplier;
import org.eclipse.incquery.runtime.rete.remote.Address;
import org.eclipse.incquery.runtime.rete.single.UniquenessEnforcerNode;
import org.eclipse.jface.viewers.ArrayContentProvider;

public class ZestReteContentProvider extends ArrayContentProvider implements IGraphEntityContentProvider {

    @Override
    public Object[] getElements(Object inputElement) {
        if (inputElement instanceof ReteContainer) {
            return super.getElements(((ReteContainer) inputElement).getAllNodes());
        } else if (inputElement instanceof ReteBoundary) {
            ReteBoundary rb = (ReteBoundary) inputElement;
            Vector<Node> r = new Vector<Node>();
            for (Object a : rb.getAllUnaryRoots()) {
                r.add(rb.getHeadContainer().resolveLocal((Address) a)); // access all unary constraints
            }
            for (Object a : rb.getAllTernaryEdgeRoots()) {
                r.add(rb.getHeadContainer().resolveLocal((Address) a)); // access all ternary constraints
            }
            return r.toArray();
        }
        return super.getElements(inputElement);
    }

    @Override
    public Object[] getConnectedTo(Object entity) {
        if (entity instanceof Node) {
            Vector<Node> r = new Vector<Node>();
            if (entity instanceof Supplier) {
                r.addAll(((Supplier) entity).getReceivers());
                
                // look for memoryNullIndexer and memoryIdentityIndexer references
                if (entity instanceof PredicateEvaluatorNode) {
                    MemoryNullIndexer mni = ((PredicateEvaluatorNode)entity).getNullIndexer();
                    if (mni!=null) {
                        r.add(mni);
                    }
                    MemoryIdentityIndexer mii = ((PredicateEvaluatorNode)entity).getIdentityIndexer();
                    if (mii!=null) {
                        r.add(mii);
                    }
                }
                if (entity instanceof UniquenessEnforcerNode) {
                    MemoryNullIndexer mni = ((UniquenessEnforcerNode)entity).getNullIndexer();
                    if (mni!=null) {
                        r.add(mni);
                    }
                    MemoryIdentityIndexer mii = ((UniquenessEnforcerNode)entity).getIdentityIndexer();
                    if (mii!=null) {
                        r.add(mii);
                    }
                }
            }
            if (entity instanceof Indexer) {
                if (entity instanceof StandardIndexer) {
                    for (IndexerListener il : ((StandardIndexer) entity).getListeners()) {
                        r.add(il.getOwner());
                    }
                }
            }
            return r.toArray();
        }
        return null;
    }

}
