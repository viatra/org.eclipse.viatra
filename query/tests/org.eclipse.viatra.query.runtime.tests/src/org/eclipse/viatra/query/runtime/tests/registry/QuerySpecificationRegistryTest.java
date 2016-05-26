/*******************************************************************************
 * Copyright (c) 2010-2016, Abel Hegedus, IncQuery Labs Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Abel Hegedus - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.runtime.tests.registry;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Set;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.eclipse.viatra.query.runtime.api.IPatternMatch;
import org.eclipse.viatra.query.runtime.api.IQuerySpecification;
import org.eclipse.viatra.query.runtime.api.ViatraQueryMatcher;
import org.eclipse.viatra.query.runtime.extensibility.IQuerySpecificationProvider;
import org.eclipse.viatra.query.runtime.registry.IQuerySpecificationRegistry;
import org.eclipse.viatra.query.runtime.registry.IRegistrySourceConnector;
import org.eclipse.viatra.query.runtime.registry.IRegistryView;
import org.eclipse.viatra.query.runtime.registry.QuerySpecificationRegistry;
import org.eclipse.viatra.query.runtime.registry.connector.SpecificationMapSourceConnector;
import org.eclipse.viatra.query.runtime.util.ViatraQueryLoggingUtil;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

public class QuerySpecificationRegistryTest {

    @BeforeClass
    public static void setup() {
        Logger logger = ViatraQueryLoggingUtil.getLogger(IQuerySpecificationRegistry.class);
        logger.setLevel(Level.TRACE);
    }
    
    @Test
    public void emptyRegistryTest() {
        IQuerySpecificationRegistry registry = QuerySpecificationRegistry.getInstance();
        assertNotNull(registry);
    }
    
    @Test
    public void simpleConnectorTest() {
        
        IQuerySpecificationRegistry registry = QuerySpecificationRegistry.getInstance();
        IRegistrySourceConnector connector = new SpecificationMapSourceConnector("test1", false);
        boolean registerSource = registry.addSource(connector);
        assertTrue(registerSource);
        
        boolean removeSource = registry.removeSource(connector);
        assertTrue(removeSource);
        
    }
    
    @Test
    public void simpleViewTest() {
        IQuerySpecificationRegistry registry = QuerySpecificationRegistry.getInstance();
        IRegistryView aspect = registry.createView();
        assertNotNull(aspect);
        
        IQuerySpecificationRegistry registry2 = aspect.getRegistry();
        assertNotNull(registry2);
        
    }  
     
    @SuppressWarnings("unchecked")
    @Test
    public void querySpecificationTest() {
        IQuerySpecificationRegistry registry = QuerySpecificationRegistry.getInstance();
        
        SpecificationMapSourceConnector connector = new SpecificationMapSourceConnector("test2", true);
        IQuerySpecificationProvider mockedProvider = mock(IQuerySpecificationProvider.class);
        when(mockedProvider.getFullyQualifiedName()).thenReturn("testQS");
        IQuerySpecification<ViatraQueryMatcher<? extends IPatternMatch>> mockedSpecification = mock(IQuerySpecification.class);
        when(mockedProvider.get()).thenReturn(mockedSpecification);
        connector.addQuerySpecificationProvider(mockedProvider);
        
        boolean registerSource = registry.addSource(connector);
        assertTrue(registerSource);
        
        IRegistryView aspect = registry.createView();
        assertTrue(aspect.getQueryGroup().getSpecifications().contains(mockedSpecification));
        
        boolean removeSource = registry.removeSource(connector);
        assertTrue(removeSource);
    }
    
    @Ignore("No extensions registered during CI build")
    @Test
    public void extensionBasedRegistryTest() {
        IQuerySpecificationRegistry instance = QuerySpecificationRegistry.getInstance();
        IRegistryView view = instance.getDefaultView();
        Set<String> registeredFQNs = view.getQuerySpecificationFQNs();
        assertTrue("No registered query specifications!", registeredFQNs.size() > 0);
        
        IQuerySpecification<?> specification = view.getEntry(registeredFQNs.iterator().next()).get();
        assertNotNull(specification);
        
        boolean hasQueryRegisteredWithFQN = view.hasQuerySpecificationFQN(specification.getFullyQualifiedName());
        assertTrue(hasQueryRegisteredWithFQN);
        
    }
}
