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

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.eclipse.viatra.transformation.runtime.emf.rules.batch.BatchTransformationRule;

public enum DseIdPoolHelper {

    INSTANCE;

    public static class IdProvider {

        private final BatchTransformationRule<?, ?> rule;
        private List<BatchTransformationRule<?, ?>> rulesTrajectory;

        public IdProvider(ThreadContext context, BatchTransformationRule<?, ?> rule) {
            this.rule = rule;

            rulesTrajectory = context.getDesignSpaceManager().getTrajectoryInfo().getRules();
        }

        public int getId() {
            int nextId = 0;
            for (BatchTransformationRule<?, ?> r : rulesTrajectory) {
                if (r.equals(this.rule)) {
                    nextId++;
                }
            }
            return nextId;
        }

    }

    private ConcurrentHashMap<Thread, HashMap<BatchTransformationRule<?, ?>, IdProvider>> idProviders = new ConcurrentHashMap<>();
    private AtomicInteger fallBackId = new AtomicInteger();

    public int getId(BatchTransformationRule<?, ?> rule) {
        Thread currentThread = Thread.currentThread();
        HashMap<BatchTransformationRule<?, ?>, IdProvider> ruleMap = idProviders.get(currentThread);
        if (ruleMap == null) {
            return fallBackId.getAndIncrement();
        }
        IdProvider idProvider = ruleMap.get(rule);
        return idProvider.getId();
    }

    public void registerRules(ThreadContext context) {
        Thread currentThread = Thread.currentThread();
        HashMap<BatchTransformationRule<?, ?>, IdProvider> ruleMap = new HashMap<>();
        for (BatchTransformationRule<?, ?> rule : context.getGlobalContext().getTransformations()) {
            IdProvider idProvider = new IdProvider(context, rule);
            ruleMap.put(rule, idProvider);
        }
        idProviders.put(currentThread, ruleMap);
    }

    public void disposeByThread() {
        Thread currentThread = Thread.currentThread();
        idProviders.remove(currentThread);
    }

    public void resetFallBackId() {
        fallBackId.set(0);
    }
}
