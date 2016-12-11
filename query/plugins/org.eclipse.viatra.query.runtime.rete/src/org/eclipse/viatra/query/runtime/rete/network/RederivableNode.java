/*******************************************************************************
 * Copyright (c) 2010-2016, Tamas Szabo, Istvan Rath and Daniel Varro
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
 * A rederivable node can potentially re-derive tuples after the Rete network has finished the delivery of messages.  
 * 
 * @author Tamas Szabo
 */
public interface RederivableNode {

    /**
     * The method is called by the {@link ReteContainer} to re-derive tuples after the normal messages have been 
     * delivered and consumed. The re-derivation process may trigger the creation and delivery of further messages 
     * and further re-derivation rounds.  
     */
    public void rederiveOne();
    
}
