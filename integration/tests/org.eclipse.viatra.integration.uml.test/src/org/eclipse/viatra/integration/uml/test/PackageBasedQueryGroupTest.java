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
package org.eclipse.viatra.integration.uml.test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Set;

import org.eclipse.viatra.integration.uml.derivedfeatures.DerivedFeatures;
import org.eclipse.viatra.query.runtime.api.IPatternMatch;
import org.eclipse.viatra.query.runtime.api.IQuerySpecification;
import org.eclipse.viatra.query.runtime.api.PackageBasedQueryGroup;
import org.eclipse.viatra.query.runtime.api.ViatraQueryMatcher;
import org.eclipse.viatra.query.runtime.exception.ViatraQueryException;
import org.eclipse.viatra.query.runtime.extensibility.IQuerySpecificationProvider;
import org.eclipse.viatra.query.runtime.registry.QuerySpecificationRegistry;
import org.eclipse.viatra.query.runtime.registry.connector.SpecificationMapSourceConnector;
import org.junit.Test;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;

/**
 * 
 * @author Abel Hegedus
 *
 */
public class PackageBasedQueryGroupTest {

    @Test
    public void simpleTest() throws ViatraQueryException {
        PackageBasedQueryGroup packageBasedQueryGroup = new PackageBasedQueryGroup("org.eclipse.viatra.integration.uml.derivedfeatures");
        Set<IQuerySpecification<?>> specifications = packageBasedQueryGroup.getSpecifications();
        assertFalse(specifications.isEmpty());
        assertEquals(specifications.size(), DerivedFeatures.instance().getSpecifications().size());
        
        IQuerySpecification<?> specification = specifications.iterator().next();
        assertTrue(specification.getFullyQualifiedName().contains("uml.derivedfeatures"));
    }
    
    @Test
    public void updateTest() throws ViatraQueryException {
        SpecificationMapSourceConnector connector = null;
        try {
            PackageBasedQueryGroup packageBasedQueryGroup = new PackageBasedQueryGroup("org.eclipse.viatra.integration.uml.derivedfeatures");
            Set<IQuerySpecification<?>> specifications = packageBasedQueryGroup.getSpecifications();
            assertFalse(specifications.isEmpty());
            
            IQuerySpecificationProvider mockedProvider = mock(IQuerySpecificationProvider.class);
            final String fqn = "org.eclipse.viatra.integration.uml.derivedfeatures.testQS";
            when(mockedProvider.getFullyQualifiedName()).thenReturn(fqn);
            @SuppressWarnings("unchecked")
            IQuerySpecification<ViatraQueryMatcher<? extends IPatternMatch>> mockedSpecification = mock(IQuerySpecification.class);
            when(mockedProvider.get()).thenReturn(mockedSpecification);
            when(mockedSpecification.getFullyQualifiedName()).thenReturn(fqn);
            connector = new SpecificationMapSourceConnector("umlSource", ImmutableSet.of(mockedProvider), true);
            QuerySpecificationRegistry.getInstance().addSource(connector);
            
            Set<IQuerySpecification<?>> specifications2 = packageBasedQueryGroup.getSpecifications();
            boolean fqnIncluded = Iterables.any(specifications2, new Predicate<IQuerySpecification<?>>() {
    
                @Override
                public boolean apply(IQuerySpecification<?> specification) {
                    return specification.getFullyQualifiedName().equals(fqn);
                }
            });
            assertTrue(fqnIncluded);
        } finally {
            if (connector != null) {
                QuerySpecificationRegistry.getInstance().removeSource(connector);
            }
        }
    }
    
    @Test
    public void subPackageTest() throws ViatraQueryException {
        PackageBasedQueryGroup subPackageGroup = new PackageBasedQueryGroup("org.eclipse.viatra.integration.uml", true);
        assertFalse(subPackageGroup.getSpecifications().isEmpty());
        
        assertEquals(DerivedFeatures.instance().getSpecifications().size(), subPackageGroup.getSpecifications().size());
        
        PackageBasedQueryGroup noSubPackageGroup = new PackageBasedQueryGroup("org.eclipse.viatra.integration.uml", false);
        assertTrue(noSubPackageGroup.getSpecifications().isEmpty());
    }

}
