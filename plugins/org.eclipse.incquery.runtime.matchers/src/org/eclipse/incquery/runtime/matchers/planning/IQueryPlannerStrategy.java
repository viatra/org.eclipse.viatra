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

package org.eclipse.incquery.runtime.matchers.planning;

import org.eclipse.incquery.runtime.matchers.IPatternMatcherContext;
import org.eclipse.incquery.runtime.matchers.psystem.PBody;

/**
 * An algorithm that builds a matcher based on a PSystem.
 * 
 * @author Gabor Bergmann
 */
public interface IQueryPlannerStrategy {
    public SubPlan layout(PBody pSystem, IOperationCompiler compiler, IPatternMatcherContext context) throws QueryPlannerException;
}
