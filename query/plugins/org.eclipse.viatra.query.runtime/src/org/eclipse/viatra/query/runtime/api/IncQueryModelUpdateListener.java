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
package org.eclipse.viatra.query.runtime.api;



/**
 * Listener interface for model changes affecting different levels of the IncQuery architecture.
 * 
 * @author Abel Hegedus
 *
 */
public interface IncQueryModelUpdateListener {

    /** 
     * Possible notification levels for changes
     * 
     * @author Abel Hegedus
     *
     */
    enum ChangeLevel {
        NO_CHANGE, MODEL, INDEX, MATCHSET;
        
        public ChangeLevel changeOccured(ChangeLevel occuredLevel) {
            if(this.compareTo(occuredLevel) < 0) {
                return occuredLevel;
            } else {
                return this;
            }
        }
    }
    /**
     * Called after each change with also sending the level of change.
     * Only called if the change level is at least at the level returned by getLevel().
     * 
     * @param changeLevel
     */
    void notifyChanged(ChangeLevel changeLevel);
    
    /**
     * This may be queried only ONCE (!!!) at the registration of the listener.
     * 
     * NOTE: this allows us to only create engine level change providers if there is someone who needs it.
     * 
     * @return the change level where you want notifications
     */
    ChangeLevel getLevel();
    
}
