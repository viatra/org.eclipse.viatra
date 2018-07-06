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
package org.eclipse.viatra.query.runtime.cps.tests.api;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Arrays;
import java.util.List;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.viatra.query.runtime.api.AdvancedViatraQueryEngine;
import org.eclipse.viatra.query.runtime.api.IPatternMatch;
import org.eclipse.viatra.query.runtime.api.IQuerySpecification;
import org.eclipse.viatra.query.runtime.api.ViatraQueryEngineOptions;
import org.eclipse.viatra.query.runtime.api.ViatraQueryMatcher;
import org.eclipse.viatra.query.runtime.cps.tests.queries.api.util.DefaultBackendQuerySpecification;
import org.eclipse.viatra.query.runtime.cps.tests.queries.api.util.IncrementalBackendQuerySpecification;
import org.eclipse.viatra.query.runtime.cps.tests.queries.api.util.SearchBackendQuerySpecification;
import org.eclipse.viatra.query.runtime.emf.EMFScope;
import org.eclipse.viatra.query.runtime.localsearch.matcher.integration.GenericLocalSearchResultProvider;
import org.eclipse.viatra.query.runtime.localsearch.matcher.integration.LocalSearchEMFBackendFactory;
import org.eclipse.viatra.query.runtime.localsearch.matcher.integration.LocalSearchGenericBackendFactory;
import org.eclipse.viatra.query.runtime.localsearch.matcher.integration.LocalSearchResultProvider;
import org.eclipse.viatra.query.runtime.rete.matcher.ReteBackendFactory;
import org.eclipse.viatra.query.runtime.rete.matcher.RetePatternMatcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class MatcherAccessTest {

    private static ViatraQueryEngineOptions.Builder options() {
        return new ViatraQueryEngineOptions.Builder();
    }
    
    private static final ViatraQueryEngineOptions ENGINE_DEFAULT = ViatraQueryEngineOptions.getDefault();
    private static final ViatraQueryEngineOptions ENGINE_RETE = options().withDefaultBackend(ReteBackendFactory.INSTANCE).build();
    private static final ViatraQueryEngineOptions ENGINE_LS = options().withDefaultBackend(LocalSearchEMFBackendFactory.INSTANCE).build();
    private static final ViatraQueryEngineOptions ENGINE_LS_GENERIC = options().withDefaultBackend(LocalSearchGenericBackendFactory.INSTANCE).build();
    
    private static final ViatraQueryEngineOptions ENGINE_RETE_LS = options()
            .withDefaultBackend(ReteBackendFactory.INSTANCE)
            .withDefaultCachingBackend(ReteBackendFactory.INSTANCE)
            .withDefaultSearchBackend(LocalSearchEMFBackendFactory.INSTANCE)
            .build();
    private static final ViatraQueryEngineOptions ENGINE_RETE_GENERIC_LS = options()
            .withDefaultBackend(ReteBackendFactory.INSTANCE)
            .withDefaultCachingBackend(ReteBackendFactory.INSTANCE)
            .withDefaultSearchBackend(LocalSearchGenericBackendFactory.INSTANCE)
            .build();
    private static final ViatraQueryEngineOptions ENGINE_LS_RETE = options()
            .withDefaultBackend(LocalSearchEMFBackendFactory.INSTANCE)
            .withDefaultCachingBackend(ReteBackendFactory.INSTANCE)
            .withDefaultSearchBackend(LocalSearchEMFBackendFactory.INSTANCE)
            .build();
    
    
    private static final IQuerySpecification<?> QUERY_DEFAULT = DefaultBackendQuerySpecification.instance();
    private static final IQuerySpecification<?> QUERY_CACHING = IncrementalBackendQuerySpecification.instance();
    private static final IQuerySpecification<?> QUERY_SEARCH = SearchBackendQuerySpecification.instance();
    
    @Parameters(name = "{0} - {1}")
    public static List<Object[]> testData() {
        
        return Arrays.<Object[]>asList(
                // No query hint
                new Object[] {"DEFAULT", QUERY_DEFAULT, ENGINE_DEFAULT, RetePatternMatcher.class},
                new Object[] {"RETE", QUERY_DEFAULT, ENGINE_RETE, RetePatternMatcher.class},
                new Object[] {"LS", QUERY_DEFAULT, ENGINE_LS, LocalSearchResultProvider.class},
                new Object[] {"LS-Generic", QUERY_DEFAULT, ENGINE_LS_GENERIC, GenericLocalSearchResultProvider.class},
                new Object[] {"LS-Caching", QUERY_DEFAULT, ENGINE_RETE_LS, RetePatternMatcher.class},
                new Object[] {"LS-Caching", QUERY_DEFAULT, ENGINE_RETE_GENERIC_LS, RetePatternMatcher.class},
                new Object[] {"Rete-Caching", QUERY_DEFAULT, ENGINE_LS_RETE, LocalSearchResultProvider.class},
                // Caching query requirement
                new Object[] {"DEFAULT", QUERY_CACHING, ENGINE_DEFAULT, RetePatternMatcher.class},
                new Object[] {"RETE", QUERY_CACHING, ENGINE_RETE, RetePatternMatcher.class},
                new Object[] {"LS", QUERY_CACHING, ENGINE_LS, RetePatternMatcher.class},
                new Object[] {"LS-Generic", QUERY_CACHING, ENGINE_LS_GENERIC, RetePatternMatcher.class},
                new Object[] {"LS-Caching", QUERY_CACHING, ENGINE_RETE_LS, RetePatternMatcher.class},
                new Object[] {"LS-Caching", QUERY_CACHING, ENGINE_RETE_GENERIC_LS, RetePatternMatcher.class},
                new Object[] {"Rete-Caching", QUERY_CACHING, ENGINE_LS_RETE, RetePatternMatcher.class},
                // Search query requirement
                new Object[] {"DEFAULT", QUERY_SEARCH, ENGINE_DEFAULT, LocalSearchResultProvider.class},
                new Object[] {"RETE", QUERY_SEARCH, ENGINE_RETE, LocalSearchResultProvider.class},
                new Object[] {"LS", QUERY_SEARCH, ENGINE_LS, LocalSearchResultProvider.class},
                new Object[] {"LS-Generic", QUERY_SEARCH, ENGINE_LS_GENERIC, GenericLocalSearchResultProvider.class},
                new Object[] {"LS-Caching", QUERY_SEARCH, ENGINE_RETE_LS, LocalSearchResultProvider.class},
                new Object[] {"LS-Caching", QUERY_SEARCH, ENGINE_RETE_GENERIC_LS, GenericLocalSearchResultProvider.class},
                new Object[] {"Rete-Caching", QUERY_SEARCH, ENGINE_LS_RETE, LocalSearchResultProvider.class}
                );
    }
    
    @Parameter(0)
    public String name;
    @Parameter(1)
    public IQuerySpecification<?> querySpecification;
    @Parameter(2)
    public ViatraQueryEngineOptions options;
    @Parameter(3)
    public Class<?> expectedResultProviderClass;
                
    AdvancedViatraQueryEngine engine;
                
    @Before
    public void initializeEngine() {
        ResourceSet rs = new ResourceSetImpl();
        rs.getResource(URI.createPlatformPluginURI("org.eclipse.viatra.query.runtime.cps.tests/models/instances/demo.cyberphysicalsystem", false), true);
        engine = AdvancedViatraQueryEngine.createUnmanagedEngine(new EMFScope(rs), options);
    }
    
    @After
    public void disposeEngine() {
        engine.dispose();
    }
    
    @Test
    public void testExpectedBackendClass() {
        final ViatraQueryMatcher<? extends IPatternMatch> matcher = engine.getMatcher(querySpecification);
        assertThat(engine.getResultProviderOfMatcher(matcher), instanceOf(expectedResultProviderClass));
    }
}
