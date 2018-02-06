/*******************************************************************************
 * Copyright (c) 2010-2014, Marton Bur, Akos Horvath, Zoltan Ujhelyi, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Marton Bur - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.tooling.localsearch.ui.debugger.internal;

import java.util.Arrays;
import java.util.stream.Collectors;

import org.eclipse.viatra.query.runtime.localsearch.matcher.LocalSearchMatcher;
import org.eclipse.viatra.query.runtime.localsearch.matcher.integration.LocalSearchResultProvider;
import org.eclipse.viatra.query.tooling.localsearch.ui.debugger.LocalSearchDebugger;

public final class LocalSearchDebuggerRunner implements Runnable {
    private final LocalSearchDebugger debugger;
    private final Object[] adornment;
    private final LocalSearchResultProvider lsResultProvider;

    public LocalSearchDebuggerRunner(LocalSearchDebugger debugger, Object[] adornment,
            LocalSearchResultProvider lsResultProvider) {
        this.debugger = debugger;
        this.adornment = Arrays.copyOf(adornment, adornment.length);
        this.lsResultProvider = lsResultProvider;
    }

    @Override
    public void run() {
        final LocalSearchMatcher localSearchMatcher = lsResultProvider.newLocalSearchMatcher(adornment);
        debugger.setStartHandlerCalled(true);

        // Initiate the matching
        localSearchMatcher.streamMatches(new Object[0]).collect(Collectors.toSet());
    }
}