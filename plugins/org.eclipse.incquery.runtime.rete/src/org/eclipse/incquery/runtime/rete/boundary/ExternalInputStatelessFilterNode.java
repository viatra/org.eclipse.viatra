/*******************************************************************************
 * Copyright (c) 2010-2015, Bergmann Gabor, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Bergmann Gabor - initial API and implementation
 *******************************************************************************/
package org.eclipse.incquery.runtime.rete.boundary;

import org.eclipse.incquery.runtime.matchers.context.IInputKey;
import org.eclipse.incquery.runtime.matchers.context.IQueryRuntimeContext;
import org.eclipse.incquery.runtime.matchers.tuple.Tuple;
import org.eclipse.incquery.runtime.rete.matcher.ReteEngine;
import org.eclipse.incquery.runtime.rete.network.ReteContainer;
import org.eclipse.incquery.runtime.rete.single.FilterNode;

/**
 * @author Bergmann Gabor
 *
 */
public class ExternalInputStatelessFilterNode extends FilterNode implements Disconnectable {
	
	IQueryRuntimeContext context = null;
	IInputKey inputKey;
	private InputConnector inputConnector;

	public ExternalInputStatelessFilterNode(ReteContainer reteContainer) {
		super(reteContainer);
		this.inputConnector = reteContainer.getNetwork().getInputConnector();
	}
	
	@Override
	public boolean check(Tuple ps) {
		return context.containsTuple(inputKey, inputConnector.unwrapTuple(ps));
	}
	
	
	public void connectThroughContext(ReteEngine engine, IInputKey inputKey) {
		this.inputKey = inputKey;
		setTag(inputKey);
		
		final IQueryRuntimeContext context = engine.getRuntimeContext();
		if (!context.getMetaContext().isStateless(inputKey))
			throw new IllegalArgumentException(
					this.getClass().getSimpleName() + 
					" only applicable for stateless input keys; received instead " + 
					inputKey);
		
		this.context = context;
		
		engine.addDisconnectable(this);
	}
	
	@Override
	public void disconnect() {
		this.context = null;
	}
}
