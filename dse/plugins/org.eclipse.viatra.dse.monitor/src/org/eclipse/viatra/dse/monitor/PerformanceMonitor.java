/*******************************************************************************
 * Copyright (c) 2010-2014, Miklos Foldenyi, Andras Szabolcs Nagy, Abel Hegedus, Akos Horvath, Zoltan Ujhelyi and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 *   Miklos Foldenyi - initial API and implementation
 *   Andras Szabolcs Nagy - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.dse.monitor;

import java.util.concurrent.atomic.AtomicLong;

public class PerformanceMonitor {
    private String name;

    private AtomicLong totalTime = new AtomicLong(0);
    private AtomicLong totalCount = new AtomicLong(0);

    private static final String prefix = "____________________________________________________________________";

    public PerformanceMonitor(String name) {
        this.name = name;
        PerformanceMonitorManager.getInstance().register(this);
    }

    public String getName() {
        return name;
    }

    public void addMeasurement(long interval) {
        totalTime.addAndGet(interval);
        totalCount.addAndGet(1);
    }

    private final long division = 1000;

    public String report() {
        return "-" + name + prefix.substring(name.length()) + " Avg: (microsec) "
                + ((totalTime.get() / totalCount.get()) / division) + "\tCount:\t" + totalCount.get();
    }
}
