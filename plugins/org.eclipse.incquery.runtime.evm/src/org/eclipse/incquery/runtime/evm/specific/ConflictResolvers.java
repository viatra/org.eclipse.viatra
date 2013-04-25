/*******************************************************************************
 * Copyright (c) 2010-2013, Abel Hegedus, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Abel Hegedus - initial API and implementation
 *******************************************************************************/
package org.eclipse.incquery.runtime.evm.specific;

import java.util.Comparator;

import org.eclipse.incquery.runtime.evm.api.Activation;
import org.eclipse.incquery.runtime.evm.specific.resolver.ArbitraryOrderConflictResolver;
import org.eclipse.incquery.runtime.evm.specific.resolver.ComparingConflictResolver;
import org.eclipse.incquery.runtime.evm.specific.resolver.FixedPriorityConflictResolver;

/**
 * @author Abel Hegedus
 *
 */
public final class ConflictResolvers {

    /**
     * 
     */
    private ConflictResolvers() {
    }
    
    public static ArbitraryOrderConflictResolver createArbitraryResolver() {
        return new ArbitraryOrderConflictResolver();
    }
    
    public static FixedPriorityConflictResolver createFixedPriorityResolver() {
        return new FixedPriorityConflictResolver();
    }
    
    public static ComparingConflictResolver createComparingResolver(Comparator<Activation> comparator) {
        return new ComparingConflictResolver(comparator);
    }
    
    // TODO LIFO
    
    // TODO state-based buckets
}
