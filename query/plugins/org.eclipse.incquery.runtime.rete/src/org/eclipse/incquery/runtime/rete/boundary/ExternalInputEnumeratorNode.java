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

import java.util.Collection;
import java.util.Collections;

import org.eclipse.incquery.runtime.matchers.context.IInputKey;
import org.eclipse.incquery.runtime.matchers.context.IQueryRuntimeContext;
import org.eclipse.incquery.runtime.matchers.context.IQueryRuntimeContextListener;
import org.eclipse.incquery.runtime.matchers.tuple.Tuple;
import org.eclipse.incquery.runtime.rete.matcher.ReteEngine;
import org.eclipse.incquery.runtime.rete.network.Direction;
import org.eclipse.incquery.runtime.rete.network.Network;
import org.eclipse.incquery.runtime.rete.network.Receiver;
import org.eclipse.incquery.runtime.rete.network.ReteContainer;
import org.eclipse.incquery.runtime.rete.network.StandardNode;
import org.eclipse.incquery.runtime.rete.network.Supplier;
import org.eclipse.incquery.runtime.rete.remote.Address;

/**
 * An input node representing an enumerable extensional input relation and receiving external updates.
 * 
 * <p> Contains those tuples that are in the extensional relation identified by the input key, and also conform to the global seed (if any).
 * 
 * @author Bergmann Gabor
 *
 */
public class ExternalInputEnumeratorNode extends StandardNode implements Disconnectable, Receiver, IQueryRuntimeContextListener {

	private IQueryRuntimeContext context = null;
	private IInputKey inputKey;
	private Tuple globalSeed;
	private InputConnector inputConnector;
	private Network network;
	private Address<? extends Receiver> myAddress;
	private boolean parallelExecutionEnabled;

	public ExternalInputEnumeratorNode(ReteContainer reteContainer) {
		super(reteContainer);
		this.myAddress = Address.of(this);
		this.network = reteContainer.getNetwork();
		this.inputConnector = network.getInputConnector();
	}
	
	public void connectThroughContext(ReteEngine engine, IInputKey inputKey, Tuple globalSeed) {
		this.inputKey = inputKey;
		this.globalSeed = globalSeed; 
		setTag(inputKey);
		
		final IQueryRuntimeContext context = engine.getRuntimeContext();
		if (!context.getMetaContext().isEnumerable(inputKey))
			throw new IllegalArgumentException(
					this.getClass().getSimpleName() + 
					" only applicable for enumerable input keys; received instead " + 
					inputKey);
		
		this.context = context;
		this.parallelExecutionEnabled = engine.isParallelExecutionEnabled();
		
		engine.addDisconnectable(this);
		context.addUpdateListener(inputKey, globalSeed, this);		
	}
	
	@Override
	public void disconnect() {
		if (context != null) { // if connected
			context.removeUpdateListener(inputKey, globalSeed, this);
			context = null;
		}
	}
	
	@Override
	public void pullInto(Collection<Tuple> collector) {
		if (context != null) { // if connected
			for (Tuple tuple : context.enumerateTuples(inputKey, globalSeed)) {
				collector.add(tuple);
			}
		}
	}

	/* Update from runtime context */
	@Override
	public void update(IInputKey key, Tuple updateTuple, boolean isInsertion) {
		if (parallelExecutionEnabled) {
			// send back to myself as an official external update, and then propagate it transparently
			network.sendExternalUpdate(myAddress, direction(isInsertion), updateTuple);			
		} else {
			// just propagate the input
			propagateUpdate(direction(isInsertion), updateTuple);
			network.waitForReteTermination();
		}
	}

    private static Direction direction(boolean isInsertion) {
    	return isInsertion ? Direction.INSERT : Direction.REVOKE;
    }
    
	/* Self-addressed from network */
	@Override
	public void update(Direction direction, Tuple updateElement) {
		propagateUpdate(direction, updateElement);
	}

	@Override
	public void appendParent(Supplier supplier) {
		throw new UnsupportedOperationException("Input nodes can't have parents");
	}

	@Override
	public void removeParent(Supplier supplier) {
		throw new UnsupportedOperationException("Input nodes can't have parents");
	}

	@Override
	public Collection<Supplier> getParents() {
		return Collections.emptySet();
	}

	public IInputKey getInputKey() {
		return inputKey;
	}

	public Tuple getGlobalSeed() {
		return globalSeed;
	}
	
	

}
