/*******************************************************************************
 * Copyright (c) 2004-2010 Gabor Bergmann and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Gabor Bergmann - initial API and implementation
 *******************************************************************************/

package org.eclipse.incquery.runtime.matchers.psystem;

import java.util.Set;

import org.eclipse.incquery.runtime.matchers.context.IQueryMetaContext;

/**
 * @author Gabor Bergmann
 * 
 */
public interface ITypeInfoProviderConstraint {

    /**
     * Returns type information implied by this constraint.
     * 
     */
    public Set<TypeJudgement> getImpliedJudgements(IQueryMetaContext context);

}
