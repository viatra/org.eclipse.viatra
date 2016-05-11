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

import java.util.Set;

import org.eclipse.viatra.integration.uml.derivedfeatures.DerivedFeatures;
import org.eclipse.viatra.query.runtime.api.IQuerySpecification;
import org.eclipse.viatra.query.runtime.api.PackageBasedQueryGroup;
import org.eclipse.viatra.query.runtime.exception.ViatraQueryException;
import org.junit.Test;

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
    public void subPackageTest() throws ViatraQueryException {
        PackageBasedQueryGroup subPackageGroup = new PackageBasedQueryGroup("org.eclipse.viatra.integration.uml", true);
        assertFalse(subPackageGroup.getSpecifications().isEmpty());
        
        assertEquals(DerivedFeatures.instance().getSpecifications().size(), subPackageGroup.getSpecifications().size());
        
        PackageBasedQueryGroup noSubPackageGroup = new PackageBasedQueryGroup("org.eclipse.viatra.integration.uml", false);
        assertTrue(noSubPackageGroup.getSpecifications().isEmpty());
    }

}
