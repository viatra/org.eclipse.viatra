/*******************************************************************************
 * Copyright (c) 2010-2016, Grill Balázs, IncQueryLabs
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Grill Balázs - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.runtime.matchers.psystem.queries;

/**
 * Values of this enum describe a constraint to the calling of patterns regarding its parameters.
 * 
 * @author Grill Balázs
 * @since 1.4
 *
 */
public enum PParameterDirection {

    /**
     * Default value, no additional constraint is applied
     */
    INOUT,
    
    /**
     * The parameters marked with this constraints shall be set to a value before calling the pattern
     */
    IN,
    
    /**
     * The parameters marked with this constraints shall not be set to a value before calling the pattern
     */
    OUT
    
}
