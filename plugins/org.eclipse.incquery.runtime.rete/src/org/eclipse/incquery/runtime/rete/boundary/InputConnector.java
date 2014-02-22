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
package org.eclipse.incquery.runtime.rete.boundary;

import org.eclipse.incquery.runtime.matchers.tuple.Tuple;
import org.eclipse.incquery.runtime.rete.network.Network;
import org.eclipse.incquery.runtime.rete.network.Node;
import org.eclipse.incquery.runtime.rete.network.Receiver;
import org.eclipse.incquery.runtime.rete.recipes.BinaryInputRecipe;
import org.eclipse.incquery.runtime.rete.recipes.InputRecipe;
import org.eclipse.incquery.runtime.rete.recipes.TypeInputRecipe;
import org.eclipse.incquery.runtime.rete.recipes.UnaryInputRecipe;
import org.eclipse.incquery.runtime.rete.remote.Address;

/**
 * A class responsible for connecting input nodes to the runtime context.
 * 
 * @author Bergmann Gabor
 *
 */
public class InputConnector {
	Network network;

	public InputConnector(Network network) {
		super();
		this.network = network;
	}
	

	public Network getNetwork() {
		return network;
	}


	/**
	 * Connects a given input node to the external input source.
	 */
	public void connectInput(InputRecipe recipe, Node freshNode) {
		final Address<Receiver> freshAddress = Address.of((Receiver)freshNode);
		if (recipe instanceof TypeInputRecipe) {
			final Object typeKey = ((TypeInputRecipe) recipe).getTypeKey();
			
			if (recipe instanceof UnaryInputRecipe) {
				new EntityFeeder(freshAddress, this, typeKey).feed();
//		        if (typeObject != null && generalizationQueryDirection == GeneralizationQueryDirection.BOTH) {
//		            Collection<? extends Object> subTypes = context.enumerateDirectUnarySubtypes(typeObject);
		//
//		            for (Object subType : subTypes) {
//		                Address<? extends Tunnel> subRoot = accessUnaryRoot(subType);
//		                network.connectRemoteNodes(subRoot, tn, true);
//		            }
//		        }
			} else if (recipe instanceof BinaryInputRecipe) {
				new RelationFeeder(freshAddress, this, typeKey).feed();
				//        if (typeObject != null && generalizationQueryDirection == GeneralizationQueryDirection.BOTH) {
				//            Collection<? extends Object> subTypes = context.enumerateDirectTernaryEdgeSubtypes(typeObject);
				//
				//            for (Object subType : subTypes) {
				//                Address<? extends Tunnel> subRoot = accessTernaryEdgeRoot(subType);
				//                network.connectRemoteNodes(subRoot, tn, true);
				//            }
				//        }
			}
			
			
		}
		
	}
	
    /**
     * Wraps the element into a form suitable for entering the network model element -> internal object
     */
    public Object wrapElement(Object element) {
        return element;// .getID();
    }

    /**
     * Unwraps the element into its original form internal object -> model element
     */
    public Object unwrapElement(Object wrapper) {
        return wrapper;// modelManager.getElementByID((String)
                       // wrapper);
    }

    /**
     * Unwraps the tuple of elements into a form suitable for entering the network
     */
    public Tuple wrapTuple(Tuple unwrapped) {
        // int size = unwrapped.getSize();
        // Object[] elements = new Object[size];
        // for (int i=0; i<size; ++i) elements[i] =
        // wrapElement(unwrapped.get(i));
        // return new FlatTuple(elements);
        return unwrapped;
    }

    /**
     * Unwraps the tuple of elements into their original form
     */
    public Tuple unwrapTuple(Tuple wrappers) {
        // int size = wrappers.getSize();
        // Object[] elements = new Object[size];
        // for (int i=0; i<size; ++i) elements[i] =
        // unwrapElement(wrappers.get(i));
        // return new FlatTuple(elements);
        return wrappers;
    }
	
}
