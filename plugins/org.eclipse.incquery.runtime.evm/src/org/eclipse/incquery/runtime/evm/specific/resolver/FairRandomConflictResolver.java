package org.eclipse.incquery.runtime.evm.specific.resolver;


import java.util.Random;

import org.eclipse.incquery.runtime.evm.api.Activation;
import org.eclipse.incquery.runtime.evm.api.resolver.ConflictResolver;
import org.eclipse.incquery.runtime.evm.specific.resolver.impl.RandomAccessConflictSetImpl;

public class FairRandomConflictResolver implements ConflictResolver {
	
	@Override
	public ConflictSetImpl createConflictSet() {
		return new ConflictSetImpl();
	}
	
	final class ConflictSetImpl extends RandomAccessConflictSetImpl {
		Random rnd = new Random();
		
		@Override
		public ConflictResolver getConflictResolver() {
			return FairRandomConflictResolver.this;
		}

		@Override
		public Activation<?> getNextActivation() {
			if (activationList.isEmpty()) return null;
			int index = rnd.nextInt(activationList.size());
			return activationList.get(index);
		}
		
	}
}
