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

import java.util.Collection;
import java.util.Map;

import org.eclipse.incquery.runtime.matchers.tuple.Tuple;
import org.eclipse.incquery.runtime.rete.collections.CollectionsFactory;
import org.eclipse.incquery.runtime.rete.network.Network;
import org.eclipse.incquery.runtime.rete.network.Node;
import org.eclipse.incquery.runtime.rete.network.Supplier;
import org.eclipse.incquery.runtime.rete.network.Tunnel;
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
	
	
    /*
     * arity:1 used as simple entity constraints label is the object representing the type null label means all entities
     * regardless of type (global supertype), if allowed
     */
    protected Map<Object, Address<? extends Tunnel>> unaryRoots = CollectionsFactory.getMap();
    /*
     * arity:3 (rel, from, to) used as VPM relation constraints null label means all relations regardless of type
     * (global supertype)
     */
    protected Map<Object, Address<? extends Tunnel>> ternaryEdgeRoots = CollectionsFactory.getMap();
    /*
     * arity:2 (from, to) not used over VPM; can be used as EMF references for instance label is the object representing
     * the type null label means all entities regardless of type if allowed (global supertype), if allowed
     */
    protected Map<Object, Address<? extends Tunnel>> binaryEdgeRoots = CollectionsFactory.getMap();
	
    protected Address<? extends Tunnel> containmentRoot = null;
    protected Address<? extends Supplier> containmentTransitiveRoot = null;
    protected Address<? extends Tunnel> instantiationRoot = null;
    protected Address<? extends Supplier> instantiationTransitiveRoot = null;
    protected Address<? extends Tunnel> generalizationRoot = null;
    protected Address<? extends Supplier> generalizationTransitiveRoot = null;
	

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
		final Address<Tunnel> freshAddress = Address.of((Tunnel)freshNode);
		if (recipe instanceof TypeInputRecipe) {
			final Object typeKey = ((TypeInputRecipe) recipe).getTypeKey();
			
			if (recipe instanceof UnaryInputRecipe) {
				unaryRoots.put(typeKey, freshAddress);
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
				binaryEdgeRoots.put(typeKey, freshAddress);
				new ReferenceFeeder(freshAddress, this, typeKey).feed();
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
	
    /**
     * fetches the entity Root node under specified label; returns null if it doesn't exist yet
     */
    public Address<? extends Tunnel> getUnaryRoot(Object label) {
        return unaryRoots.get(label);
    }

    public Collection<Address<? extends Tunnel>> getAllUnaryRoots() {
        return unaryRoots.values();
    }

    /**
     * fetches the relation Root node under specified label; returns null if it doesn't exist yet
     */
    public Address<? extends Tunnel> getTernaryEdgeRoot(Object label) {
        return ternaryEdgeRoots.get(label);
    }

    public Collection<Address<? extends Tunnel>> getAllTernaryEdgeRoots() {
        return ternaryEdgeRoots.values();
    }
    
    /**
     * fetches the reference Root node under specified label; returns null if it doesn't exist yet
     */
    public Address<? extends Tunnel> getBinaryEdgeRoot(Object label) {
        return binaryEdgeRoots.get(label);
    }

    public Collection<Address<? extends Tunnel>> getAllBinaryEdgeRoots() {
        return binaryEdgeRoots.values();
    }


	public Address<? extends Tunnel> getContainmentRoot() {
		return containmentRoot;
	}


	public Address<? extends Supplier> getContainmentTransitiveRoot() {
		return containmentTransitiveRoot;
	}


	public Address<? extends Tunnel> getInstantiationRoot() {
		return instantiationRoot;
	}


	public Address<? extends Supplier> getInstantiationTransitiveRoot() {
		return instantiationTransitiveRoot;
	}


	public Address<? extends Tunnel> getGeneralizationRoot() {
		return generalizationRoot;
	}

    
    
    
    
}
