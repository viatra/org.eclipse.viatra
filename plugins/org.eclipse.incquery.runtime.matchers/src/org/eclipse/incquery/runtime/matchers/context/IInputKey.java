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

/**
 * An input key identifies an input (extensional) relation, such as the instance set of a given node or edge type, or the direct containment relation.
 * 
 * <p> The input key, at the very minimum, is associated with an arity (number of columns), a user-friendly name, and a string identifier (for distributive purposes). 
 * 
 * <p> The input key itself must be an immutable data object that properly overrides equals() and hashCode(). 
 * It must be instantiable without using the query context object, so that query specifications may construct the appropriate PQueries. 
 * 
 * @author Bergmann Gabor
 *
 */
public interface IInputKey {

	/**
	 * A user-friendly name that can be shown on screen for degug purposes, included in exceptions, etc.
	 */
	public String getPrettyPrintableName();
	/**
	 * An internal string identifier that can be used to uniquely identify to input key (relevant for distributed applications).
	 */
	public String getStringID();
	
	/**
	 * The width of tuples in this relation.
	 */
	public int getArity();
	
}
