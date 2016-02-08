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
package org.eclipse.viatra.query.runtime.base.api;

/**
 * Listener interface for change notifications from the IncQuery Base index.
 * 
 * @author Abel Hegedus
 *
 */
public interface EMFBaseIndexChangeListener {

    /**
     * NOTE: it is possible that this method is called only ONCE! Consider returning a constant value that is set in the constructor.
     * 
     * @return true, if the listener should be notified only after index changes, false if notification is needed after each model change 
     */
    public boolean onlyOnIndexChange();
    
    /**
     * Called after a model change is handled by the IncQuery Base index and if <code>indexChanged == onlyOnIndexChange()</code>.
     *  
     * @param indexChanged true, if the model change also affected the contents of the base index
     */
    public void notifyChanged(boolean indexChanged);
    
}
