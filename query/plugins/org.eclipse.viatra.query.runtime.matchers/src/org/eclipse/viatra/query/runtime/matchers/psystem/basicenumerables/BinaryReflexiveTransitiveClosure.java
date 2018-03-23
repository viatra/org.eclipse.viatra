/*******************************************************************************
 * Copyright (c) 2004-2010 Zoltan Ujhelyi, Gabor Bergmann, IncQuery Labs Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Gabor Bergmann, Zoltan Ujhelyi - initial API and implementation
 *******************************************************************************/

package org.eclipse.viatra.query.runtime.matchers.psystem.basicenumerables;

import org.eclipse.viatra.query.runtime.matchers.context.IInputKey;
import org.eclipse.viatra.query.runtime.matchers.planning.QueryProcessingException;
import org.eclipse.viatra.query.runtime.matchers.psystem.PBody;
import org.eclipse.viatra.query.runtime.matchers.psystem.queries.PQuery;
import org.eclipse.viatra.query.runtime.matchers.tuple.Tuple;

/**
 * For a binary base pattern over an enumerable universe type, computes the reflexive transitive closure (base)*
 * 
 * @author Gabor Bergmann, Zoltan Ujhelyi
 * @since 2.0
 */
public class BinaryReflexiveTransitiveClosure extends AbstractTransitiveClosure {

    private final IInputKey universeType;

    public BinaryReflexiveTransitiveClosure(PBody pBody, Tuple variablesTuple,
            PQuery pattern, IInputKey universeType) {
        super(pBody, variablesTuple, pattern);
        this.universeType = universeType;
    }

    @Override
    protected String keyToString() {
        return supplierKey.getFullyQualifiedName() + "*";
    }

    /**
     * Returns the type whose instances should be returned as 0-long paths. 
     * @since 2.0
     */
    public IInputKey getUniverseType() {
        return universeType;
    }

    @Override
    public void checkSanity() {
        if (!universeType.isEnumerable() || universeType.getArity() != 1) {
            throw new QueryProcessingException(
                    String.format("Invalid universe type %s - it should be enumerable and must have an arity of 1.",
                            universeType.getPrettyPrintableName()),
                    pBody.getPattern());
        }
    }
    
}
