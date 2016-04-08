/*******************************************************************************
 * Copyright (c) 2010-2016, Andras Szabolcs Nagy and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 *   Andras Szabolcs Nagy - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.dse.base;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.eclipse.viatra.dse.api.DSETransformationRule;
import org.eclipse.viatra.dse.designspace.api.ITransition;

public enum DseIdPoolHelper {

    INSTANCE;

    public static class IdProvider {

        private final DSETransformationRule<?, ?> rule;
        private List<ITransition> trajectory;

        public IdProvider(ThreadContext context, DSETransformationRule<?, ?> rule) {
            this.rule = rule;

            trajectory = context.getDesignSpaceManager().getTrajectoryInfo().getTransitionTrajectory();
        }

        public int getId() {
            int nextId = 0;
            for (ITransition t : trajectory) {
                if (t.getTransitionMetaData().rule.equals(this.rule)) {
                    nextId++;
                }
            }
            return nextId;
        }

    }

    private ConcurrentHashMap<Thread, IdProvider> idProviders = new ConcurrentHashMap<>();
    private AtomicInteger fallBackId = new AtomicInteger();

    public int getId(DSETransformationRule<?, ?> rule) {
        Thread currentThread = Thread.currentThread();
        IdProvider idProvider = idProviders.get(currentThread);
        if (idProvider == null) {
            return fallBackId.getAndIncrement();
        }
        return idProvider.getId();
    }

    public void registerRules(ThreadContext context) {
        Thread currentThread = Thread.currentThread();
        for (DSETransformationRule<?, ?> rule : context.getGlobalContext().getTransformations()) {
            IdProvider idProvider = new IdProvider(context, rule);
            idProviders.put(currentThread, idProvider);
        }
    }

    public void disposeByThread() {
        Thread currentThread = Thread.currentThread();
        idProviders.remove(currentThread);
    }

    public void resetFallBackId() {
        fallBackId.set(0);
    }
}
