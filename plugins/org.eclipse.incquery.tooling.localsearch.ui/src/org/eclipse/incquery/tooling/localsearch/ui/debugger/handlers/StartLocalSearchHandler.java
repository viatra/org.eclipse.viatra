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
package org.eclipse.incquery.tooling.localsearch.ui.debugger.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.incquery.runtime.localsearch.matcher.LocalSearchMatcher;
import org.eclipse.incquery.tooling.localsearch.ui.debugger.LocalSearchDebugger;

//import columbus.examples.localsearch.tests.matchers.PreparedIndexes;
//import columbus.examples.localsearch.tests.matchers.stringcomparewithoutequals.StringCompareWithoutEqualsLSMatcher;
//import columbus.java.asg.util.LJSIResourceFactory;

/**
 * 
 * @author Marton Bur
 *
 * This class is only for testing and introductory purposes and should be replaced soon.
 *
 */
public class StartLocalSearchHandler extends AbstractHandler {

    public static Thread planExecutorThread = null;

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {

        // Create and start the matcher thread
        Runnable planExecutor = new Runnable() {
            @Override
            public void run() {
                try {
                    // TODO set matcher here for testing
                    LocalSearchMatcher specializedLSMatcher = null;

                    LocalSearchDebugger debugger = new LocalSearchDebugger();
                    specializedLSMatcher.addAdapter(debugger);

                    // Do the matching here
                    specializedLSMatcher.getAllMatches();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        if (planExecutorThread == null || !planExecutorThread.isAlive()) {
            // Start the matching process if not started or in progress, yet
            planExecutorThread = new Thread(planExecutor);
            planExecutorThread.start();
        }
        
        return null;
    }

}
