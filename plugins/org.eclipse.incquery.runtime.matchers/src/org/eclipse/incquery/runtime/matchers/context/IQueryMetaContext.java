/*******************************************************************************
 * Copyright (c) 2010-2015, Bergmann Gabor, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Bergmann Gabor - initial API and implementation
 *******************************************************************************/
package org.eclipse.incquery.runtime.matchers.context;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * Provides metamodel information (relationship of input keys) to query evaluator backends at runtime and at query planning time.
 * 
 * @author Bergmann Gabor
 *
 */
public interface IQueryMetaContext {
	
	/**
	 * Returns known implications, e.g. edge supertypes, edge opposites, node type constraints, etc.
	 */
	Collection<InputKeyImplication> getImplications(IInputKey implyingKey);
	
	/**
	 * Returns functional dependencies of the input key expressed in terms of column indices.
	 * 
	 * <p> Each entry of the map is a functional dependency rule, where the entry key specifies source columns and the entry value specifies target columns. 
	 */
	Map<Set<Integer>, Set<Integer>> getFunctionalDependencies(IInputKey key);
	
}
