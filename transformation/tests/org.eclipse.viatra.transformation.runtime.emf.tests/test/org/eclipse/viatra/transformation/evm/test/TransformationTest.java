/*******************************************************************************
 * Copyright (c) 2010-2018, Zoltan Ujhelyi, IncQuery Labs Ltd.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.transformation.evm.test;

import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Level;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.viatra.examples.cps.cyberPhysicalSystem.ApplicationInstance;
import org.eclipse.viatra.examples.cps.cyberPhysicalSystem.ApplicationType;
import org.eclipse.viatra.examples.cps.cyberPhysicalSystem.CyberPhysicalSystemFactory;
import org.eclipse.viatra.examples.cps.cyberPhysicalSystem.HostInstance;
import org.eclipse.viatra.query.runtime.api.AdvancedViatraQueryEngine;
import org.eclipse.viatra.query.runtime.api.ViatraQueryEngine;
import org.eclipse.viatra.query.runtime.cps.tests.queries.HostInstanceMatch;
import org.eclipse.viatra.query.runtime.emf.EMFScope;
import org.eclipse.viatra.query.runtime.util.ViatraQueryLoggingUtil;
import org.eclipse.viatra.query.testing.core.TestingSeverityAggregatorLogAppender;
import org.eclipse.viatra.transformation.evm.api.event.EventFilter;
import org.eclipse.viatra.transformation.evm.specific.event.ViatraQueryMatchEventFilter;
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
    public void matchParameterDefaultFilterTest() {
        ResourceSet rs = new ResourceSetImpl();
        Resource resource = rs.getResource(URI.createPlatformPluginURI("org.eclipse.viatra.query.runtime.cps.tests/models/instances/demo.cyberphysicalsystem", true), true);
        HostInstance instance0 = (HostInstance) resource.getEObject("simple.cps.host.FirstHostClass0.inst0");
        HostInstance instance1 = (HostInstance) resource.getEObject("simple.cps.host.FirstHostClass0.inst1");
        
        final EventFilter<HostInstanceMatch> filter0 = ViatraQueryMatchEventFilter.createFilter(HostInstanceMatch.newMatch(instance0)); 
        final EventFilter<HostInstanceMatch> filter1 = ViatraQueryMatchEventFilter.createFilter(HostInstanceMatch.newMatch(instance1));
        
        final BatchTransformationWithDefaultFilter transformationWrapper = new BatchTransformationWithDefaultFilter(resource, filter0);
        Assert.assertEquals(10, transformationWrapper.countMatchesNoFilter());
        Assert.assertEquals(1, transformationWrapper.countMatchesDefaultFilter());
        Assert.assertEquals(1, transformationWrapper.countMatchesOverriddenDefaultFilter(filter1));
        Assert.assertEquals(1, transformationWrapper.countMatchesOverriddenEmptyFilter(filter1));
    }
    
    @Test
    public void matchParameterDefaultFilterExtensionTest() {
        ResourceSet rs = new ResourceSetImpl();
        Resource resource = rs.getResource(URI.createPlatformPluginURI("org.eclipse.viatra.query.runtime.cps.tests/models/instances/demo.cyberphysicalsystem", true), true);
        HostInstance instance0 = (HostInstance) resource.getEObject("simple.cps.host.FirstHostClass0.inst0");
        HostInstance instance1 = (HostInstance) resource.getEObject("simple.cps.host.FirstHostClass0.inst1");
        
        final EventFilter<HostInstanceMatch> filter0 = ViatraQueryMatchEventFilter.createFilter(HostInstanceMatch.newMatch(instance0)); 
        final EventFilter<HostInstanceMatch> filter1 = ViatraQueryMatchEventFilter.createFilter(HostInstanceMatch.newMatch(instance1)); 
        
        final BatchTransformationWithDefaultFilter transformationWrapper = new BatchTransformationWithDefaultFilter(resource, filter0);
        
        Assert.assertEquals(0, transformationWrapper.countMatchesOverriddenEmptyFilter(filter1.and(transformationWrapper.getFilteredRule().getFilter())));
        Assert.assertEquals(1, transformationWrapper.countMatchesOverriddenEmptyFilter(filter1.and(transformationWrapper.getUnfilteredRule().getFilter())));
    }
    
    @Test
    public void transformationFiringStatusTest() {
        ResourceSet rs = new ResourceSetImpl();
        Resource resource = rs.getResource(URI.createPlatformPluginURI("org.eclipse.viatra.query.runtime.cps.tests/models/instances/demo.cyberphysicalsystem", true), true);
        HostInstance instance0 = (HostInstance) resource.getEObject("simple.cps.host.FirstHostClass0.inst0");
        HostInstance instance1 = (HostInstance) resource.getEObject("simple.cps.host.FirstHostClass0.inst1");
        
        final EventFilter<HostInstanceMatch> filter0 = ViatraQueryMatchEventFilter.createFilter(HostInstanceMatch.newMatch(instance0)); 
        final EventFilter<HostInstanceMatch> filter1 = ViatraQueryMatchEventFilter.createFilter(HostInstanceMatch.newMatch(instance1)); 
        
        final BatchTransformationWithFiringStatus transformationWrapper = new BatchTransformationWithFiringStatus(resource, filter0);
        Assert.assertTrue(transformationWrapper.canTransformationExecute());
        Assert.assertTrue(transformationWrapper.canFireFiltered());
        Assert.assertTrue(transformationWrapper.canFireUnfiltered());
        Assert.assertFalse(transformationWrapper.canFireFiltered(filter1.and(transformationWrapper.getFilteredRule().getFilter())));
        
        transformationWrapper.fireFiltered();
        Assert.assertTrue(transformationWrapper.canTransformationExecute());
        Assert.assertFalse(transformationWrapper.canFireFiltered());
        Assert.assertTrue(transformationWrapper.canFireUnfiltered());
        
        transformationWrapper.fireUnfiltered();
        Assert.assertFalse(transformationWrapper.canTransformationExecute());
        Assert.assertFalse(transformationWrapper.canFireFiltered());
        Assert.assertFalse(transformationWrapper.canFireUnfiltered());
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
    public void setBasedFilterTest() {
        ResourceSet rs = new ResourceSetImpl();
        Resource resource = rs.getResource(URI.createPlatformPluginURI("org.eclipse.viatra.query.runtime.cps.tests/models/instances/demo.cyberphysicalsystem", true), true);
        Set<HostInstance> instances = new HashSet<>();
        instances.add((HostInstance) resource.getEObject("simple.cps.host.FirstHostClass0.inst0"));
        instances.add((HostInstance) resource.getEObject("simple.cps.host.FirstHostClass0.inst1"));
        
        Assert.assertEquals(2, new BatchTransformationWithFilterJava(resource).callCastTypeWithPredicateFilterRule(instances));
        Assert.assertEquals(2, new BatchTransformationWithFilterJava(resource).callTypeInferredWithPredicateFilterRule(instances));
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
    
    @Test
    public void inOrderTransformationDisposalTest() {
        ResourceSet rs = new ResourceSetImpl();
        rs.createResource(URI.createURI("__dummy.cyberphysicalsystem", true));

        AdvancedViatraQueryEngine engine = AdvancedViatraQueryEngine.createUnmanagedEngine(new EMFScope(rs));
        final TestingSeverityAggregatorLogAppender logAppender = new TestingSeverityAggregatorLogAppender();
        ViatraQueryLoggingUtil.getLogger(ViatraQueryEngine.class).addAppender(logAppender);
        
        
        final EventDrivenTransformationWithPrioritizedRules transformation = new EventDrivenTransformationWithPrioritizedRules(
                engine);

        transformation.dispose();
        engine.dispose();
        
        Assert.assertFalse("Unexpected engine log error", logAppender.getSeverity().isGreaterOrEqual(Level.ERROR));
    }
    
    @Test
    public void outOfOrderTransformationDisposalTest() {
        ResourceSet rs = new ResourceSetImpl();
        rs.createResource(URI.createURI("__dummy.cyberphysicalsystem", true));

        AdvancedViatraQueryEngine engine = AdvancedViatraQueryEngine.createUnmanagedEngine(new EMFScope(rs));
        final TestingSeverityAggregatorLogAppender logAppender = new TestingSeverityAggregatorLogAppender();
        ViatraQueryLoggingUtil.getLogger(ViatraQueryEngine.class).addAppender(logAppender);

        final EventDrivenTransformationWithPrioritizedRules transformation = new EventDrivenTransformationWithPrioritizedRules(
                engine);

        engine.dispose();
        transformation.dispose();
        Assert.assertFalse("Unexpected engine log error", logAppender.getSeverity().isGreaterOrEqual(Level.ERROR));
    }
}
