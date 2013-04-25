/*******************************************************************************
 * Copyright (c) 2010-2013, Abel Hegedus, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Abel Hegedus - initial API and implementation
 *******************************************************************************/
package org.eclipse.incquery.runtime.evm.api.event;

/**
 * Basic interface that represents a single data transfer object 
 * 
 * @author Abel Hegedus
 *
 */
public interface Atom {

    /**
     * Used for filtering
     * 
     * @param other
     * @return true, if this Atom is compatible with the other Atom
     */
    boolean isCompatibleWith(Atom other);

    /**
     * @return
     */
    boolean isMutable();
    
}
