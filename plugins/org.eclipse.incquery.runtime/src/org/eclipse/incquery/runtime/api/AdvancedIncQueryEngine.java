/*******************************************************************************
 * Copyright (c) 2010-2013, Bergmann Gabor, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Bergmann Gabor - initial API and implementation
 *******************************************************************************/
package org.eclipse.incquery.runtime.api;

import org.apache.log4j.Appender;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.incquery.patternlanguage.patternLanguage.Pattern;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.eclipse.incquery.runtime.rete.matcher.ReteEngine;


/**
 * An EMF-IncQuery incremental evaluation engine.
 * 
 * <p>While the default interface {@link IncQueryEngine}, is suitable for most users, this advanced interface 
 *   provides tighter control over the engine. The most important added functionality is the following: <ul>
 * <li> You have tighter control over the lifecycle of the engine. 
 *   For instance, the engine can be disposed in order to detach from the EMF model and stop listening on update notifications. 
 *   Total lifecycle control is only available for unmanaged engines (see {@link IncQueryEngineManager}).
 * <li> You can add and remove listeners to receive notification when the model or the match sets change, 
 * 	 as well as on engine lifecycle events.
 * </ul>
 * 
 * You can obtain an {@link AdvancedIncQueryEngine} from the {@link IncQueryEngineManager}. 
 * Additionally, you can safely cast any {@link IncQueryEngine} to {@link AdvancedIncQueryEngine}.
 * 
 * @author Bergmann Gabor
 *
 */
public abstract class AdvancedIncQueryEngine extends IncQueryEngine {

    /**
     * Add an engine lifecycle listener to this engine instance.
     * @param listener the {@link IncQueryEngineLifecycleListener} that should listen to lifecycle events from this engine
     */
	public abstract void addLifecycleListener(IncQueryEngineLifecycleListener listener);
    /**
     * Remove an existing lifecycle listener from this engine instance.
     * @param listener the {@link IncQueryEngineLifecycleListener} that should not listen to lifecycle events from this engine anymore
     */
	public abstract void removeLifecycleListener(IncQueryEngineLifecycleListener listener);

    /**
     * Add an model update event listener to this engine instance (that fires its callbacks according to its notification level).
     * @param listener the {@link IncQueryModelUpdateListener} that should listen to model update events from this engine.
     */
	public abstract void addModelUpdateListener(IncQueryModelUpdateListener listener);
    /**
     * Remove an existing model update event listener to this engine instance.
     * @param listener the {@link IncQueryModelUpdateListener} that should not listen to model update events from this engine anymore
     */
	public abstract void removeModelUpdateListener(IncQueryModelUpdateListener listener);
	
	/**
     * Registers low-level callbacks for match appearance and disappearance on this pattern matcher.
     * 
     * <p>
     * This is a low-level callback that is invoked when the pattern matcher is not necessarily in a consistent state
     * yet. Importantly, no model modification permitted during the callback. Most users should use the agenda and trigger engine instead. TODO reference
     * 
     * <p>
     * Performance note: expected to be much more efficient than polling at {@link #addCallbackAfterUpdates(Runnable)},
     * but prone to "signal hazards", e.g. spurious match appearances that will disappear immediately afterwards.
     * 
     * <p>
     * The callback can be unregistered via {@link #removeCallbackOnMatchUpdate(IMatchUpdateListener)}.
     * 
     * @param fireNow
     *            if true, appearCallback will be immediately invoked on all current matches as a one-time effect. See
     *            also {@link IncQueryMatcher#forEachMatch(IMatchProcessor)}.
     * @param listener
     *            the listener that will be notified of each new match that appears or disappears, starting from now.
     * @param matcher 
     *            the {@link IncQueryMatcher} for which this listener should be active
     */
	public abstract <Match extends IPatternMatch> void addMatchUpdateListener(IncQueryMatcher<Match> matcher, IMatchUpdateListener<? super Match> listener,
			boolean fireNow);
    /**
     * Remove an existing match update event listener to this engine instance.
     * @param matcher the {@link IncQueryMatcher} for which this listener should not be active anymore
     * @param listener the {@link IMatchUpdateListener} that should not receive the callbacks anymore
     */
	public abstract <Match extends IPatternMatch> void removeMatchUpdateListener(IncQueryMatcher<Match> matcher, IMatchUpdateListener<? super Match> listener);

    /**
     * Indicates whether the engine is managed by {@link IncQueryEngineManager}.
     * 
     * <p>
     * If the engine is managed, there may be other clients using it. Care should be taken with {@link #wipe()} and
     * {@link #dispose()}. Register a callback using {@link IncQueryMatcher#addCallbackAfterWipes(Runnable)} or directly
     * at {@link #getAfterWipeCallbacks()} to learn when a client has called these dangerous methods.
     * 
     * @return true if the engine is managed, and therefore potentially shared with other clients querying the same EMF
     *         model
     */
	public abstract boolean isManaged();

    /**
     * Indicates whether the engine is in a tainted, inconsistent state due to some internal errors. If true, results
     * are no longer reliable; engine should be disposed.
     * 
     * <p>
     * The engine is defined to be in a tainted state if any of its internal processes has logged a
     * <strong>fatal</strong> error to the engine's logger. The cause of the error can therefore be determined by
     * checking the contents of the log. This is possible e.g. through a custom {@link Appender} that was attached to
     * the engine's logger.
     * 
     * @return the tainted state
     */
	public abstract boolean isTainted();

    /**
     * Discards any pattern matcher caches and forgets known patterns. The base index built directly on the underlying
     * EMF model, however, is kept in memory to allow reuse when new pattern matchers are built. Use this method if you
     * have e.g. new versions of the same patterns, to be matched on the same model.
     * 
     * <p>
     * Matcher objects will continue to return stale results. If no references are retained to the matchers, they can
     * eventually be GC'ed.
     * <p>
     * If the engine is managed (see {@link #isManaged()}), there may be other clients using it. Care should be taken
     * with wiping such engines.
     * 
     */
	public abstract void wipe();

    /**
     * Completely disconnects and dismantles the engine.
     * <p>
     * Matcher objects will continue to return stale results. If no references are retained to the matchers or the
     * engine, they can eventually be GC'ed, and they won't block the EMF model from being GC'ed anymore.
     * 
     * <p>
     * Cannot be reversed.
     * <p>
     * If the engine is managed (see {@link #isManaged()}), there may be other clients using it. Care should be taken
     * with disposing such engines.
     */
	public abstract void dispose();

	/**
	 * Internal method for asking the {@link IncQueryEngineManager} to internally remove the engine.
	 * @noreference for internal use only
	 */
	protected static void managerKillInternal(IncQueryEngineManager manager, Notifier emfRoot) {
		manager.killInternal(emfRoot);
	}

	/**
	 * Access the internal Rete pattern matching network (for advanced debugging purposes only).
	 * @noreference for internal use only
	 */
	public abstract ReteEngine<Pattern> getReteEngine() throws IncQueryException;
	
}
