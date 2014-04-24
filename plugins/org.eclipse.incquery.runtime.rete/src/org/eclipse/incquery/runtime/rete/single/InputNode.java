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
package org.eclipse.incquery.runtime.rete.single;

import org.eclipse.incquery.runtime.rete.network.ReteContainer;

/**
 * Node that represents an input (extensional) relation from outside Rete.
 * 
 * @author Bergmann Gabor
 *
 */
public class InputNode extends UniquenessEnforcerNode {

	public InputNode(ReteContainer reteContainer, int tupleWidth, Object inputKey) {
		super(reteContainer, tupleWidth);
		setTag(inputKey);
	}

}
