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

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.google.common.base.Stopwatch;

public class PerformanceMonitorManager {
    private Map<String, PerformanceMonitor> monitors = new ConcurrentHashMap<String, PerformanceMonitor>();
    private static Map<String, Stopwatch> timers = new ConcurrentHashMap<String, Stopwatch>();

    private static PerformanceMonitorManager instance;

    private final Logger logger = Logger.getLogger(this.getClass());

    public static PerformanceMonitorManager getInstance() {
        if (instance == null) {
            instance = new PerformanceMonitorManager();
            instance.reset();
        }
        return instance;
    }

    public void register(PerformanceMonitor m) {
        monitors.put(m.getName(), m);
    }

    private PerformanceMonitor getMonitorByName(String name) {
        PerformanceMonitor m = monitors.get(name);
        if (m == null) {
            m = new PerformanceMonitor(name);
        }
        return m;
    }

    @SuppressWarnings("deprecation")
    public void reset() {
        monitors.clear();
        logger.info("");
        logger.info("");
        logger.info("");
        logger.info("================================================");
        logger.info("Manager initialized at " + new Date().toLocaleString());
    }

    @SuppressWarnings("deprecation")
    public void report() {
        for (PerformanceMonitor m : monitors.values()) {
            logger.info(m.report());
        }
        logger.info("Manager reported at " + new Date().toLocaleString());
    }

    public static void startTimer(String name) {
        Stopwatch stopwatch = Stopwatch.createUnstarted();
        timers.put(name + Thread.currentThread().getId(), stopwatch);
        stopwatch.start();
    }

    public static void endTimer(String name) {
        Stopwatch stopwatch = timers.get(name + Thread.currentThread().getId());
        if (stopwatch != null) {
            stopwatch.stop();
            addMeasurementToTimer(stopwatch.elapsedTime(TimeUnit.NANOSECONDS), name);
        }
    }

    private static void addMeasurementToTimer(long measurement, String timerName) {
        getInstance().getMonitorByName(timerName).addMeasurement(measurement);
    }
}
