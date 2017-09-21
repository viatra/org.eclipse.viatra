/*******************************************************************************
 * Copyright (c) 2010-2013, Zoltan Ujhelyi, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.runtime.matchers.psystem;

/**
 * Helper interface to get values from a tuple of variables. All pattern matching engines are expected to implement this
 * to handle their internal structures.
 * 
 * @author Zoltan Ujhelyi
 * 
 */
public interface IValueProvider {

    /**
     * Returns the value of the selected variable
     * @param variableName
     * @return the value of the variable; never null
     * @throws IllegalArgumentException if the variable is not defined
     */
    Object getValue(String variableName);
}
