package org.eclipse.incquery.runtime.evm.specific.resolver.impl;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.incquery.runtime.evm.api.Activation;
import org.eclipse.incquery.runtime.evm.api.resolver.ChangeableConflictSet;

public abstract class HashSetBasedConflictSetImpl implements ChangeableConflictSet {
	
	protected Set<Activation<?>> container = new HashSet<Activation<?>>();

	@Override
	public Set<Activation<?>> getConflictingActivations() {
		return Collections.unmodifiableSet(container);
	}

	@Override
	public boolean addActivation(Activation<?> activation) {
		return container.add(activation);
	}

	@Override
	public boolean removeActivation(Activation<?> activation) {
		return container.remove(activation);
	}

	@Override
	public Set<Activation<?>> getNextActivations() {
		return Collections.unmodifiableSet(container);
	}

}
