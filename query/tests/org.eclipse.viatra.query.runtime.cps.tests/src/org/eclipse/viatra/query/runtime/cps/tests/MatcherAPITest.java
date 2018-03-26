/*******************************************************************************
 * Copyright (c) 2010-2018, Zoltan Ujhelyi, IncQuery Labs Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.runtime.cps.tests;

import static org.junit.Assert.assertEquals;

import java.util.stream.Collectors;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.viatra.examples.cps.cyberPhysicalSystem.ApplicationType;
import org.eclipse.viatra.query.runtime.api.ViatraQueryEngine;
import org.eclipse.viatra.query.runtime.cps.tests.queries.ApplicationInstancesIdentifiersMatch;
import org.eclipse.viatra.query.runtime.cps.tests.queries.ApplicationInstancesIdentifiersMatcher;
import org.eclipse.viatra.query.runtime.cps.tests.queries.ApplicationTypesMatch;
import org.eclipse.viatra.query.runtime.cps.tests.queries.ApplicationTypesMatcher;
import org.eclipse.viatra.query.runtime.cps.tests.queries.TransitionsOfApplicationTypeMatcher;
import org.eclipse.viatra.query.runtime.emf.EMFScope;
import org.junit.Before;
import org.junit.Test;

public class MatcherAPITest {
    
    ViatraQueryEngine engine;
    
    @Before
    public void initializeEngine() {
        ResourceSet rs = new ResourceSetImpl();
        rs.getResource(URI.createPlatformPluginURI("org.eclipse.viatra.query.runtime.cps.tests/models/instances/demo.cyberphysicalsystem", false), true);
        engine = ViatraQueryEngine.on(new EMFScope(rs));
    }
    
    @Test
    public void testOneArbitraryMatchSuccess() {
        ApplicationTypesMatcher.on(engine)
            .getOneArbitraryMatch()
            .orElseThrow(AssertionError::new);
    }
    
    @Test
    public void testOneArbitraryMatchNotFound() {
        String identifier = ApplicationInstancesIdentifiersMatcher.on(engine)
        .getOneArbitraryMatch("MISSING")
        .map(ApplicationInstancesIdentifiersMatch::getAIIdentifier)
        .orElse("MISSING_IDENTIFIER");
        assertEquals("MISSING_IDENTIFIER", identifier);
    }
    
    @Test
    public void testStreamMatches() {
        String applicationIdentifiers = ApplicationTypesMatcher.on(engine)
                .streamAllMatches()
                .map(ApplicationTypesMatch::getAT)
                // Calculate the identifier
                .map(ApplicationType::getIdentifier)
                // Sort the results by identifiers - required for keeping result string stable
                .sorted()
                //Provide a comma separated string of identifiers
                .collect(Collectors.joining(", "));
        assertEquals("simple.cps.app.FirstAppClass0, simple.cps.app.SecondAppClass0", applicationIdentifiers);
    }
    
    @Test
    public void testStreamValues1() {
        String applicationIdentifiers = ApplicationTypesMatcher.on(engine)
                .streamAllValuesOfAT()
                // Calculate the identifier
                .map(ApplicationType::getIdentifier)
                // Sort the results by identifiers - required for keeping result string stable
                .sorted()
                //Provide a comma separated string of identifiers
                .collect(Collectors.joining(", "));
        assertEquals("simple.cps.app.FirstAppClass0, simple.cps.app.SecondAppClass0", applicationIdentifiers);
    }
    
    @Test
    public void testStreamValues2() {
        String applicationIdentifiers = TransitionsOfApplicationTypeMatcher.on(engine)
          .streamAllValuesOfAT()
          // Calculate the identifier
          .map(ApplicationType::getIdentifier)
          // Normally, this should have been executed inside the query specification
          .distinct()
          // Sort the results by identifiers
          .sorted()
          //Provide a comma separated string of identifiers
          .collect(Collectors.joining(", "));
        assertEquals("simple.cps.app.FirstAppClass0, simple.cps.app.SecondAppClass0", applicationIdentifiers);
    }

}
