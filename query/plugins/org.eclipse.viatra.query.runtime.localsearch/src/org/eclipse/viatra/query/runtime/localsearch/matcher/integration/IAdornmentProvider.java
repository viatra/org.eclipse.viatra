/*******************************************************************************
 * Copyright (c) 2010-2016, Grill Balázs, IncQueryLabs Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Grill Balázs - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.runtime.localsearch.matcher.integration;

import java.util.Set;

import org.eclipse.viatra.query.runtime.matchers.psystem.queries.PParameter;
import org.eclipse.viatra.query.runtime.matchers.psystem.queries.PQuery;

/**
 * An adornment provider is used to define the adornments the pattern matcher should prepare for. 
 * A default implementation is available in {@link AllValidAdornments} that describes all 
 * adornments fulfilling the parameter direction declarations.
 * 
 * <br><br>
 * 
 * Users may implement this interface to limit the number of prepared plans based on some runtime information:
 * 
 * <pre>
 * class SomeAdornments{
 * 
 *     public Iterable<Set<{@link PParameter}>> getAdornments({@link PQuery} query){
 *         if (SomeGeneratedQuerySpecification.instance().getInternalQueryRepresentation().equals(query)){
 *             return Collections.singleton(Sets.filter(Sets.newHashSet(query.getParameters()), new Predicate<PParameter>() {
 *
 *                  &#64;Override
 *                  public boolean apply(PParameter input) {
 *                      // Decide whether this particular parameter will be bound
 *                      return false;
 *                  }
 *              }));
 *         }
 *         // Returning an empty iterable is safe for unknown queries
 *         return Collections.emptySet();
 *     }
 * 
 * }
 * </pre>
 * 
 * @author Grill Balázs
 * @since 1.5
 *
 */
public interface IAdornmentProvider {

    /**
     * The bound parameter sets
     * 
     * @param query
     * @return
     */
    public Iterable<Set<PParameter>> getAdornments(PQuery query);
    
}
