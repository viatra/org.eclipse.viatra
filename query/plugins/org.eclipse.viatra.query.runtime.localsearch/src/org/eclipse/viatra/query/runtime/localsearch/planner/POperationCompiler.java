/*******************************************************************************
 * Copyright (c) 2010-2014, Marton Bur, Akos Horvath, Zoltan Ujhelyi, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Marton Bur - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.runtime.localsearch.planner;

import org.eclipse.viatra.query.runtime.localsearch.planner.compiler.EMFOperationCompiler;
import org.eclipse.viatra.query.runtime.matchers.context.IQueryRuntimeContext;

/**
 * An EMF specific plan compiler for the local search-based pattern matcher
 * 
 * @author Marton Bur
 * @noreference This class is not intended to be referenced by clients.
 * @deprecated Use the refactored {@link EMFOperationCompiler} instead
 */
@Deprecated
public class POperationCompiler extends EMFOperationCompiler {
    
    public POperationCompiler(IQueryRuntimeContext runtimeContext) {
        super(runtimeContext, false);
    }

    public POperationCompiler(IQueryRuntimeContext runtimeContext, boolean baseIndexAvailable) {
        super(runtimeContext, baseIndexAvailable);
    }
}
