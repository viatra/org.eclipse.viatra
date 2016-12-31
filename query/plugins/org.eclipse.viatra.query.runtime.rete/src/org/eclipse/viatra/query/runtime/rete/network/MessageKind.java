/*******************************************************************************
 * Copyright (c) 2010-2017, Tamas Szabo, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Tamas Szabo - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.runtime.rete.network;

/**
 * The enum represents the possible kinds of messages in the query backend. 
 * 
 * @author Tamas Szabo
 *
 */
public enum MessageKind {
    
    /**
     * Inserts and delete-insert monotone change pairs
     */
    MONOTONE,
    
    /**
     * Deletes
     */
    ANTI_MONOTONE
    
}
