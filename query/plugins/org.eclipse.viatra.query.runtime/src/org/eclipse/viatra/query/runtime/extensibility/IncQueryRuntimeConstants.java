/*******************************************************************************
 * Copyright (c) 2010-2015, Abel Hegedus, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Abel Hegedus - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.runtime.extensibility;

/**
 * Utility class for IncQuery runtime constants, such as extension point identifiers.
 * 
 * @author Abel Hegedus
 *
 */
public final class IncQueryRuntimeConstants {

    /**
     * Constructor hidden for utility class
     */
    protected IncQueryRuntimeConstants() {
        // TODO Auto-generated constructor stub
    }
    
    // Surrogate query extension    
    
    // FIXME remove when deprecated extension point is removed
    public static final String SURROGATE_QUERY_DEPRECATED_EXTENSIONID = "org.eclipse.incquery.patternlanguage.emf.surrogatequeryemf";
    public static final String SURROGATE_QUERY_EXTENSIONID = "org.eclipse.viatra.query.runtime.surrogatequeryemf";

    
    
}
