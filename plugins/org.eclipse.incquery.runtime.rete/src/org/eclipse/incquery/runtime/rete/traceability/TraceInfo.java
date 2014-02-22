/*******************************************************************************
 * Copyright (c) 2010-2014, Bergmann Gabor, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Bergmann Gabor - initial API and implementation
 *******************************************************************************/
package org.eclipse.incquery.runtime.rete.traceability;

import org.eclipse.incquery.runtime.rete.network.Node;


/**
 * Traces the node back to a purpose for which the node was built, 
 * to explain why the node is there and what it means.
 * @author Bergmann Gabor
 */
public interface TraceInfo {
	boolean propagateToIndexerParent();
	boolean propagateFromIndexerToSupplierParent();
	boolean propagateFromStandardNodeToSupplierParent();  	
	boolean propagateToProductionNodeParentAlso();

	void assignNode(Node node);
	Node getNode();
}
// /**
// * The semantics of the tuples contained in this node.
// * @return a tuple of correct size representing the semantics of each position.
// * @post not null
// */
// Tuple getSemantics();