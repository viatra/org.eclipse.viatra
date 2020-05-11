/*******************************************************************************
 * Copyright (c) 2010-2018, Zoltan Ujhelyi, IncQuery Labs Ltd.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.query.runtime.cps.tests.api;

import static org.junit.Assert.assertEquals;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.viatra.query.runtime.api.AdvancedViatraQueryEngine;
import org.eclipse.viatra.query.runtime.api.IMatchUpdateListener;
import org.eclipse.viatra.query.runtime.api.ViatraQueryEngine;
import org.eclipse.viatra.query.runtime.cps.tests.queries.ApplicationTypesMatch;
import org.eclipse.viatra.query.runtime.cps.tests.queries.ApplicationTypesMatcher;
import org.eclipse.viatra.query.runtime.cps.tests.queries.HostTypesMatcher;
import org.eclipse.viatra.query.runtime.emf.EMFScope;
import org.junit.Before;
import org.junit.Test;

public class QueryEngineAPITest {
    
    private final class CounterListener implements IMatchUpdateListener<ApplicationTypesMatch> {
        int appearCount = 0;
        int disappearCount = 0;

        @Override
        public void notifyAppearance(ApplicationTypesMatch match) {
            appearCount++;
        }

        @Override
        public void notifyDisappearance(ApplicationTypesMatch match) {
            disappearCount++;                
        }
    }


    AdvancedViatraQueryEngine engine;
    
    @Before
    public void initializeEngine() {
        ResourceSet rs = new ResourceSetImpl();
        rs.getResource(URI.createPlatformPluginURI("org.eclipse.viatra.query.runtime.cps.tests/models/instances/demo.cyberphysicalsystem", false), true);
        engine = AdvancedViatraQueryEngine.from(ViatraQueryEngine.on(new EMFScope(rs)));
    }
    
    
    /**
     * Test case for bug https://bugs.eclipse.org/bugs/show_bug.cgi?id=562369
     */
    @Test
    public void testListenerAddition1() {
        final CounterListener listener = new CounterListener();
        final ApplicationTypesMatcher matcher = ApplicationTypesMatcher.on(engine);
        try {
            engine.addMatchUpdateListener(matcher, listener, true);
            
            int appearCount = listener.appearCount;
            int disappearCount = listener.disappearCount;
            ApplicationTypesMatcher.on(engine);
            
            assertEquals("Late appearance count incorrect", 2, listener.appearCount);
            assertEquals("Late disappearance count incorrect", 0, listener.disappearCount);
            assertEquals("Early appearance count incorrect", 2, appearCount);
            assertEquals("Early disappearance count incorrect", 0, disappearCount);
        } finally {
            engine.removeMatchUpdateListener(matcher, listener);
        }

    }
    
    /**
     * Test case for bug https://bugs.eclipse.org/bugs/show_bug.cgi?id=562369
     */
    @Test
    public void testListenerAddition2() {
        final CounterListener listener = new CounterListener();
        final ApplicationTypesMatcher matcher = ApplicationTypesMatcher.on(engine);
        try {
            engine.addMatchUpdateListener(matcher, listener, true);
            
            int appearCount = listener.appearCount;
            int disappearCount = listener.disappearCount;
            
            matcher.countMatches();
            
            assertEquals("Late appearance count incorrect", 2, listener.appearCount);
            assertEquals("Late disappearance count incorrect", 0, listener.disappearCount);
            assertEquals("Early appearance count incorrect", 2, appearCount);
            assertEquals("Early disappearance count incorrect", 0, disappearCount);
        } finally {
            engine.removeMatchUpdateListener(matcher, listener);
        }
    }
    
    /**
     * Test case for bug https://bugs.eclipse.org/bugs/show_bug.cgi?id=562369
     */
    @Test
    public void testListenerAddition3() {
        final CounterListener listener = new CounterListener();
        final ApplicationTypesMatcher matcher = ApplicationTypesMatcher.on(engine);
        try {
            engine.addMatchUpdateListener(matcher, listener, true);
            
            int appearCount = listener.appearCount;
            int disappearCount = listener.disappearCount;
            
            HostTypesMatcher.on(engine);
            
            assertEquals("Late appearance count incorrect", 2, listener.appearCount);
            assertEquals("Late disappearance count incorrect", 0, listener.disappearCount);
            assertEquals("Early appearance count incorrect", 2, appearCount);
            assertEquals("Early disappearance count incorrect", 0, disappearCount);
        } finally {
            engine.removeMatchUpdateListener(matcher, listener);
        }
    }
    
}
