/*******************************************************************************
 * Copyright (c) 2004-2015, Peter Lunk, Zoltan Ujhelyi and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Peter Lunk - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.transformation.debug;

import java.util.Iterator;
import java.util.Set;

import org.eclipse.viatra.transformation.debug.controller.IDebugController;
import org.eclipse.viatra.transformation.evm.api.Activation;
import org.eclipse.viatra.transformation.evm.api.ConflictSetIterator;
import org.eclipse.viatra.transformation.evm.api.adapter.AbstractEVMAdapter;
import org.eclipse.viatra.transformation.evm.api.resolver.ChangeableConflictSet;
import org.eclipse.viatra.transformation.evm.api.resolver.ConflictResolver;

import com.google.common.collect.Sets;

/**
 * Adapter implementation that enables the user to define the execution order of conflicting rule activations
 * 
 * @author Peter Lunk
 *
 */
public class ManualConflictResolver extends AbstractEVMAdapter{
    private IDebugController ui;
    
    public ManualConflictResolver(IDebugController usedUI){
        ui = usedUI;
    }
    
    
    @Override
    public ChangeableConflictSet getConflictSet(ChangeableConflictSet set) {
    	return new ManualConflictResolverConflictSet(set);
    }
    
    
    @Override
    public Iterator<Activation<?>> getExecutableActivations(Iterator<Activation<?>> iterator) {
    	if(iterator instanceof ConflictSetIterator){
    		return iterator;
    	}else{
    		return new ManualConflictResolverIterator(iterator);
    	}
    	
    }
    
    public class ManualConflictResolverConflictSet implements ChangeableConflictSet{
    	private final ChangeableConflictSet delegatedConflictSet;
    	
    	
    	public ManualConflictResolverConflictSet(ChangeableConflictSet delegatedConflictSet) {
			this.delegatedConflictSet = delegatedConflictSet;
		}
    	
		@Override
		public Activation<?> getNextActivation() {
			Set<Activation<?>> nextActivations = Sets.newHashSet(delegatedConflictSet.getNextActivations());
			if(nextActivations.size() > 0){
				return getActivation(nextActivations);
			}else{
				return null;
			}
		}

		@Override
		public Set<Activation<?>> getNextActivations() {
			return delegatedConflictSet.getNextActivations();
		}

		@Override
		public Set<Activation<?>> getConflictingActivations() {
			return delegatedConflictSet.getConflictingActivations();
		}

		@Override
		public ConflictResolver getConflictResolver() {
			return delegatedConflictSet.getConflictResolver();
		}

		@Override
		public boolean addActivation(Activation<?> activation) {
			return delegatedConflictSet.addActivation(activation);
		}

		@Override
		public boolean removeActivation(Activation<?> activation) {
			return delegatedConflictSet.removeActivation(activation);
		}
    	
    }
    
    
    public class ManualConflictResolverIterator implements Iterator<Activation<?>>{
    	private final Set<Activation<?>> activations = Sets.newHashSet();
    	
    	public ManualConflictResolverIterator(Iterator<Activation<?>> delegatedIterator){
    		while(delegatedIterator.hasNext()){
    			activations.add(delegatedIterator.next());
    		}
    	}
    	
		@Override
		public boolean hasNext() {
			return !activations.isEmpty();
		}

		@Override
		public Activation<?> next() {
			return getActivation(activations);
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException("Deletion from this iterator is not supported.");
			
		}
    	
    }
    
    private Activation<?> getActivation(Set<Activation<?>> activations){
		ui.displayConflictingActivations(activations);
        Activation<?> activation  = ui.getSelectedActivation();
       
        if(activation==null){
        	activation = activations.iterator().next();
        }
        activations.remove(activation);
		return activation;
    }
}
