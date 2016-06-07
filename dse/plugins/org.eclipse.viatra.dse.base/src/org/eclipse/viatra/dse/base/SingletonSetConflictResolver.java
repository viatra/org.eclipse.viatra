package org.eclipse.viatra.dse.base;

import org.eclipse.viatra.transformation.evm.api.resolver.ChangeableConflictSet;
import org.eclipse.viatra.transformation.evm.api.resolver.ConflictResolver;

public class SingletonSetConflictResolver implements ConflictResolver {

    protected ChangeableConflictSet conflictSet;

    public SingletonSetConflictResolver(ConflictResolver resolver) {
        conflictSet = resolver.createConflictSet();
    }

    @Override
    public ChangeableConflictSet createConflictSet() {
        return conflictSet;
    }
    
}