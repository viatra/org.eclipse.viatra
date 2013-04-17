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
package org.eclipse.incquery.runtime.api;


/**
 * Listener interface for getting notification on changes in an {@link IncQueryEngine}.
 * 
 * You can use 
 * 
 * @author Abel Hegedus
 *
 */
public interface IncQueryEngineLifecycleListener {

    // -----------------------------------------------------------------------
    // UPDATE (methods notifying on updates in the model, cache or match sets)
    // -----------------------------------------------------------------------
    /**
     * Called after a change notification is received by the base index of the {@link IncQueryEngine}
     */
    void modelChanged();
    
    /**
     * Called after a change notification affected the caches of the base index of the {@link IncQueryEngine}
     */
    void indexChanged();
    
    /**
     * Called after a change notification affected the match set of at least one matcher
     */
    void matchSetChanged();
    
    // ALTERNATIVE FOR UPDATE:
    /**
     * Called after each change notification
     * 
     * @param indexChanged true, if the base index changed
     * @param matchSetChanged true, if the match set changed
     */
    void notifyChanged(boolean indexChanged, boolean matchSetChanged);

    // ALTERNATIVE FOR UPDATE: 
    /** 
     * Possible notification levels for changes
     * 
     * @author Abel Hegedus
     *
     */
    enum ChangeLevel{
        MODEL, INDEX, MATCHSET
    }
    /**
     * Called after each change with also sending the level of change
     * 
     * @param changeLevel
     */
    void notifyChanged(ChangeLevel changeLevel);
    
    // ALTERNATIVE FOR UPDATE:
    /**
     * This is queried ONCE (!!!) at the registration of the listener.
     * 
     * NOTE: this allows us to only create engine level change providers if there is someone who needs it.
     * 
     * @return the change level where you want notifications
     */
    ChangeLevel getLevel();
    
    /**
     * Only called if the change level is at least at the level returned by getLevel() 
     */
    void notifyChanged();
    
    
    // -------------------------------------------------------------------------------
    // MATCHERS (methods notifying on changes in the matchers available in the engine)
    // -------------------------------------------------------------------------------
    
    /**
     * Called after a matcher is instantiated in the engine
     *  
     * @param matcher the new matcher
     */
    void matcherInstantiated(IncQueryMatcher<? extends IPatternMatch> matcher);
    
    // -------------------------------------------------------------------------
    // HEALTH (methods notifying on changes that affect the health of the engine
    // -------------------------------------------------------------------------
    
    /**
     * Called after the engine has become tainted due to a fatal error
     */
    void engineBecameTainted();
    
    /**
     * Called after the engine has been wiped
     */
    void engineWiped();
    
    /**
     * Called before the engine is disposed
     */
    void engineDisposeCalled();
}
