/*******************************************************************************
 * Copyright (c) 2010-2013 Gabor Bergmann, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Gabor Bergmann - initial API and implementation
 *******************************************************************************/
package org.eclipse.incquery.runtime.evm.specific.resolver;

import java.util.Collection;
import java.util.Collections;

import org.eclipse.incquery.runtime.evm.api.Activation;
import org.eclipse.incquery.runtime.evm.api.resolver.ConflictResolver;
import org.eclipse.incquery.runtime.evm.specific.resolver.impl.HashSetBasedConflictSetImpl;

public class CallbackConflictResolver implements ConflictResolver {
	
	public interface ActivationChoiceStrategy {
		public Activation<?> selectNextActivation(Collection<Activation<?>> activations);
	}
	
	ActivationChoiceStrategy chooser;
	
	public CallbackConflictResolver(ActivationChoiceStrategy chooser) {
		super();
		this.chooser = chooser;
	}

	public ActivationChoiceStrategy getChooser() {
		return chooser;
	}	
	protected void setChooser(ActivationChoiceStrategy chooser) {
		this.chooser = chooser;
	}

	@Override
	public ConflictSetImpl createConflictSet() {
		return new ConflictSetImpl();
	}
	
	final class ConflictSetImpl extends HashSetBasedConflictSetImpl {
		
		@Override
		public ConflictResolver getConflictResolver() {
			return CallbackConflictResolver.this;
		}

		@Override
		public Activation<?> getNextActivation() {
			return container.isEmpty() ? 
					null : 
					chooser.selectNextActivation(Collections.unmodifiableSet(container));
		}
		
	}


}
