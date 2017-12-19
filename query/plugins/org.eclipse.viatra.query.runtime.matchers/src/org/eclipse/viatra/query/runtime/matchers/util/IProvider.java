/*******************************************************************************
 * Copyright (c) 2010-2015, Zoltan Ujhelyi, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.runtime.matchers.util;

import java.util.function.Function;
import java.util.function.Supplier;

import org.eclipse.viatra.query.runtime.matchers.psystem.queries.PQuery;

/**
 * A provider interface useful in various registry instances.
 * 
 * @author Zoltan Ujhelyi
 *
 */
public interface IProvider<T> extends Supplier<T>{
    
    public final class ProvidedValueFunction implements Function<IProvider<PQuery>, PQuery> {
        @Override
        public PQuery apply(IProvider<PQuery> input) {
            return (input == null) ? null : input.get();
        }
    }
}
