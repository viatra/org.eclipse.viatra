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
package org.eclipse.viatra.query.runtime.internal.engine;

import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.eclipse.viatra.query.runtime.api.AdvancedViatraQueryEngine;
import org.eclipse.viatra.query.runtime.api.IPatternMatch;
import org.eclipse.viatra.query.runtime.api.ViatraQueryEngineLifecycleListener;
import org.eclipse.viatra.query.runtime.api.ViatraQueryMatcher;

public final class LifecycleProvider extends ListenerContainer<ViatraQueryEngineLifecycleListener> implements ViatraQueryEngineLifecycleListener{

        private final Logger logger;

        /**
         * @param queryEngine
         */
        public LifecycleProvider(AdvancedViatraQueryEngine queryEngine, Logger logger) {
            this.logger = logger;
        }

        @Override
        protected void listenerAdded(ViatraQueryEngineLifecycleListener listener) {
            logger.debug("Lifecycle listener " + listener + " added to engine.");
        }

        @Override
        protected void listenerRemoved(ViatraQueryEngineLifecycleListener listener) {
            logger.debug("Lifecycle listener " + listener + " removed from engine.");
        }

//        public void propagateEventToListeners(Predicate<ViatraQueryEngineLifecycleListener> function) {
//            if (!listeners.isEmpty()) {
//                for (ViatraQueryEngineLifecycleListener listener : new ArrayList<ViatraQueryEngineLifecycleListener>(listeners)) {
//                    try {
//                        function.apply(listener);
//                    } catch (Exception ex) {
//                        logger.error(
//                                "VIATRA Query encountered an error in delivering notification to listener "
//                                        + listener + ".", ex);
//                    }
//                }
//            }
//        }
        
        @Override
        public void matcherInstantiated(final ViatraQueryMatcher<? extends IPatternMatch> matcher) {
            if (!listeners.isEmpty()) {
                for (ViatraQueryEngineLifecycleListener listener : new ArrayList<ViatraQueryEngineLifecycleListener>(listeners)) {
                    try {
                        listener.matcherInstantiated(matcher);
                    } catch (Exception ex) {
                        logger.error(
                                "VIATRA Query encountered an error in delivering matcher initialization notification to listener "
                                        + listener + ".", ex);
                    }
                }
            }
//            propagateEventToListeners(new Predicate<ViatraQueryEngineLifecycleListener>() {
//               public boolean apply(ViatraQueryEngineLifecycleListener listener) {
//                   listener.matcherInstantiated(matcher);
//                   return true;
//               }
//            });
        }

        @Override
        public void engineBecameTainted(String description, Throwable t) {
            if (!listeners.isEmpty()) {
                for (ViatraQueryEngineLifecycleListener listener : new ArrayList<ViatraQueryEngineLifecycleListener>(listeners)) {
                    try {
                        listener.engineBecameTainted(description, t);
                    } catch (Exception ex) {
                        logger.error(
                                "VIATRA Query encountered an error in delivering engine tainted notification to listener "
                                        + listener + ".", ex);
                    }
                }
            }
//            propagateEventToListeners(new Predicate<ViatraQueryEngineLifecycleListener>() {
//                public boolean apply(ViatraQueryEngineLifecycleListener listener) {
//                    listener.engineBecameTainted();
//                    return true;
//                }
//             });
        }

        @Override
        public void engineWiped() {
            if (!listeners.isEmpty()) {
                for (ViatraQueryEngineLifecycleListener listener : new ArrayList<ViatraQueryEngineLifecycleListener>(listeners)) {
                    try {
                        listener.engineWiped();
                    } catch (Exception ex) {
                        logger.error(
                                "VIATRA Query encountered an error in delivering engine wiped notification to listener "
                                        + listener + ".", ex);
                    }
                }
            }
//            propagateEventToListeners(new Predicate<ViatraQueryEngineLifecycleListener>() {
//                public boolean apply(ViatraQueryEngineLifecycleListener listener) {
//                    listener.engineWiped();
//                    return true;
//                }
//             });
        }

        @Override
        public void engineDisposed() {
            if (!listeners.isEmpty()) {
                for (ViatraQueryEngineLifecycleListener listener : new ArrayList<ViatraQueryEngineLifecycleListener>(listeners)) {
                    try {
                        listener.engineDisposed();
                    } catch (Exception ex) {
                        logger.error(
                                "VIATRA Query encountered an error in delivering engine disposed notification to listener "
                                        + listener + ".", ex);
                    }
                }
            }
//            propagateEventToListeners(new Predicate<ViatraQueryEngineLifecycleListener>() {
//                public boolean apply(ViatraQueryEngineLifecycleListener listener) {
//                    listener.engineDisposed();
//                    return true;
//                }
//             });
        }
        
    }