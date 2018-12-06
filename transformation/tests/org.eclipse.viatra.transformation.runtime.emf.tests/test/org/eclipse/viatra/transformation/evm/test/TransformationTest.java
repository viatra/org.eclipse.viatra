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
package org.eclipse.viatra.transformation.evm.test;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.viatra.examples.cps.cyberPhysicalSystem.ApplicationInstance;
import org.eclipse.viatra.examples.cps.cyberPhysicalSystem.ApplicationType;
import org.eclipse.viatra.examples.cps.cyberPhysicalSystem.CyberPhysicalSystemFactory;
import org.eclipse.viatra.examples.cps.cyberPhysicalSystem.HostInstance;
import org.junit.Assert;
import org.junit.Test;

public class TransformationTest {
    
    @Test
    public void conflictingActivations() {
        ResourceSet rs = new ResourceSetImpl();
        Resource resource = rs.getResource(URI.createPlatformPluginURI("org.eclipse.viatra.query.runtime.cps.tests/models/instances/demo.cyberphysicalsystem", true), true);
        new BatchTransformationWithConflictingRuleInstances(resource).executeAll();
    }
    
    @Test
    public void conflictingActivationsOneByOne() {
        ResourceSet rs = new ResourceSetImpl();
        Resource resource = rs.getResource(URI.createPlatformPluginURI("org.eclipse.viatra.query.runtime.cps.tests/models/instances/demo.cyberphysicalsystem", true), true);
        new BatchTransformationWithConflictingRuleInstances(resource).executeOneByOne();
    }
    
    @Test
    public void matchParameterFilterTest() {
        ResourceSet rs = new ResourceSetImpl();
        Resource resource = rs.getResource(URI.createPlatformPluginURI("org.eclipse.viatra.query.runtime.cps.tests/models/instances/demo.cyberphysicalsystem", true), true);
        HostInstance instance = (HostInstance) resource.getEObject("simple.cps.host.FirstHostClass0.inst0");
        
        Assert.assertEquals(1, new BatchTransformationWithFilter(resource).countMatches(instance));
        
    }
    
    @Test
    public void javaBasedTransformationTest() {
        ResourceSet rs = new ResourceSetImpl();
        Resource resource = rs.getResource(URI.createPlatformPluginURI("org.eclipse.viatra.query.runtime.cps.tests/models/instances/demo.cyberphysicalsystem", true), true);
        HostInstance instance = (HostInstance) resource.getEObject("simple.cps.host.FirstHostClass0.inst0");
        
        Assert.assertEquals(1, new BatchTransformationWithFilterJava(resource).callCastTypeRule(instance));
        Assert.assertEquals(1, new BatchTransformationWithFilterJava(resource).callTypeInferredRule(instance));
    }
    
    @Test
    public void invertedDisappearancePriorityConflictResolverTest() {
        ResourceSet rs = new ResourceSetImpl();
        Resource resource = rs.createResource(URI.createURI("__dummy.cyberphysicalsystem", true));
        
        ApplicationType type = CyberPhysicalSystemFactory.eINSTANCE.createApplicationType();
        type.setIdentifier("at");
        
        ApplicationInstance instance = CyberPhysicalSystemFactory.eINSTANCE.createApplicationInstance();
        instance.setIdentifier("ai");
        type.getInstances().add(instance);
        
        resource.getContents().add(type);
        
        final EventDrivenTransformationWithPrioritizedRules transformation = new EventDrivenTransformationWithPrioritizedRules(resource);
        String result = transformation.execute();
        
        Assert.assertEquals("atai", result);
    }
}
