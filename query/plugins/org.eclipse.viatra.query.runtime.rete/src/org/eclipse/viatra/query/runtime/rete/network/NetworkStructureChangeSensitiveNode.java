/*******************************************************************************
 * Copyright (c) 2010-2019, Tamas Szabo, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Tamas Szabo - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.runtime.rete.network;

import org.eclipse.viatra.query.runtime.rete.network.communication.CommunicationTracker;

/**
 * {@link Node}s implementing this interface are sensitive to changes in the 
 * dependency graph maintained by the {@link CommunicationTracker}.
 * The {@link CommunicationTracker} notifies these nodes whenever the 
 * SCC of this node is affected by changes to the dependency graph. 
 * Depending on whether this node is contained in a recursive group or not, 
 * it may behave differently, and the {@link NetworkStructureChangeSensitiveNode#networkStructureChanged()} method 
 * can be used to perform changes in behavior.
 * 
 * @author Tamas Szabo
 * @since 2.2
 */
public interface NetworkStructureChangeSensitiveNode extends Node {
    
    /**
     * At the time of the invocation, the dependency graph has already been updated.  
     */
    public void networkStructureChanged();

}
