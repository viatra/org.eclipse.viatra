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
package org.eclipse.incquery.runtime.internal.engine;

import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.eclipse.incquery.runtime.api.AdvancedIncQueryEngine;
import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.incquery.runtime.api.IncQueryEngineLifecycleListener;
import org.eclipse.incquery.runtime.api.IncQueryMatcher;

public final class LifecycleProvider extends ListenerContainer<IncQueryEngineLifecycleListener> implements IncQueryEngineLifecycleListener{

        private final Logger logger;

        /**
         * @param incQueryEngine
         */
        public LifecycleProvider(AdvancedIncQueryEngine incQueryEngine, Logger logger) {
            this.logger = logger;
        }

        @Override
        protected void listenerAdded(IncQueryEngineLifecycleListener listener) {
            logger.debug("Lifecycle listener " + listener + " added to engine.");
        }

        @Override
        protected void listenerRemoved(IncQueryEngineLifecycleListener listener) {
            logger.debug("Lifecycle listener " + listener + " removed from engine.");
        }

//        public void propagateEventToListeners(Predicate<IncQueryEngineLifecycleListener> function) {
//            if (!listeners.isEmpty()) {
//                for (IncQueryEngineLifecycleListener listener : new ArrayList<IncQueryEngineLifecycleListener>(listeners)) {
//                    try {
//                        function.apply(listener);
//                    } catch (Exception ex) {
//                        logger.error(
//                                "EMF-IncQuery encountered an error in delivering notification to listener "
//                                        + listener + ".", ex);
//                    }
//                }
//            }
//        }
        
        @Override
        public void matcherInstantiated(final IncQueryMatcher<? extends IPatternMatch> matcher) {
            if (!listeners.isEmpty()) {
                for (IncQueryEngineLifecycleListener listener : new ArrayList<IncQueryEngineLifecycleListener>(listeners)) {
                    try {
                        listener.matcherInstantiated(matcher);
                    } catch (Exception ex) {
                        logger.error(
                                "EMF-IncQuery encountered an error in delivering matcher initialization notification to listener "
                                        + listener + ".", ex);
                    }
                }
            }
//            propagateEventToListeners(new Predicate<IncQueryEngineLifecycleListener>() {
//               public boolean apply(IncQueryEngineLifecycleListener listener) {
//                   listener.matcherInstantiated(matcher);
//                   return true;
//               }
//            });
        }

        @Override
        public void engineBecameTainted(String description, Throwable t) {
            if (!listeners.isEmpty()) {
                for (IncQueryEngineLifecycleListener listener : new ArrayList<IncQueryEngineLifecycleListener>(listeners)) {
                    try {
                        listener.engineBecameTainted(description, t);
                    } catch (Exception ex) {
                        logger.error(
                                "EMF-IncQuery encountered an error in delivering engine tainted notification to listener "
                                        + listener + ".", ex);
                    }
                }
            }
//            propagateEventToListeners(new Predicate<IncQueryEngineLifecycleListener>() {
//                public boolean apply(IncQueryEngineLifecycleListener listener) {
//                    listener.engineBecameTainted();
//                    return true;
//                }
//             });
        }

        @Override
        public void engineWiped() {
            if (!listeners.isEmpty()) {
                for (IncQueryEngineLifecycleListener listener : new ArrayList<IncQueryEngineLifecycleListener>(listeners)) {
                    try {
                        listener.engineWiped();
                    } catch (Exception ex) {
                        logger.error(
                                "EMF-IncQuery encountered an error in delivering engine wiped notification to listener "
                                        + listener + ".", ex);
                    }
                }
            }
//            propagateEventToListeners(new Predicate<IncQueryEngineLifecycleListener>() {
//                public boolean apply(IncQueryEngineLifecycleListener listener) {
//                    listener.engineWiped();
//                    return true;
//                }
//             });
        }

        @Override
        public void engineDisposed() {
            if (!listeners.isEmpty()) {
                for (IncQueryEngineLifecycleListener listener : new ArrayList<IncQueryEngineLifecycleListener>(listeners)) {
                    try {
                        listener.engineDisposed();
                    } catch (Exception ex) {
                        logger.error(
                                "EMF-IncQuery encountered an error in delivering engine disposed notification to listener "
                                        + listener + ".", ex);
                    }
                }
            }
//            propagateEventToListeners(new Predicate<IncQueryEngineLifecycleListener>() {
//                public boolean apply(IncQueryEngineLifecycleListener listener) {
//                    listener.engineDisposed();
//                    return true;
//                }
//             });
        }
        
    }