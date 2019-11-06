/*******************************************************************************
 * Copyright (c) 2010-2019, Zoltan Ujhelyi, IncQuery Labs Ltd.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.transformation.evm.test;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.viatra.examples.cps.cyberPhysicalSystem.CyberPhysicalSystemPackage;
import org.eclipse.viatra.query.runtime.api.AdvancedViatraQueryEngine;
import org.eclipse.viatra.query.runtime.cps.tests.queries.ApplicationTypesMatch;
import org.eclipse.viatra.query.runtime.cps.tests.queries.util.ApplicationTypesQuerySpecification;
import org.eclipse.viatra.query.runtime.emf.EMFScope;
import org.eclipse.viatra.transformation.evm.api.ExecutionSchema;
import org.eclipse.viatra.transformation.evm.api.Job;
import org.eclipse.viatra.transformation.evm.api.RuleSpecification;
import org.eclipse.viatra.transformation.evm.specific.ExecutionSchemas;
import org.eclipse.viatra.transformation.evm.specific.Jobs;
import org.eclipse.viatra.transformation.evm.specific.Lifecycles;
import org.eclipse.viatra.transformation.evm.specific.Rules;
import org.eclipse.viatra.transformation.evm.specific.Schedulers;
import org.eclipse.viatra.transformation.evm.specific.crud.CRUDActivationStateEnum;
import org.junit.BeforeClass;
import org.junit.Test;

public class ViatraQueryEventHandlerTest {
    
    @BeforeClass
    public static void initializeTest() {
        // This is required to allow the test to execute outside OSGi
        CyberPhysicalSystemPackage.eINSTANCE.getNsURI();
    }

    private AdvancedViatraQueryEngine initializeQueryEngine(boolean initializeContents) {
        ResourceSet set = new ResourceSetImpl();
        if (initializeContents) {
            set.getResource(URI.createPlatformPluginURI(
                    "org.eclipse.viatra.query.runtime.cps.tests/models/instances/demo.cyberphysicalsystem", true),
                    true);
        }
        EMFScope scope = new EMFScope(set);
        return AdvancedViatraQueryEngine.createUnmanagedEngine(scope);
    }
    
    private ExecutionSchema initializeSchema(AdvancedViatraQueryEngine queryEngine, boolean useUpdate,
            Set<Job<ApplicationTypesMatch>> jobs) {
        ExecutionSchema schema = ExecutionSchemas.createViatraQueryExecutionSchema(queryEngine,
                Schedulers.getQueryEngineSchedulerFactory(queryEngine));
        final RuleSpecification<ApplicationTypesMatch> rule = Rules.newMatcherRuleSpecification(ApplicationTypesQuerySpecification.instance(),
                Lifecycles.getDefault(useUpdate, false), jobs);
        
        schema.addRule(rule);
        return schema;
    }
    
    /**
     * This test initializes a simple rule with an update job over a model that has a match for the given job It is
     * expected that the rule is successfully initialized, but a bad ordering of attribute monitor initialization caused
     * a NPE Look at https://bugs.eclipse.org/bugs/show_bug.cgi?id=552027#c9 for details
     */
    @Test
    public void ruleInitializationTestWithUpdate() {
        
        AdvancedViatraQueryEngine queryEngine = initializeQueryEngine(true);
        Set<Job<ApplicationTypesMatch>> jobs = Stream
                .of(Jobs.<ApplicationTypesMatch> newStatelessJob(CRUDActivationStateEnum.CREATED, m -> {}),
                        Jobs.<ApplicationTypesMatch> newStatelessJob(CRUDActivationStateEnum.UPDATED, ApplicationTypesMatch::prettyPrint))
                .collect(Collectors.toSet());
        
        ExecutionSchema schema = initializeSchema(queryEngine, true, jobs);
        
        schema.dispose();
        queryEngine.dispose();
    }

    
    /**
     * This test initializes a simple rule with an update job over a model that has no match for the given job. This
     * case does not exhibit the NPE wrt thee bad ordering of attribute monitor initialization described in
     * https://bugs.eclipse.org/bugs/show_bug.cgi?id=552027#c9
     */
    @Test
    public void ruleInitializationTestWithUpdateWithoutContent() {
        
        AdvancedViatraQueryEngine queryEngine = initializeQueryEngine(false);
        Set<Job<ApplicationTypesMatch>> jobs = Stream
                .of(Jobs.<ApplicationTypesMatch> newStatelessJob(CRUDActivationStateEnum.CREATED, m -> {}),
                        Jobs.<ApplicationTypesMatch> newStatelessJob(CRUDActivationStateEnum.UPDATED, ApplicationTypesMatch::prettyPrint))
                .collect(Collectors.toSet());
        
        ExecutionSchema schema = initializeSchema(queryEngine, true, jobs);
        
        schema.dispose();
        queryEngine.dispose();
    }
    
    /**
     * This test initializes a simple rule without an update job over a model that has no match for the given job. This
     * case does not exhibit the NPE wrt thee bad ordering of attribute monitor initialization described in
     * https://bugs.eclipse.org/bugs/show_bug.cgi?id=552027#c9
     */
    @Test
    public void ruleInitializationTestWithoutUpdate() {
        
        AdvancedViatraQueryEngine queryEngine = initializeQueryEngine(true);
        Set<Job<ApplicationTypesMatch>> jobs = Stream
                .of(Jobs.<ApplicationTypesMatch> newStatelessJob(CRUDActivationStateEnum.CREATED, m -> {}))
                .collect(Collectors.toSet());
        
        ExecutionSchema schema = initializeSchema(queryEngine, false, jobs);
        
        schema.dispose();
        queryEngine.dispose();
    }
}
